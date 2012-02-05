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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.core.SystemBundleNames;
import org.apache.karaf.eclipse.core.internal.StateBuilder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.State;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.osgi.framework.Bundle;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public abstract class AbstractKarafPlatformModel implements KarafPlatformModel {

    /**
     * The list of bundles that are contained within this platform
     */
    private final List<URL> bundleList = new ArrayList<URL>();

    private final StateBuilder stateBuilder = new StateBuilder();

    private final AtomicBoolean stateInitialized = new AtomicBoolean(false);

    @Override
    public boolean containsPlugin(final IPluginModelBase plugin) {
        final String symbolicName = plugin.getBundleDescription().getSymbolicName();

        final BundleDescription desc = getState().getBundle(symbolicName, null);

        return desc != null;
    }

    @Override
    public IPath getConfigurationDirectory() {
        return getRootDirectory().append("etc"); //$NON-NLS-1$
    }

    @Override
    public IPath getUserDeployedDirectory() {
        return getRootDirectory().append("deploy"); //$NON-NLS-1$
    }

    @Override
    public State getState() {
        if (stateInitialized.compareAndSet(false, true)) {
            final List<URL> platformBundles = getPlatformBundles();
            for (final URL bundle : platformBundles) {
                bundleList.add(bundle);
                stateBuilder.add(new File(bundle.getFile()));
            }
        }

        return stateBuilder.getState();
    }

    @Override
    public boolean isFrameworkPlugin(final IPluginModelBase model) {
        final String symbolicName = model.getBundleDescription().getSymbolicName();

        return SystemBundleNames.EQUINOX.toString().equals(symbolicName) || SystemBundleNames.FELIX.toString().equals(symbolicName);
    }

    /**
     * Returns the {@code List} of {@link Bundle} {@code URL}S that are found in
     * this Karaf platform implementation
     *
     * @return TODO
     */
    protected abstract List<URL> getPlatformBundles();
}
