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

import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.core.SystemBundleNames;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.State;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.internal.core.PDEState;
import org.osgi.framework.Bundle;

@SuppressWarnings("restriction")
abstract public class AbstractKarafPlatformModel implements KarafPlatformModel {

    /**
     * The detailed OSGi metadata regarding the state of this model
     */
    private PDEState pdeState;

    /**
     * The list of bundles that are contained withing this platform
     */
    private final List<URL> bundleList = new ArrayList<URL>();

    private final Object monitor = new Object();

    public boolean containsPlugin(IPluginModelBase plugin) {
        final String symbolicName = plugin.getBundleDescription().getSymbolicName();

        final BundleDescription desc = getState().getBundle(symbolicName, null);

        return desc != null;
    }

    public IPath getConfigurationDirectory() {
        return getRootDirectory().append("etc");
    }

    public IPath getUserDeployedDirectory() {
        return getRootDirectory().append("deploy");
    }

    public State getState() {
        synchronized (monitor) {
            if (pdeState == null) {
                bundleList.addAll(getPlatformBundles());
                pdeState = new PDEState(bundleList.toArray(new URL[0]), false, false, new NullProgressMonitor());
            }
        }

        return pdeState.getState();
    }

    public boolean isFrameworkPlugin(IPluginModelBase model) {
        final String symbolicName = model.getBundleDescription().getSymbolicName();

        return SystemBundleNames.EQUINOX.toString().equals(symbolicName) || SystemBundleNames.FELIX.toString().equals(symbolicName);
    }

    /**
     * Returns the {@code List} of {@link Bundle} {@code URL}S that are found in
     * this Karaf platform implementation
     *
     * @return list of bundle URLs.
     */
    protected abstract List<URL> getPlatformBundles();

}
