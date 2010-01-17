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
package info.evanchik.eclipse.karaf.hooks.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class SystemPropertyLoader {

    /**
     * The Karaf installation home directory
     */
    private final File karafHome;

    /**
     * The Karaf installation base directory. This is usually the same as the
     * Karaf home directory.
     */
    private final File karafBase;

    /**
     * The location of Karaf's configuration files
     */
    private final File karafConfigDirectory;

    /**
     * Constructor. Establishes the configuration directory relative to the
     * Karaf base installation directory.
     */
    public SystemPropertyLoader() {
        karafHome = KarafModel.getKarafHome();
        karafBase = KarafModel.getKarafBase(karafHome);

        karafConfigDirectory = new File(karafBase, "etc"); //$NON-NLS-1$
    }

    /**
     * Converts a {@link File} to a {@link URL}
     *
     * @param f
     *            the {@link File} to convert
     * @return a {@link URL} representation of the given {@link File}
     */
    private URL fileToURI(File f) {
        try {
            return f.toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Load a {@link Properties} object from a {@link URL}. This ignores
     * non-existent {@link URL}'s as well as other problems loading the
     * properties.
     *
     * @param source
     *            the {@link URL} source that the properties will be loaded from
     * @return a {@link Properties} object, possibly empty but always a valid
     *         object reference
     */
    private Properties loadProperties(URL source) {
        final Properties props = new Properties();
        InputStream in = null;

        try {
            in = source.openConnection().getInputStream();
            props.load(in);
            in.close();
        } catch (FileNotFoundException ignoreFileNotFoundException) {
            // Ignore file not found
        } catch (Exception e) {
            e.printStackTrace();

            // Attempt to close the input stream ignoring exceptions
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex2) {
                // Nothing to do
            }
        }

        return props;
    }

    /**
     * Loads the Karaf specified system properties found in {@code
     * KarafStarterService#KARAF_DEFAULT_SYSTEM_PROPERTIES_FILE} to the Java
     * system properties.
     */
    public void loadSystemProperties() {

        final File systemProps = new File(karafConfigDirectory,
                KarafModel.KARAF_DEFAULT_SYSTEM_PROPERTIES_FILE);

        final Properties props = loadProperties(fileToURI(systemProps));

        // Interpolate any currently set system properties in to the Karaf
        // system properties
        HookUtils.interpolateVariables(props, null);

        for (Object o : props.keySet()) {
            final String key = (String) o;
            final String value = props.getProperty(key);

            System.setProperty(key, value);
        }
    }
}
