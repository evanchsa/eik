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
package org.apache.karaf.eik.app.internal;

import org.apache.karaf.eik.app.PropertyUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.StringTokenizer;

public final class SystemPropertyLoader {

    public static final String INCLUDES_PROPERTY = "${includes}"; //$NON-NLS-1$

    public static final String KARAF_BASE_PROP_KEY = "karaf.base"; //$NON-NLS-1$

    public static final String SYSTEM_PROPERTIES_FILE = "system.properties"; //$NON-NLS-1$

    public static final SystemPropertyLoader instance =
        new SystemPropertyLoader();

    /**
     * The Karaf installation base directory. This is usually the
     * same as the Karaf home directory.
     */
    private final File karafBase;

    /**
     * The location of Karaf's configuration files
     */
    private final File karafConfigDirectory;

    /**
     *
     * @return
     */
    public static SystemPropertyLoader getInstance() {
        return instance;
    }

    /**
     * Constructor. Establishes the configuration directory relative to the
     * Karaf base installation directory.
     */
    private SystemPropertyLoader() {
        karafBase = new File(System.getProperty(KARAF_BASE_PROP_KEY));

        karafConfigDirectory = new File(karafBase, "etc"); //$NON-NLS-1$
    }

    /**
     *
     */
    public void loadSystemProperties() {

        final File systemProps = new File(karafConfigDirectory,
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

        final String includes = props.getProperty(INCLUDES_PROPERTY);
        if (includes != null) {
            final StringTokenizer st = new StringTokenizer(includes, "\" ", true);
            if (st.countTokens() > 0) {
                String location;
                do {
                    location = nextLocation(st);
                    if (location != null) {
                        try {
                            URL url = new URL(source, location);
                            Properties includeProps = loadProperties(url);
                            props.putAll(includeProps);
                        } catch (MalformedURLException e) {
                            // TODO: What to throw?
                        }
                    }
                } while (location != null);
            }
            props.remove(INCLUDES_PROPERTY);
        }

        return props;
    }

    private static String nextLocation(StringTokenizer st) {
        String retVal = null;

        if (st.countTokens() > 0) {
            String tokenList = "\" ";
            StringBuffer tokBuf = new StringBuffer(10);
            String tok = null;
            boolean inQuote = false;
            boolean tokStarted = false;
            boolean exit = false;
            while ((st.hasMoreTokens()) && (!exit)) {
                tok = st.nextToken(tokenList);
                if (tok.equals("\"")) {
                    inQuote = !inQuote;
                    if (inQuote) {
                        tokenList = "\"";
                    } else {
                        tokenList = "\" ";
                    }

                } else if (tok.equals(" ")) {
                    if (tokStarted) {
                        retVal = tokBuf.toString();
                        tokStarted = false;
                        tokBuf = new StringBuffer(10);
                        exit = true;
                    }
                } else {
                    tokStarted = true;
                    tokBuf.append(tok.trim());
                }
            }

            // Handle case where end of token stream and
            // still got data
            if ((!exit) && (tokStarted)) {
                retVal = tokBuf.toString();
            }
        }

        return retVal;
    }

}
