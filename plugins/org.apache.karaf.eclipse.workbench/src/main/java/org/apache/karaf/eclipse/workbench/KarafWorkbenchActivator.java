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
package org.apache.karaf.eclipse.workbench;

import org.apache.karaf.eclipse.core.LogWrapper;
import org.apache.karaf.eclipse.ui.KarafUIPluginActivator;
import org.apache.karaf.eclipse.workbench.internal.RuntimeDataProviderManager;
import org.apache.karaf.eclipse.workbench.jmx.IJMXTransportRegistry;
import org.apache.karaf.eclipse.workbench.jmx.JMXServiceDescriptor;
import org.apache.karaf.eclipse.workbench.jmx.internal.JMXServiceManager;
import org.apache.karaf.eclipse.workbench.jmx.internal.JMXTransportRegistry;
import org.apache.karaf.eclipse.workbench.jmx.mbeans.MBeanProviderManager;
import org.apache.karaf.eclipse.workbench.provider.RuntimeDataProvider;
import org.apache.karaf.eclipse.workbench.provider.eclipse.EclipseRuntimeDataProvider;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public final class KarafWorkbenchActivator extends AbstractUIPlugin {

	public static final String BUNDLE_OBJ_IMG = "bundle_obj"; //$NON-NLS-1$

	public static final String JMX_CONNECTOR_PROVIDER_EXTENSION_ID = "jmxConnectorProvider"; //$NON-NLS-1$

    public static final String LOGO_16X16_IMG = "logo16"; //$NON-NLS-1$

	public static final String PLUGIN_ID = "org.apache.karaf.eclipse.workbench"; //$NON-NLS-1$

    public static final String SERVICE_IMG = "service"; //$NON-NLS-1$

    private static KarafWorkbenchActivator plugin;

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static KarafWorkbenchActivator getDefault() {
        return plugin;
    }

    /**
     * Getter for the {@link LogWrapper} object that makes logging much easier
     * on the caller.
     *
     * @return the {@link LogWrapper} instance
     */
    public static LogWrapper getLogger() {
        return new LogWrapper(getDefault().getLog(), PLUGIN_ID);
    }

    private EclipseRuntimeDataProvider eclipseWorkbenchDataProvider;

    private JMXServiceManager jmxServiceManager;

	private JMXTransportRegistry jmxTransportRegistry;

    private MBeanProviderManager mbeanProviderManager;

    private RuntimeDataProviderManager runtimeDataProviderManager;

    /**
     * Getter for the {@link WorkbenchServiceManager} of
     * {@link JMXServiceDescriptor}s. There is only one per plugin instance.
     *
     * @return the {@link WorkbenchServiceManager} of
     *         {@link JMXServiceDescriptor}s
     */
    public WorkbenchServiceManager<JMXServiceDescriptor> getJMXServiceManager() {
        return jmxServiceManager;
    }

    /**
     * Getter for the {@link IJMXTransportRegistry} implementation. There is
     * only on per plugin instance.
     *
     * @return the {@link IJMXTransportRegistry} instance
     */
    public IJMXTransportRegistry getJMXTransportRegistry() {
        return jmxTransportRegistry;
    }

    /**
     * Getter for the {@link WorkbenchServiceManager} for the
     * {@link MBeanProvider}s. There is only one per plugin instance.
     *
     * @return the {@link WorkbenchServiceManager} for the {@link MBeanProvider}
     *         s
     */
    public WorkbenchServiceManager<MBeanProvider> getMBeanProviderManager() {
        return mbeanProviderManager;
    }

    /**
     * Getter for the {@link WorkbenchServiceManager} for the
     * {@link RuntimeDataProvider}s. There is only one per plugin instance.
     *
     * @return the {@link WorkbenchServiceManager} for the
     *         {@link RuntimeDataProvider}s
     */
    public WorkbenchServiceManager<RuntimeDataProvider> getRuntimeDataProviderManager() {
        return runtimeDataProviderManager;
    }

	@Override
    public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

        jmxServiceManager = new JMXServiceManager();
        jmxTransportRegistry = new JMXTransportRegistry();
        mbeanProviderManager = new MBeanProviderManager();
        runtimeDataProviderManager = new RuntimeDataProviderManager();
        eclipseWorkbenchDataProvider = new EclipseRuntimeDataProvider(getBundle().getBundleContext());

        // The EclipseRuntimeDataProvider must be started
        // asynchronously with plugin activation because
        // it takes a long time
        final Job eclipseRuntimeDataProviderStarter = new Job("Eclipse Runtime Data Starter") {

            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                startEclipseRuntimeDataProvider();
                return Status.OK_STATUS;
            }
        };

        eclipseRuntimeDataProviderStarter.setSystem(true);
        eclipseRuntimeDataProviderStarter.schedule();
	}

	@Override
    public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);

        runtimeDataProviderManager.remove(eclipseWorkbenchDataProvider);

        jmxServiceManager = null;
        jmxTransportRegistry = null;
        mbeanProviderManager = null;

        runtimeDataProviderManager = null;
        eclipseWorkbenchDataProvider = null;
	}

    /**
     * Creates the {@link ImageRegistery} for this plugin. Images can be
     * retrieved using the static accessor method
     * {@link KarafUIPluginActivator#getImageDescriptor(String)}
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#createImageRegistry()
     */
    @Override
    protected void initializeImageRegistry(final ImageRegistry imageRegistry) {
        registerImage(imageRegistry, BUNDLE_OBJ_IMG, "icons/obj16/bundle_obj.gif"); //$NON-NLS-1$
        registerImage(imageRegistry, LOGO_16X16_IMG, "icons/obj16/felixLogo16x16.gif"); //$NON-NLS-1$
        registerImage(imageRegistry, "logo32", "icons/obj32/felixLogo32x32.gif"); //$NON-NLS-1$ $NON-NLS-2$
        registerImage(imageRegistry, "logo64", "icons/obj64/felixLogo64x64.gif"); //$NON-NLS-1$ $NON-NLS-2$
        registerImage(imageRegistry, SERVICE_IMG, "icons/obj16/generic_element.gif"); //$NON-NLS-1$
    }

    /**
     * Starts the {@link EclipseRuntimeDataProvider} for this plugin. The
     * {@code EclipseRuntimeDataProvider} is a singleton in this plugin.
     */
    private void startEclipseRuntimeDataProvider() {
        eclipseWorkbenchDataProvider.start();

        runtimeDataProviderManager.add(eclipseWorkbenchDataProvider);
    }

    /**
     * Registers an {@link ImageDescriptor} with the {@link ImageRegistry}
     *
     * @param registry
     *            the instance of {@link ImageRegistry}
     * @param key
     *            the key to register the image under
     * @param imageUrl
     *            the URL of the image to be registered
     */
    private void registerImage(final ImageRegistry registry, final String key, final String imageUrl) {
        final ImageDescriptor id = imageDescriptorFromPlugin(PLUGIN_ID, imageUrl);
        registry.put(key, id);
    }
}
