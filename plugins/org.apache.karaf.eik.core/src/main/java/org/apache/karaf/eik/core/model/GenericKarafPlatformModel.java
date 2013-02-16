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
package org.apache.karaf.eik.core.model;

import org.apache.karaf.eik.core.IKarafConstants;
import org.apache.karaf.eik.core.KarafCorePluginUtils;
import org.apache.karaf.eik.core.KarafPlatformDetails;
import org.apache.karaf.eik.core.configuration.FeaturesSection;
import org.apache.karaf.eik.core.configuration.GeneralSection;
import org.apache.karaf.eik.core.configuration.ManagementSection;
import org.apache.karaf.eik.core.configuration.ShellSection;
import org.apache.karaf.eik.core.configuration.StartupSection;
import org.apache.karaf.eik.core.configuration.SystemSection;
import org.apache.karaf.eik.core.configuration.internal.FeaturesSectionImpl;
import org.apache.karaf.eik.core.configuration.internal.GeneralSectionImpl;
import org.apache.karaf.eik.core.configuration.internal.ManagementSectionImpl;
import org.apache.karaf.eik.core.configuration.internal.ShellSectionImpl;
import org.apache.karaf.eik.core.configuration.internal.StartupSectionImpl;
import org.apache.karaf.eik.core.configuration.internal.SystemSectionImpl;
import org.apache.karaf.eik.core.internal.KarafCorePluginActivator;
import org.apache.karaf.eik.core.shell.KarafSshConnectionUrl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;

public class GenericKarafPlatformModel extends AbstractKarafPlatformModel implements IAdaptable {

    /**
     * The maximum depth to search for JARs in this model
     */
    public static final int MAX_SEARCH_DEPTH = 50;

    /**
     * The root of the Karaf platform installation
     */
    private final IPath rootPlatformPath;

    /**
     *
     * @param platformPath
     */
    public GenericKarafPlatformModel(final IPath platformPath) {
        this.rootPlatformPath = platformPath;
    }

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") final Class adapterType) {
        final Object adaptedObject;
        if (adapterType == FeaturesSection.class) {
            adaptedObject = new FeaturesSectionImpl(this);
        } else if (adapterType == ShellSection.class) {
            adaptedObject = new ShellSectionImpl(this);
        }else if (adapterType == GeneralSection.class) {
            adaptedObject = new GeneralSectionImpl(this);
        } else if (adapterType == ManagementSection.class) {
            adaptedObject = adaptManagementSection();
        } else if (adapterType == StartupSection.class) {
            adaptedObject = new StartupSectionImpl(this);
        } else if (adapterType == SystemSection.class) {
            return new SystemSectionImpl(this);
        } else if (adapterType == KarafPlatformDetails.class) {
            adaptedObject = adaptKarafPlatformDetails();
        } else if (adapterType == KarafSshConnectionUrl.class) {
            adaptedObject = adaptKarafSshConnectionUrl();
        } else {
            adaptedObject = Platform.getAdapterManager().getAdapter(this, adapterType);
        }

        return adaptedObject;
    }

    @Override
    public List<String> getBootClasspath() {
        final List<File> jarFiles = new ArrayList<File>();
        KarafCorePluginUtils.getJarFileList(rootPlatformPath.append("lib").toFile(), jarFiles, 0);

        final List<String> bootClasspath = new ArrayList<String>();
        for(final File f : jarFiles) {
            bootClasspath.add(f.getAbsolutePath());
        }

        return bootClasspath;
    }

    @Override
    public IPath getConfigurationDirectory() {
        return rootPlatformPath.append("etc");
    }

    @Override
    public IPath getConfigurationFile(final String key) {
        return getConfigurationDirectory().append(key);
    }

    @Override
    public IPath getPluginRootDirectory() {
        return rootPlatformPath.append("system");
    }

    @Override
    public IPath getRootDirectory() {
        return rootPlatformPath;
    }

    /**
     * Directory based Karaf platform model's typically exist outside of an
     * Eclipse platform on a file system and support customization of their
     * configuration.
     *
     * @return false, always
     */
    @Override
    public boolean isReadOnly() {
        return false;
    }

    /**
     * Convert a {@link List} of {@link File} to a {@code List} of {@link URL}
     *
     * @param files
     *            the {@code List} of {@code File}S
     * @return a {@code List} of {@code URL}S, one for each {@code File}
     */
    protected final List<URL> filesToUrls(final List<File> files) {
        final List<URL> urls = new ArrayList<URL>();

        for (final File f : files) {
            try {
                final URL u = f.toURI().toURL();
                // Mandatory to Decode URL. please see
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=258368
            	String urlDecoded = URLDecoder.decode(u.toString(),
						System.getProperty("file.encoding"));
                urls.add(new URL(urlDecoded));
            } catch (final MalformedURLException e) {
                KarafCorePluginActivator.getLogger().error(
                                "Unable to convert file to URL: " + f.getAbsolutePath(), e);
            }catch (UnsupportedEncodingException e) {
				KarafCorePluginActivator.getLogger().error(
						"UnsupportedEncodingException to convert file path : "
								+ f.getAbsolutePath() + "with encoding "
								+ System.getProperty("file.encoding") , e);
            }
        }

        return urls;
    }

    @Override
    protected List<URL> getPlatformBundles() {

        final List<File> jarFiles = new ArrayList<File>();
        KarafCorePluginUtils.getJarFileList(getPluginRootDirectory().toFile(), jarFiles,
                        MAX_SEARCH_DEPTH);

        return filesToUrls(jarFiles);
    }

    private Object adaptKarafSshConnectionUrl() {
        final ShellSection shellSection = (ShellSection) getAdapter(ShellSection.class);
        shellSection.load();

        return new KarafSshConnectionUrl(shellSection.getSshHost(), shellSection.getSshPort());
    }

    private Object adaptKarafPlatformDetails() {
        final Object adaptedObject;
        try {
            if (KarafCorePluginUtils.isKaraf(this)) {
                final File file = getRootDirectory().append("lib").append("karaf.jar").toFile();
                adaptedObject = new GenericKarafPlatformDetails(file);
            } else {
                adaptedObject = null;
            }
        } catch (final IOException e) {
            return null;
        }
        return adaptedObject;
    }

    private Object adaptManagementSection() {
        final Object adaptedObject;
        if (KarafCorePluginUtils.isKaraf(this)) {
            adaptedObject = new ManagementSectionImpl(this, IKarafConstants.ORG_APACHE_KARAF_MANAGEMENT_CFG_FILENAME);
        } else {
            adaptedObject = null;
        }

        return adaptedObject;
    }

}
