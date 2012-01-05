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

import info.evanchik.smk.app.internal.LockManager;
import info.evanchik.smk.app.internal.SystemPropertyLoader;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.servicemix.kernel.main.spi.MainService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class MainApplication implements IApplication, MainService {

    private String[] applicationArgs;

    private BundleContext bundleContext;

    private final AtomicInteger exitCode = new AtomicInteger(0);

    private ServiceRegistration mainService;

    public Object start(IApplicationContext context) throws Exception {
        System.out.println("Apache ServiceMix Kernel :: Starting main application");

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
            this.applicationArgs = (String[]) argsObject;
        }

        SystemPropertyLoader.getInstance().loadSystemProperties();
        LockManager.getInstance().start();

        return null;
    }

    public void stop() {

        mainService.unregister();

        try {
            LockManager.getInstance().stop();
        } catch(Exception e) {

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
}
