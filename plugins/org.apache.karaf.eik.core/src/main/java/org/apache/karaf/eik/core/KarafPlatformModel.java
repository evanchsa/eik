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

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osgi.service.resolver.State;
import org.eclipse.pde.core.plugin.IPluginModelBase;

public interface KarafPlatformModel extends IAdaptable {

    /**
     * Determines if a plugin is in the Karaf Target Platform.
     *
     * @param plugin
     *            the plugin's descriptor
     * @return true if the plugin is in the Karaf Target Platform, false
     *         otherwise
     */
    public boolean containsPlugin(IPluginModelBase plugin);

    /**
     * Gets the list of boot classpath jars for Karaf
     *
     * @return a list of jars to be used on the boot classpath
     */
    public List<String> getBootClasspath();

    /**
     * Getter for the directory that contains the default configuration files.
     * These files are considered templates that will be used in the initial
     * configuration of the launch configurations.
     *
     * @return the {@link IPath} to the configuration directory
     */
    public IPath getConfigurationDirectory();

    /**
     * Getter for the root directory of Karaf platform
     *
     * @return the {@link IPath} to the root directory of the Karaf platform
     *         this model represents.
     */
    public IPath getRootDirectory();

    /**
     * Getter for the configuration file indicated by the supplied key. This is
     * typically the name of the file but can be anything as long as it is
     * unique among the set of identifiers used in configuration retrieval by
     * implementors of this interface.
     *
     * @param key
     *            the key of the configuration file
     * @return the {@link IPath} to the configuration file
     */
    public IPath getConfigurationFile(String key);

    /**
     * Gets the root directory that contains the plugins for the platform
     *
     * @return a {@link IPath} that represents the directory that contains the
     *         platform bundles
     */
    public IPath getPluginRootDirectory();

    /**
     * Getter for the underlying OSGi {@link State} object that contains the
     * detailed information about the plugins found in this platform.
     *
     * @return the {@link State} object containing detailed model metadata
     */
    public State getState();

    /**
     * Gets the deployment directory for user deployed bundles. This typically
     * corresponds to the {@code KARAF_ROOT/deploy} directory.
     *
     * @return the {@link IPath} the the directory containing user deployed
     *         bundles
     */
    public IPath getUserDeployedDirectory();

    /**
     * Determines if the specified {@link IPluginModelBase} is a OSGi Framework
     * provider
     *
     * @param model
     *            the {@link IPluginModelBase} to evaluate
     * @return true if the plugin model is an OSGi Framework provider
     */
    public boolean isFrameworkPlugin(IPluginModelBase model);

    /**
     * Determines if the the Karaf platform model is read only.<br>
     * <br>
     * A read only Karaf platform means configuration files cannot be updated.
     *
     * @return true if configuration this Karaf platform instance supports
     *         writable configuration files
     */
    public boolean isReadOnly();

}
