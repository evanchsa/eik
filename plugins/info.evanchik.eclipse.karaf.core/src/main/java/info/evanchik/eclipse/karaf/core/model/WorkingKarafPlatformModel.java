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

import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.osgi.service.resolver.State;
import org.eclipse.pde.core.plugin.IPluginModelBase;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class WorkingKarafPlatformModel extends AbstractKarafPlatformModel {

    private final IPath location;

    private final KarafPlatformModel parentKarafModel;

    /**
     * Constructs a {@link KarafPlatformModel} that delegates all information
     * about the underlying plugins that comprise this model while handling all
     * configuration methods.
     *
     * @param thisModelLocation
     *            the {@link IPath} to the working copy of the {@code
     *            KarafPlatformModel}
     * @param source
     *            the source of all non-configuration information for this Karaf
     *            platform.
     */
    public WorkingKarafPlatformModel(IPath thisModelLocation, KarafPlatformModel source) {
        this.location = thisModelLocation;
        this.parentKarafModel = source;
    }

    public KarafPlatformModel getParentKarafModel() {
        return parentKarafModel;
    }

    @Override
    public boolean containsPlugin(IPluginModelBase plugin) {
        return parentKarafModel.containsPlugin(plugin);
    }

    public List<String> getBootClasspath() {
        return parentKarafModel.getBootClasspath();
    }

    public IPath getConfigurationDirectory() {
        return location.append("etc"); //$NON-NLS-1$
    }

    public IPath getConfigurationFile(String key) {
        final IPath origFile = parentKarafModel.getConfigurationFile(key);
        final int matchingSegments = origFile.matchingFirstSegments(parentKarafModel
                        .getConfigurationDirectory());

        final IPath configFile = origFile.removeFirstSegments(matchingSegments - 1);

        return location.append(configFile);
    }

    public IPath getPluginRootDirectory() {
        return parentKarafModel.getPluginRootDirectory();
    }

    public IPath getRootDirectory() {
        return location;
    }

    @Override
    public State getState() {
        return parentKarafModel.getState();
    }

    @Override
    public boolean isFrameworkPlugin(IPluginModelBase model) {
        return parentKarafModel.isFrameworkPlugin(model);
    }

    /**
     * While the source {@link KarafPlatformModel} may be read-only this working
     * copy is not.
     *
     * @return true in all cases
     */
    public boolean isReadOnly() {
        return false;
    }

    /**
     * This working copy is always valid (unless something outside of Eclipse
     * mutates it).
     *
     * @return true in all cases
     */
    public boolean isValid() {
        return true;
    }

    @Override
    protected List<URL> getPlatformBundles() {
        return Collections.emptyList();
    }
}
