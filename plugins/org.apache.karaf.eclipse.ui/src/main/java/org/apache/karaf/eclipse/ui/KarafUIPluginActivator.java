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
package org.apache.karaf.eclipse.ui;

import java.util.Map;

import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.core.KarafPlatformModelFactory;
import org.apache.karaf.eclipse.core.KarafPlatformModelRegistry;
import org.apache.karaf.eclipse.core.KarafPlatformValidator;
import org.apache.karaf.eclipse.core.LogWrapper;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * The activator class controls the plug-in life cycle
 */
public class KarafUIPluginActivator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.apache.karaf.eclipse.ui"; // $NON-NLS-1$

    public static final String BUNDLE_OBJ_IMG = "bundle_obj"; //$NON-NLS-1$

    public static final String FEATURE_OBJ_IBM = "feature_obj"; //$NON-NLS-1$

    public static final String LOGO_16X16_IMG = "logo16"; //$NON-NLS-1$

    public static final String LOGO_32X32_IMG = "logo32"; //$NON-NLS-1$

    public static final String LOGO_64X64_IMG = "logo64"; //$NON-NLS-1$

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
    *
    * @return
    * @throws CoreException
    */
   public static KarafPlatformModel findActivePlatformModel() throws CoreException {
       final Map<String, IConfigurationElement> triggerBundleMap =
           KarafPlatformModelRegistry.getTriggerBundlePlatformFactoryMap();

       for (final Map.Entry<String, IConfigurationElement> e : triggerBundleMap.entrySet()) {
           final String symbolicName = e.getKey();

           final IPluginModelBase karafPlatformPlugin = PluginRegistry.findModel(symbolicName);

           if (karafPlatformPlugin == null) {
               continue;
           }

           final IConfigurationElement c = e.getValue();
           final KarafPlatformModelFactory f =
               (KarafPlatformModelFactory)c.createExecutableExtension(KarafPlatformModelRegistry.ATT_CLASS);

           final KarafPlatformValidator validator = f.getPlatformValidator();

           IPath modelPath = new Path(karafPlatformPlugin.getInstallLocation()).removeLastSegments(1);
           while(!modelPath.isEmpty() && !modelPath.isRoot()) {
               modelPath = modelPath.removeLastSegments(1);

               if (validator.isValid(modelPath)) {
                   return f.getPlatformModel(modelPath);
               }
           }
       }

       return null;
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
        registerImage(imageRegistry, BUNDLE_OBJ_IMG, "icons/obj16/bundle_obj.gif"); //$NON-NLS-1$
        registerImage(imageRegistry, FEATURE_OBJ_IBM, "icons/obj16/feature_obj.gif"); //$NON-NLS-1$
        registerImage(imageRegistry, "runtime_obj", "icons/obj16/runtime_obj.gif"); //$NON-NLS-1$
        registerImage(imageRegistry, "details_view", "icons/obj16/details_view.gif"); //$NON-NLS-1$
        registerImage(imageRegistry, LOGO_16X16_IMG, "icons/obj16/karaf-logo-16x16.png"); //$NON-NLS-1$
        registerImage(imageRegistry, LOGO_32X32_IMG, "icons/obj32/karaf-logo-32x32.png"); //$NON-NLS-1$
        registerImage(imageRegistry, LOGO_64X64_IMG, "icons/obj64/karaf-logo-64x64.png"); //$NON-NLS-1$
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
