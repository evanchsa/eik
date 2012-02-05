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
package org.apache.karaf.eclipse.core.model;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.karaf.eclipse.core.KarafCorePluginUtils;
import org.apache.karaf.eclipse.core.KarafPlatformDetails;
import org.apache.karaf.eclipse.core.internal.KarafCorePluginActivator;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class GenericKarafPlatformModel extends AbstractKarafPlatformModel implements IAdaptable {

    /**
     * The maximum depth to search for JARs in this model
     */
    public static int MAX_SEARCH_DEPTH = 50;

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
        final Object adaptedObject = Platform.getAdapterManager().getAdapter(this, adapterType);
        return adaptedObject;
    }

    @Override
    public List<String> getBootClasspath() {
        final List<File> jarFiles = new ArrayList<File>();
        KarafCorePluginUtils.getJarFileList(rootPlatformPath.append("lib").toFile(), jarFiles, 0); //$NON-NLS-1$

        final List<String> bootClasspath = new ArrayList<String>();
        for(final File f : jarFiles) {
            bootClasspath.add(f.getAbsolutePath());
        }

        return bootClasspath;
    }
    @Override
    public IPath getConfigurationDirectory() {
        return rootPlatformPath.append("etc"); //$NON-NLS-1$
    }

    @Override
    public IPath getConfigurationFile(final String key) {
        return getConfigurationDirectory().append(key);
    }

    @Override
    public IPath getPluginRootDirectory() {
        return rootPlatformPath.append("system"); //$NON-NLS-1$
    }

    @Override
    public KarafPlatformDetails getPlatformDetails() {
        try {
            if(KarafCorePluginUtils.isServiceMix(this)) {
                return GenericKarafPlatformDetails.create(getRootDirectory().append("lib").append("servicemix.jar")); // $NON-NLS-1$ $NON-NLS-2$
            } else if (KarafCorePluginUtils.isFelixKaraf(this) || KarafCorePluginUtils.isKaraf(this)) {
                return GenericKarafPlatformDetails.create(getRootDirectory().append("lib").append("karaf.jar")); // $NON-NLS-1$ $NON-NLS-2$
            } else {
                throw new AssertionError("Unknown Karaf platform");
            }
        } catch (final IOException e) {
            // throw something
            return null;
        }
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
                urls.add(u);
            } catch (final MalformedURLException e) {
                KarafCorePluginActivator.getLogger().error(
                                "Unable to convert file to URL: " + f.getAbsolutePath(), e);
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
}
