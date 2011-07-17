/**
 * Copyright (c) 2009 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package info.evanchik.eclipse.karaf.ui;

import info.evanchik.eclipse.karaf.core.LogWrapper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.ops4j.pax.url.mvn.Handler;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * The activator class controls the plug-in life cycle
 */
public class KarafUIPluginActivator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "info.evanchik.eclipse.karaf.ui"; // $NON-NLS-1$

    public static final String BUNDLE_OBJ_IMG = "bundle_obj"; //$NON-NLS-1$

    public static final String FEATURE_OBJ_IBM = "feature_obj"; //$NON-NLS-1$

    public static final String LOGO_16X16_IMG = "logo16"; //$NON-NLS-1$

    public static final String LOGO_32X32_IMG = "logo32"; //$NON-NLS-1$

    public static final String LOGO_64X64_IMG = "logo64"; //$NON-NLS-1$

    // The shared instance
    private static KarafUIPluginActivator plugin;

    private BundleContext bundleContext;

    // Root URL for icons
    private static URL ICON_ROOT_URL;

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
     * A {@link Map} of all the {@link ImageDescriptor}S used by this plugin
     */
    private final Map<String, ImageDescriptor> IMAGE_DESCRIPTORS = new HashMap<String, ImageDescriptor>();

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

        new Handler();

        final String pathSuffix = "icons/"; // $NON-NLS-1$
        ICON_ROOT_URL = getBundle().getEntry(pathSuffix);
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
        registerImage(imageRegistry, BUNDLE_OBJ_IMG, "obj16/bundle_obj.gif"); //$NON-NLS-1$
        registerImage(imageRegistry, FEATURE_OBJ_IBM, "obj16/feature_obj.gif"); //$NON-NLS-1$
        registerImage(imageRegistry, LOGO_16X16_IMG, "obj16/felixLogo16x16.gif"); //$NON-NLS-1$
        registerImage(imageRegistry, LOGO_32X32_IMG, "obj32/felixLogo32x32.gif"); //$NON-NLS-1$
        registerImage(imageRegistry, LOGO_64X64_IMG, "obj64/felixLogo64x64.gif"); //$NON-NLS-1$
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

        try {
            final ImageDescriptor id = ImageDescriptor.createFromURL(new URL(ICON_ROOT_URL,
                            imageUrl));

            registry.put(key, id);

            // Store this as an ImageDescriptor for future use in Wizards
            IMAGE_DESCRIPTORS.put(key, id);
        } catch (final MalformedURLException e) {
            getLogger().error("Could not create image descriptor for: " + key + " -> " + imageUrl,
                            e);
        }
    }
}
