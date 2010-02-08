/**
 * Copyright (c) 2010 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package info.evanchik.smk.app;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.servicemix.kernel.main.Lock;
import org.apache.servicemix.kernel.main.SimpleFileLock;
import org.apache.servicemix.kernel.main.spi.MainService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.startlevel.StartLevel;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class MainApplication implements IApplication, MainService {

    /**
     * If a lock should be used before starting the runtime
     */
    public static final String PROPERTY_USE_LOCK = "servicemix.lock";

    /**
     * The lock implementation
     */
    public static final String PROPERTY_LOCK_CLASS = "servicemix.lock.class";

    public static final String PROPERTY_LOCK_DELAY = "servicemix.lock.delay";

    public static final String PROPERTY_LOCK_LEVEL = "servicemix.lock.level";

    public static final String PROPERTY_LOCK_CLASS_DEFAULT = SimpleFileLock.class.getName();

    private static final long SYSTEM_BUNDLE_ID = 0;

    private static final int LOCKING_START_LEVEL = 1;

    private String[] applicationArgs;

    private BundleContext bundleContext;

    private final AtomicBoolean exiting = new AtomicBoolean(false);

    private final AtomicInteger exitCode = new AtomicInteger();

    private Lock lock;

    private final int lockStartLevel = 1;

    private final int lockDelay = 1000;

    private ServiceRegistration mainService;

    public Object start(IApplicationContext context) throws Exception {
        this.bundleContext = Activator.getDefault().getBundle().getBundleContext();

        this.mainService =
            bundleContext.registerService(
                    MainService.class.getName(),
                    this,
                    null);

        final Object argsObject = context.getArguments().get("application.args");

        if (argsObject == null) {
            this.applicationArgs = new String[0];
        } else {
            this.applicationArgs = new String[0];
            // this.applicationArgs = (String[]) argsObject;
        }

        // Start lock monitor
        new Thread() {
            @Override
            public void run() {
                lock(new Properties());
            }
        }.start();

        return null;
    }

    public void stop() {
        mainService.unregister();
        exiting.set(true);

        try {
            unlock();
        } catch (Exception e) {

        }
    }

    public String[] getArgs() {
        return applicationArgs;
    }

    public int getExitCode() {
        return exitCode.get();
    }

    public void setExitCode(int arg0) {
        exitCode.set(arg0);
    }

    private void lock(Properties props) {
        try {
            if (Boolean.parseBoolean(props.getProperty(PROPERTY_USE_LOCK, "true"))) {
                final String clz = props.getProperty(PROPERTY_LOCK_CLASS, PROPERTY_LOCK_CLASS_DEFAULT);
                lock = (Lock) Class.forName(clz).getConstructor(Properties.class).newInstance(props);

                boolean lockLogged = false;

                for (;;) {
                    if (lock.lock()) {
                        if (lockLogged) {
                            System.out.println("Lock acquired.");
                        }

                        setStartLevel(LOCKING_START_LEVEL);

                        for (;;) {
                            if (!lock.isAlive()) {
                                break;
                            }
                            Thread.sleep(lockDelay);
                        }

                        if (bundleContext.getBundle(SYSTEM_BUNDLE_ID).getState() == Bundle.ACTIVE && !exiting.get()) {
                            System.out.println("Lost the lock, stopping this instance ...");
                            setStartLevel(lockStartLevel);
                        }
                        break;
                    } else if (!lockLogged) {
                        System.out.println("Waiting for the lock ...");
                        lockLogged = true;
                    }

                    Thread.sleep(lockDelay);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setStartLevel(int level) throws Exception {
        final ServiceReference[] refs =
            bundleContext.getServiceReferences(StartLevel.class.getName(), null);

        final StartLevel sl =
            (StartLevel) bundleContext.getService(refs[0]);

        sl.setStartLevel(level);
    }

    private void unlock() throws Exception {
        if (lock != null) {
            lock.release();
        }
    }
}
