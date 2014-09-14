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
package org.apache.karaf.eik.core;

import org.apache.karaf.eik.core.equinox.BundleEntry;
import org.apache.karaf.eik.core.internal.KarafCorePluginActivator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.collections.Predicate;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

public final class KarafCorePluginUtils {

    private static final String INCLUDES_PROPERTY = "${includes}";

    /**
     * Create a JVM system property argument (e.g -DpropertyName=propertyValue).
     *
     * @param name
     *            the name of the system property
     * @param value
     *            the value of the system property
     * @return the fully constructed system property string
     */
    public static String constructSystemProperty(final String name, final String value) {
        final StringBuilder sb = new StringBuilder("-D");
        sb.append(name);
        sb.append("=");
        sb.append(value);

        return sb.toString();
    }

    /**
     *
     * @param model
     * @return
     */
    public static boolean isKaraf(final KarafPlatformModel model) {
        if (model.getConfigurationFile(IKarafConstants.ORG_APACHE_KARAF_MANAGEMENT_CFG_FILENAME).toFile().exists()) {
            return true;
        } else if (model instanceof KarafWorkingPlatformModel) {
            final KarafWorkingPlatformModel workingModel = (KarafWorkingPlatformModel) model;
            return workingModel.getConfigurationFile(IKarafConstants.ORG_APACHE_KARAF_MANAGEMENT_CFG_FILENAME).toFile().exists();
        } else {
            return false;
        }
    }

    /**
     * Filter a list based on a {@link Predicate}
     *
     * @param <T>
     * @param target
     * @param predicate
     * @return
     */
    public static <T> List<T> filterList(final Collection<T> target, final Predicate predicate) {
        final List<T> result = new ArrayList<T>();
        for (final T element: target) {
            if (predicate.evaluate(element)) {
                result.add(element);
            }
        }
        return result;
    }

    /**
     * Helper method that returns the {@link Bundle}'s location based on its
     * symbolic name
     *
     * @param bundleSymbolicName
     *            the symbolic name of the {@code Bundle}
     * @return the location of the {@code Bundle} or {@code null}
     */
    public static String getBundleLocation(final String bundleSymbolicName) {
        final Bundle bundle = Platform.getBundle(bundleSymbolicName);
        if (bundle == null) {
            KarafCorePluginActivator.getLogger().error("Unable to locate bundle with symbolic name: " + bundleSymbolicName);
            return null;
        }

        try {
            return FileLocator.getBundleFile(bundle).getAbsolutePath();
        } catch(final IOException e) {
            KarafCorePluginActivator.getLogger().error("Unable to locate bundle with symbolic name: " + bundleSymbolicName, e);
            return null;
        }
    }

    /**
     * Construct a {@link List} of {@link BundleEntry} objects from the
     * specified string. The string is one typically found in the {@code
     * osgi.bundles} property of an Eclipse {@code config.ini}
     *
     * @param osgiBundles
     *            a string is typically found in the {@code osgi.bundles}
     *            property of an Eclipse {@code config.ini}
     * @return a {@code List} of {@code BundleEntry} objects
     */
    public static List<BundleEntry> getEquinoxBundles(final String osgiBundles) {
        final String[] bundles = osgiBundles.split(",");

        final List<BundleEntry> entries = new ArrayList<BundleEntry>();
        for (final String s : bundles) {
            entries.add(BundleEntry.fromString(s.trim()));
        }

        return entries;
    }

    /**
     * Searches a directory to the specified depth for library files.<br>
     * <br>
     * This method is recursive so be careful with the maximum depth
     *
     * @param dir
     *            the directory to being the search
     * @param list
     *            the list of libraries found
     * @param maxDepth
     *            the current maximum depth
     */
    public static void getJarFileList(final File dir, final List<File> list, final int maxDepth) {
    	getFileList(dir, ".jar", list, maxDepth);
    }

	/**
	 * Searches a directory to the specified depth for library files.<br>
	 * <br>
	 * This method is recursive so be careful with the maximum depth
	 *
	 * @param dir
	 *            the directory to being the search
	 * @param extension
	 *            the extension to search for
	 * @param list
	 *            the list of libraries found
	 * @param maxDepth
	 *            the current maximum depth
	 */
    public static void getFileList(final File dir, final String extension, final List<File> list, final int maxDepth) {
        if (dir == null) {
            throw new IllegalArgumentException("Directory must not be null");
        }

        final File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (final File file : files) {
            if (file.isDirectory() && maxDepth > 0) {
                getFileList(file, extension, list, maxDepth - 1);
            } else if (file.getAbsolutePath().endsWith(extension) || file.getAbsolutePath().endsWith(".zip")) {
                list.add(file);
            }
        }
    }

    /**
     * Implementation of join using a {@link StringBuffer}
     *
     * @param items
     *            the {@link Collection} of items that will be joined together
     * @param glue
     *            the string to act as glue in the concatenation
     * @return the concatenation of the specified items
     */
    public static String join(final Collection<? extends Object> items, final String glue) {
        final StringBuffer buffer = new StringBuffer();
        for (final Object o : items) {
            if (buffer.length() > 0) {
                buffer.append(glue);
            }

            buffer.append(o.toString());
        }

        return buffer.toString();
    }

    /**
     * Loads a configuration file relative to the specified base
     * directory
     *
     * @param base
     *            the directory containing the file
     * @param filename
     *            the relative path to the properties file
     * @return the {@link Properties} object created from the contents of
     *         configuration file
     * @throws CoreException
     *             if there is a problem loading the file
     */
    public static Properties loadProperties(final File base, final String filename) throws CoreException {
       return loadProperties(base, filename, false);
    }

    /**
     * Loads a configuration file relative to the specified base directory. This
     * method also processes any include directives that import other properties
     * files relative to the specified property file.
     *
     * @param base
     *            the directory containing the file
     * @param filename
     *            the relative path to the properties file
     * @param processIncludes
     *            true if {@link #INCLUDES_PROPERTY} statements should be
     *            followed; false otherwise.
     * @return the {@link Properties} object created from the contents of
     *         configuration file
     * @throws CoreException
     *             if there is a problem loading the file
     */
    public static Properties loadProperties(final File base, final String filename, final boolean processIncludes) throws CoreException {
        final File f = new File(base, filename);

        try {
            final Properties p = new Properties();
            p.load(new FileInputStream(f));

            if (processIncludes) {
                final String includes = p.getProperty(INCLUDES_PROPERTY);
                if (includes != null) {
                    final StringTokenizer st = new StringTokenizer(includes, "\" ", true);
                    if (st.countTokens() > 0) {
                        String location;
                        do {
                            location = nextLocation(st);
                            if (location != null) {
                                final Properties includeProps = loadProperties(base, location);
                                p.putAll(includeProps);
                            }
                        } while (location != null);
                    }
                    p.remove(INCLUDES_PROPERTY);
                }
            }

            return p;
        } catch (final IOException e) {
            final String message = "Unable to load configuration file from configuration directory: " + f.getAbsolutePath();
            throw new CoreException(new Status(IStatus.ERROR, KarafCorePluginActivator.PLUGIN_ID, IStatus.OK, message, e));
        }

    }

    /**
     * Saves a properties file
     *
     * @param file
     * @param properties
     */
    public static void save(final File file, final Properties properties) {
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            properties.store(stream, "Configuration File"); //$NON-NLS-1$
            stream.flush();
        } catch (final IOException e) {
            KarafCorePluginActivator.getLogger().error("Unable to store properties in: " + file.getAbsolutePath(), e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (final IOException e) {
                    // Intentionally left blank
                }
            }

        }
    }

    private KarafCorePluginUtils() {
        throw new AssertionError("Cannot instantiate " + KarafCorePluginUtils.class.getName());
    }

    private static String nextLocation(final StringTokenizer st) {
        String retVal = null;

        if (st.countTokens() > 0) {
            String tokenList = "\" ";
            StringBuffer tokBuf = new StringBuffer(10);
            String tok = null;
            boolean inQuote = false;
            boolean tokStarted = false;
            boolean exit = false;
            while (st.hasMoreTokens() && !exit) {
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
            if (!exit && tokStarted) {
                retVal = tokBuf.toString();
            }
        }

        return retVal;
    }
}
