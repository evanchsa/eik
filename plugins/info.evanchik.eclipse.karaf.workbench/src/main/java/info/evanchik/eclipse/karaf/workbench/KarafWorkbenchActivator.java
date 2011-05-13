package info.evanchik.eclipse.karaf.workbench;

import info.evanchik.eclipse.karaf.core.LogWrapper;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;
import info.evanchik.eclipse.karaf.workbench.jmx.IJMXServiceManager;
import info.evanchik.eclipse.karaf.workbench.jmx.IJMXTransportRegistry;
import info.evanchik.eclipse.karaf.workbench.jmx.internal.JMXServiceManager;
import info.evanchik.eclipse.karaf.workbench.jmx.internal.JMXTransportRegistry;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class KarafWorkbenchActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "info.evanchik.eclipse.karaf.workbench";

	public static final String JMX_CONNECTOR_PROVIDER_EXTENSION_ID = "jmxConnectorProvider";

    public static final String BUNDLE_OBJ_IMG = "bundle_obj"; //$NON-NLS-1$

    public static final String LOGO_16X16_IMG = "logo16"; //$NON-NLS-1$

    private JMXServiceManager jmxServiceManager;

    private JMXTransportRegistry jmxTransportRegistry;

	// The shared instance
	private static KarafWorkbenchActivator plugin;

    // Root URL for icons
    private static URL ICON_ROOT_URL;

    /**
     * A {@link Map} of all the {@link ImageDescriptor}S used by this plugin
     */
    private static final Map<String, ImageDescriptor> IMAGE_DESCRIPTORS =
        new HashMap<String, ImageDescriptor>();


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

	/**
	 * The constructor
	 */
	public KarafWorkbenchActivator() {
	}

	/**
     * Getter for the {@link IJMXServiceManager} implementation. There is only
     * one per plugin instance.
     *
     * @return the {@code IJMXServiceManager} instance
     */
    public IJMXServiceManager getJMXServiceManager() {
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

	@Override
    public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

        final String pathSuffix = "icons/"; // $NON-NLS-1$
        ICON_ROOT_URL = getBundle().getEntry(pathSuffix);

        jmxServiceManager = new JMXServiceManager();
        jmxTransportRegistry = new JMXTransportRegistry();
	}

	@Override
    public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);

        jmxServiceManager = null;
        jmxTransportRegistry = null;
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
        registerImage(imageRegistry, BUNDLE_OBJ_IMG, "obj16/bundle_obj.gif");
        registerImage(imageRegistry, LOGO_16X16_IMG, "obj16/felixLogo16x16.gif");
        registerImage(imageRegistry, "logo32", "obj32/felixLogo32x32.gif");
        registerImage(imageRegistry, "logo64", "obj64/felixLogo64x64.gif");
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
