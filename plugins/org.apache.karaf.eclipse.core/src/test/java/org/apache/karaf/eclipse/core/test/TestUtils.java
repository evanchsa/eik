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
package org.apache.karaf.eclipse.core.test;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Ignore;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
@Ignore
public enum TestUtils {

    DEFAULT;

    /**
     * A {@link FileFilter} implementation that searches for directories
     * that match the supplied prefix
     *
     * @author Stephen Evanchik (evanchsa@gmail.com)
     *
     */
    private final class DistributionFilter implements FileFilter {

        private final String distributionPrefix;
        private final List<File> distributions;

        /**
         * Constructor.
         *
         * @param distributionPrefix
         *            directory name prefix that must match in order for the
         *            directory to be added to the list
         * @param distributions
         *            this is an OUT parameter. It will contain the list of
         *            directories that match the filter
         */
        private DistributionFilter(final String distributionPrefix, final List<File> distributions) {
            this.distributionPrefix = distributionPrefix;
            this.distributions = distributions;
        }

        @Override
        public boolean accept(final File pathname) {
            if (pathname.isDirectory() && pathname.getName().startsWith(distributionPrefix)) {
                distributions.add(pathname);
                return true;
            } else {
                return false;
            }
        }
    }

    private static final String TEST_PROPERTIES = "karaf.test.properties";

    /**
     * Initializes the test's System properties in the following manner:
     * <ol>
     * <li>Examine the embedded {@link #TEST_PROPERTIES} to establish them as
     * the baseline</li>
     * <li>Examine {@code System.getProperty("user.home") +
     * {@link #TEST_PROPERTIES} and if available apply them to the System
     * properties</li>
     * </ol>
     */
    public void initializeSystemProperties() {
        try {
            final InputStream inputStream = getClass().getResourceAsStream(TEST_PROPERTIES);
            final Properties defaultTestProperties = new Properties();
            defaultTestProperties.load(inputStream);
            inputStream.close();

            final String userHome = System.getProperty("user.home");
            final File userTestProperties = new File(userHome, TEST_PROPERTIES);
            if (userTestProperties.isFile() && userTestProperties.canRead()) {
                final FileInputStream fin = new FileInputStream(userTestProperties);

                defaultTestProperties.load(fin);

                fin.close();
            }

            for (final Map.Entry<Object, Object> entry : defaultTestProperties.entrySet()) {
                System.setProperty(entry.getKey().toString(), entry.getValue().toString());
            }
        } catch (final IOException e) {
            // Intentionally left blank
        }
    }

    /**
     *
     * @return
     */
    public Collection<File> getKarafDistributionsForTest() {
        final File root = getKarafDistributionRoot();
        return getKarafDistributionDirectories(root);
    }

    /**
     *
     * @return
     */
    public Collection<File> getServiceMixDistributionsForTest() {
        final File root = getKarafDistributionRoot();
        return getServiceMixDistributionDirectories(root);
    }

    /**
     *
     * @return
     */
    public File getKarafDistributionRoot() {
        return new File(System.getProperty("karaf.distribution.root"));
    }

    /**
     *
     * @param version
     * @return
     */
    public File getKarafDistribution(final String version) {
        final Collection<File> distributions = getKarafDistributionsForTest();

        for (final File distribution : distributions) {
            if (distribution.getName().endsWith(version)) {
                return distribution;
            }
        }

        return null;
    }

    /**
     *
     * @param version
     * @return
     */
    public File getServiceMixDistribution(final String version) {
        final Collection<File> distributions = getServiceMixDistributionsForTest();

        for (final File distribution : distributions) {
            if (distribution.getName().endsWith(version)) {
                return distribution;
            }
        }

        return null;
    }

    /**
     *
     * @param rootDirectory
     * @return
     */
    public Collection<File> getKarafDistributionDirectories(final File rootDirectory) {
        final List<File> distributions = new ArrayList<File>();

        rootDirectory.listFiles(new DistributionFilter("apache-karaf-", distributions));

        return distributions;
    }

    /**
     *
     * @param rootDirectory
     * @return
     */
    public Collection<File> getServiceMixDistributionDirectories(final File rootDirectory) {
        final List<File> distributions = new ArrayList<File>();

        rootDirectory.listFiles(new DistributionFilter("apache-servicemix-", distributions));

        return distributions;
    }
}
