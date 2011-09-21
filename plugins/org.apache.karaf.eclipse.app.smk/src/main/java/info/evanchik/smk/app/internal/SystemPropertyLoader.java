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
package org.apache.karaf.eclipse.app.smk.internal;

import org.apache.karaf.eclipse.app.smk.PropertyUtils;

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
public final class SystemPropertyLoader {

    public static final String SERVICEMIX_BASE_PROP_KEY = "servicemix.base"; //$NON-NLS-1$

    public static final String SYSTEM_PROPERTIES_FILE = "system.properties"; //$NON-NLS-1$

    public static final SystemPropertyLoader instance =
        new SystemPropertyLoader();

    /**
     * The ServiceMix Kernel installation base directory. This is usually the
     * same as the ServiceMix Kernel home directory.
     */
    private final File servicemixBase;

    /**
     * The location of ServiceMix Kernel's configuration files
     */
    private final File servicemixConfigDirectory;

    /**
     *
     * @return
     */
    public static SystemPropertyLoader getInstance() {
        return instance;
    }

    /**
     * Constructor. Establishes the configuration directory relative to the
     * ServiceMix Kernel base installation directory.
     */
    private SystemPropertyLoader() {
        servicemixBase = new File(System.getProperty(SERVICEMIX_BASE_PROP_KEY));

        servicemixConfigDirectory = new File(servicemixBase, "etc"); //$NON-NLS-1$
    }

    /**
     *
     */
    public void loadSystemProperties() {

        final File systemProps = new File(servicemixConfigDirectory,
                SYSTEM_PROPERTIES_FILE);

        final Properties props = loadProperties(fileToURI(systemProps));

        // Interpolate any currently set system properties in to the Karaf
        // system properties
        PropertyUtils.interpolateVariables(props, null);

        for (Object o : props.keySet()) {
            final String key = (String) o;
            final String value = props.getProperty(key);

            System.setProperty(key, value);
        }
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
}
