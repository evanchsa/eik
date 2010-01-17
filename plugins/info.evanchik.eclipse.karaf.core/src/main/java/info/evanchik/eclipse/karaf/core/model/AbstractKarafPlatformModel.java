/**
 * Copyright (c) 2009 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package info.evanchik.eclipse.karaf.core.model;

import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.SystemBundleNames;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.State;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.internal.core.PDEState;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
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

    public IPath getUserDeployedDirectory() {
        return getRootDirectory().append("deploy"); //$NON-NLS-1$
    }

    public State getState() {
        synchronized (monitor) {
            if (pdeState == null) {
                bundleList.addAll(getPlatformBundles());

                pdeState = new PDEState(bundleList.toArray(new URL[0]), false, new NullProgressMonitor());
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
     * @return TODO
     */
    protected abstract List<URL> getPlatformBundles();
}
