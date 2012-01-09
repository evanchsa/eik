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
package org.apache.karaf.eik.app;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Extracted from Karaf Main.
 */
public final class PropertyUtils {

    private static final String DELIM_START = "${";

    private static final String DELIM_STOP = "}";

    /**
     * Retrieves the canonicalized directory from the specified path.
     *
     * @param path
     *            the path to test
     * @param errPrefix
     *            a String to use to provide a specific error message
     * @return the canonicalized {@link File}
     *
     * @throws IllegalArgumentException
     *             if there is an {@link IOException} accessing the directory or
     *             if the path does not exist or if the path is not a directory
     */
    public static File getCanonicalDirectory(String path, String errPrefix) {
        File rc;
        try {
            rc = new File(path).getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalArgumentException(errPrefix + " '" + path + "' : " + e.getMessage()); //$NON-NLS-1$ $NON-NLS-2$
        }
        if (!rc.exists()) {
            throw new IllegalArgumentException(errPrefix + " '" + path + "' : does not exist"); //$NON-NLS-1$ $NON-NLS-2$
        }
        if (!rc.isDirectory()) {
            throw new IllegalArgumentException(errPrefix + " '" + path + "' : is not a directory"); //$NON-NLS-1$ $NON-NLS-2$
        }
        return rc;
    }

    /**
     * Interpolates variables found in the property string values. A variable is
     * {@code $ name} and can be nested {@code $ outer-$ inner} .
     *
     * @param configProperties
     *            the {@link Properties} object that will have its variables
     *            interpolated
     */
    public static void interpolateVariables(final Properties configProperties, final Properties initialProperties) {

        for (Enumeration<?> e = configProperties.propertyNames(); e.hasMoreElements();) {
            final String name = (String) e.nextElement();
            final String value = (String) configProperties.get(name);

            final String newValue = PropertyUtils.substVars(value, name, null, initialProperties);
            configProperties.put(name, newValue);
        }
    }

    /**
     * <p>
     * This method performs property variable substitution on the specified
     * value. If the specified value contains the syntax
     * <tt>${&lt;prop-name&gt;}</tt>, where <tt>&lt;prop-name&gt;</tt> refers to
     * either a configuration property or a system property, then the
     * corresponding property value is substituted for the variable placeholder.
     * Multiple variable placeholders may exist in the specified value as well
     * as nested variable placeholders, which are substituted from inner most to
     * outer most. Configuration properties override system properties.
     * </p>
     *
     * @param val
     *            The string on which to perform property substitution.
     * @param currentKey
     *            The key of the property being evaluated used to detect cycles.
     * @param cycleMap
     *            Map of variable references used to detect nested cycles.
     * @param configProps
     *            Set of configuration properties.
     * @return The value of the specified string after system property
     *         substitution.
     * @throws IllegalArgumentException
     *             If there was a syntax error in the property placeholder
     *             syntax or a recursive variable reference.
     */
    public static String substVars(String val, String currentKey, Map<String, String> cycleMap, Properties configProps)
            throws IllegalArgumentException {
        // If there is currently no cycle map, then create
        // one for detecting cycles for this invocation.
        if (cycleMap == null) {
            cycleMap = new HashMap<String, String>();
        }

        // Put the current key in the cycle map.
        cycleMap.put(currentKey, currentKey);

        // Assume we have a value that is something like:
        // "leading ${foo.${bar}} middle ${baz} trailing"

        // Find the first ending '}' variable delimiter, which
        // will correspond to the first deepest nested variable
        // placeholder.
        int stopDelim = val.indexOf(DELIM_STOP);

        // Find the matching starting "${" variable delimiter
        // by looping until we find a start delimiter that is
        // greater than the stop delimiter we have found.
        int startDelim = val.indexOf(DELIM_START);
        while (stopDelim >= 0) {
            int idx = val.indexOf(DELIM_START, startDelim + DELIM_START.length());
            if ((idx < 0) || (idx > stopDelim)) {
                break;
            } else if (idx < stopDelim) {
                startDelim = idx;
            }
        }

        // If we do not have a start or stop delimiter, then just
        // return the existing value.
        if ((startDelim < 0) && (stopDelim < 0)) {
            return val;
        }
        // At this point, we found a stop delimiter without a start,
        // so throw an exception.
        else if (((startDelim < 0) || (startDelim > stopDelim)) && (stopDelim >= 0)) {
            throw new IllegalArgumentException("stop delimiter with no start delimiter: " + val);
        }

        // At this point, we have found a variable placeholder so
        // we must perform a variable substitution on it.
        // Using the start and stop delimiter indices, extract
        // the first, deepest nested variable placeholder.
        String variable = val.substring(startDelim + DELIM_START.length(), stopDelim);

        // Verify that this is not a recursive variable reference.
        if (cycleMap.get(variable) != null) {
            throw new IllegalArgumentException("recursive variable reference: " + variable);
        }

        // Get the value of the deepest nested variable placeholder.
        // Try to configuration properties first.
        String substValue = (configProps != null) ? configProps.getProperty(variable, null) : null;
        if (substValue == null) {
            // Ignore unknown property values.
            substValue = System.getProperty(variable, "");
        }

        // Remove the found variable from the cycle map, since
        // it may appear more than once in the value and we don't
        // want such situations to appear as a recursive reference.
        cycleMap.remove(variable);

        // Append the leading characters, the substituted value of
        // the variable, and the trailing characters to get the new
        // value.
        val = val.substring(0, startDelim) + substValue + val.substring(stopDelim + DELIM_STOP.length(), val.length());

        // Now perform substitution again, since there could still
        // be substitutions to make.
        val = substVars(val, currentKey, cycleMap, configProps);

        // Return the value.
        return val;
    }

}
