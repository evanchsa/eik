/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
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
