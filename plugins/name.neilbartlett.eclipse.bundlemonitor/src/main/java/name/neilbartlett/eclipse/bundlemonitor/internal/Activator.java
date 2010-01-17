/*
 * Copyright (c) 2008 Neil Bartlett
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Neil Bartlett - initial implementation
 *     Stephen Evanchik - Updated to use data provider services
 */
package name.neilbartlett.eclipse.bundlemonitor.internal;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Standard Eclipse-generated plug-in activator
 */
public class Activator extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "name.neilbartlett.eclipse.bundlemonitor";

    public static final String BUNDLE_OBJ_IMG = "bundle_obj";
    public static final String DELETE_EDIT_IMG = "delete_edit";

    private static Activator plugin;

    public static Activator getDefault() {
        return plugin;
    }

    private BundleContext context;

    // Root URL for icons
    private static URL ICON_ROOT_URL;

    public BundleContext getBundleContext() {
        return context;
    }

    @Override
    public void start(BundleContext context) throws Exception {
        this.context = context;
        super.start(context);
        plugin = this;

        final String pathSuffix = "icons/"; // $NON-NLS-1$
        ICON_ROOT_URL = getBundle().getEntry(pathSuffix);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
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
    private void registerImage(ImageRegistry registry, String key, String imageUrl) {

        try {
            final ImageDescriptor id = ImageDescriptor.createFromURL(new URL(ICON_ROOT_URL, imageUrl));

            registry.put(key, id);
        } catch (MalformedURLException e) {
        }
    }

    /**
     * Creates the {@link ImageRegistery} for this plugin. Images can be
     * retrieved using the static accessor method
     * {@link KarafUIPluginActivator#getImageDescriptor(String)}
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#createImageRegistry()
     */
    @Override
    protected ImageRegistry createImageRegistry() {
        final ImageRegistry imageRegistry = new ImageRegistry();

        registerImage(imageRegistry, BUNDLE_OBJ_IMG, "bundle_obj.gif"); //$NON-NLS-1$
        registerImage(imageRegistry, DELETE_EDIT_IMG, "delete_edit.gif"); //$NON-NLS-1$

        return imageRegistry;
    }
}
