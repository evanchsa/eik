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
package info.evanchik.karaf.app;

import info.evanchik.karaf.app.internal.LockManager;
import info.evanchik.karaf.app.internal.SystemPropertyLoader;

import java.util.Properties;
import java.util.logging.Logger;

import org.apache.felix.karaf.main.BootstrapLogManager;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleContext;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class MainApplication implements IApplication {

    private static final Logger LOG = Logger.getLogger(MainApplication.class.getName());

    @SuppressWarnings("unused")
    private String[] applicationArgs;

    @SuppressWarnings("unused")
    private BundleContext bundleContext;

    public Object start(IApplicationContext context) throws Exception {
        System.out.println("Apache ServiceMix Kernel :: Starting main application");

        this.bundleContext = Activator.getDefault().getBundle().getBundleContext();

        final Object argsObject = context.getArguments().get("application.args");

        if (argsObject == null) {
            this.applicationArgs = new String[0];
        } else {
            this.applicationArgs = (String[]) argsObject;
        }

        BootstrapLogManager.setProperties(new Properties());
        LOG.addHandler(BootstrapLogManager.getDefaultHandler());

        SystemPropertyLoader.getInstance().loadSystemProperties();
        LockManager.getInstance().start();

        return null;
    }

    public void stop() {

        try {
            LockManager.getInstance().stop();
        } catch(Exception e) {

        }
    }


}
