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
package info.evanchik.eclipse.felix.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.ModelEntry;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.ui.launcher.IPDELauncherConstants;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class FelixLaunchHelper {

    public static final char VERSION_SEPARATOR = '*';

    public static IPluginModelBase findClosestPluginMatch(String version, IPluginModelBase[] models) {
        for (int i = 0; i < models.length; i++) {
            final IPluginBase base = models[i].getPluginBase();

            // match only if...
            // a) if we have the same version
            // b) no version
            // c) all else fails, if there's just one bundle available, use it
            if (base.getVersion().equals(version) || version.length() == 0 || models.length == 1) {
                return models[i];
            }
        }

        return null;
    }

    public static List<BundleEntry> getBundles(String source) {
        final List<BundleEntry> bundles = new ArrayList<BundleEntry>();

        final StringTokenizer tok = new StringTokenizer(source, ",");
        while (tok.hasMoreTokens()) {
            final String bundle = tok.nextToken();

            final BundleEntry entry = BundleEntry.fromString(bundle);
            bundles.add(entry);
        }

        return bundles;
    }

    public static Set<IPluginModelBase> getDeselectedPluginSet(ILaunchConfiguration configuration) throws CoreException {
        final String deselected = configuration.getAttribute(IPDELauncherConstants.DESELECTED_WORKSPACE_PLUGINS, ""); //$NON-NLS-1$
        final List<BundleEntry> deselectedBundles = FelixLaunchHelper.getBundles(deselected);

        final Set<IPluginModelBase> deselectedPlugins = new HashSet<IPluginModelBase>();
        for(BundleEntry e : deselectedBundles) {
            final IPluginModelBase base = FelixLaunchHelper.resolveWorkspaceBundleEntry(e);
            if(base == null) {
                continue;
            }

            deselectedPlugins.add(base);
        }

        return deselectedPlugins;
    }

    public static IPluginModelBase resolveTargetBundleEntry(BundleEntry e) {
        final String[] bundleId = splitBundleId(e);

        final String id = bundleId[0];
        final String version = bundleId[1];

        final ModelEntry entry = PluginRegistry.findEntry(id);
        if(entry == null) {
            return null;
        }

        final IPluginModelBase[] models = entry.getExternalModels();

        return findClosestPluginMatch(version, models);
    }

    public static IPluginModelBase resolveWorkspaceBundleEntry(BundleEntry e) {
        final String[] bundleId = splitBundleId(e);

        final String id = bundleId[0];
        final String version = bundleId[1];

        final ModelEntry entry = PluginRegistry.findEntry(id);
        if(entry == null) {
            return null;
        }

        final IPluginModelBase[] models = entry.getWorkspaceModels();

        return findClosestPluginMatch(version, models);
    }

    public static String[] splitBundleId(BundleEntry e) {
        final String idVersion = e.getBundle();

        final int versionIndex = idVersion.indexOf(VERSION_SEPARATOR);
        final String id = (versionIndex > 0) ? idVersion.substring(0, versionIndex) : idVersion;
        final String version = (versionIndex > 0) ? idVersion.substring(versionIndex + 1) : "";

        return new String[] { id, version };
    }
}
