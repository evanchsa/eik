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
package info.evanchik.eclipse.karaf.core.configuration.internal;

import info.evanchik.eclipse.karaf.core.KarafCorePluginUtils;
import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.internal.KarafCorePluginActivator;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class BundleStartupSectionImpl extends StartupSectionImpl {

    /**
     * @param karafModel
     */
    public BundleStartupSectionImpl(KarafPlatformModel karafModel) {
        super(karafModel);
    }

    /**
     * This loads the startup properties and strips the Maven 2 style directory
     * prefix from the plugin file name.<br>
     * <br>
     * Eclipse Target Platforms are very static and the UI elements show either
     * a list of files or a list of directories with files. If the target
     * platform definitions has 1 JAR for in every directory (which is what a
     * Maven 2 artifact looks like) then the PDE UI would show many directories
     * with 1 file in it which is not ideal.<br>
     * <br>
     * This is a limitation of PDE's Target Platform system and it should be
     * fixed to be more like {@code ClasspathContainer}S
     */
    @Override
    protected void loadProperties() {
        final IPath path = getParent().getConfigurationFile(getFilename());

        Properties startupProperties = null;
        try {
            final InputStream in = new FileInputStream(path.toFile());

            startupProperties = new Properties();
            startupProperties.load(in);

            in.close();
        } catch (Exception e) {
            KarafCorePluginActivator.getLogger().error(
                            "Unable to load configuration file: " + path.toOSString(), e);
            return;
        }

        final Properties processedStartupProperties = new Properties();

        for (Enumeration<?> e = startupProperties.propertyNames(); e.hasMoreElements();) {
            final String originalName = (String) e.nextElement();
            final String startupValue = startupProperties.getProperty(originalName);

            // The paths are typically in a Maven2 style directory structure,
            // the last path component will be the filename which is all this
            // plugin cares about
            final String newName = KarafCorePluginUtils.getLastPathComponent(originalName);

            processedStartupProperties.put(newName, startupValue);
        }

        setProperties(processedStartupProperties);
    }

}
