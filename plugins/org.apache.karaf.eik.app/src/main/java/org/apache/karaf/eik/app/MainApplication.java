/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.karaf.eik.app;

import org.apache.karaf.eik.app.internal.LockManager;
import org.apache.karaf.eik.app.internal.SystemPropertyLoader;

import java.util.Properties;
import java.util.logging.Logger;

import org.apache.karaf.main.BootstrapLogManager;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleContext;

public class MainApplication implements IApplication {

    private static final Logger LOG = Logger.getLogger(MainApplication.class.getName());

    @SuppressWarnings("unused")
    private String[] applicationArgs;

    @SuppressWarnings("unused")
    private BundleContext bundleContext;

    public Object start(IApplicationContext context) throws Exception {
        System.out.println("Apache Karaf :: Starting main application");

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
