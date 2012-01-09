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
package org.apache.karaf.eik.ui;

import org.apache.karaf.eik.core.LogWrapper;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * The activator class controls the plug-in life cycle
 */
public class KarafUIPluginActivator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.apache.karaf.eik.ui";

    public static final String BUNDLE_OBJ_IMG = "bundle_obj";

    public static final String FEATURE_OBJ_IBM = "feature_obj";

    public static final String LOGO_16X16_IMG = "logo16";

    public static final String LOGO_32X32_IMG = "logo32";

    public static final String LOGO_64X64_IMG = "logo64";

    // The shared instance
    private static KarafUIPluginActivator plugin;

    private BundleContext bundleContext;

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static KarafUIPluginActivator getDefault() {
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

    /**
     * The constructor
     */
    public KarafUIPluginActivator() {
        super();
    }

    /**
     * Returns a service with the specified name or {@code null} if none.
     *
     * @param serviceName
     *            name of service
     * @return service object or {@code null} if none
     */
    public Object getService(final String serviceName) {
        final ServiceReference reference = bundleContext.getServiceReference(serviceName);
        if (reference == null) {
            return null;
        }

        final Object service = bundleContext.getService(reference);
        if (service != null) {
            bundleContext.ungetService(reference);
        }

        return service;
    }

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        bundleContext = context;
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        plugin = null;
        bundleContext = null;

        super.stop(context);
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
        registerImage(imageRegistry, BUNDLE_OBJ_IMG, "icons/obj16/bundle_obj.gif");
        registerImage(imageRegistry, FEATURE_OBJ_IBM, "icons/obj16/feature_obj.gif");
        registerImage(imageRegistry, "runtime_obj", "icons/obj16/runtime_obj.gif");
        registerImage(imageRegistry, "details_view", "icons/obj16/details_view.gif");
        registerImage(imageRegistry, LOGO_16X16_IMG, "icons/obj16/karaf-logo-16x16.png");
        registerImage(imageRegistry, LOGO_32X32_IMG, "icons/obj32/karaf-logo-32x32.png");
        registerImage(imageRegistry, LOGO_64X64_IMG, "icons/obj64/karaf-logo-64x64.png");
    }

    /**
     * Registers an {@link ImageDescriptor} with the {@link ImageRegistry}
     *
     * @param registry
     *            the instance of {@link ImageRegistry}
     * @param key
     *            the key to register the image under
     * @param imageUrl
     *            the URL, relative to the {@link ICON_ROOT_URL}, of the image
     *            to be registered
     */
    private void registerImage(final ImageRegistry registry, final String key, final String imageUrl) {
        final ImageDescriptor id = imageDescriptorFromPlugin(PLUGIN_ID, imageUrl);
        registry.put(key, id);
    }

}
