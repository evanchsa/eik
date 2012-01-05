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
package info.evanchik.eclipse.karaf.core.configuration;

import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.internal.KarafCorePluginActivator;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
abstract public class AbstractPropertiesConfigurationSection extends AbstractConfigurationSection {

    private Properties properties;

    /**
     * @see AbstractConfigurationSection#AbstractConfigurationSection(String,
     *      String, KarafPlatformModel)
     */
    public AbstractPropertiesConfigurationSection(String id, String filename,
                    KarafPlatformModel parent) {
        super(id, filename, parent);
    }

    public IStatus load() {
        loadProperties();

        return Status.OK_STATUS;
    }

    public IStatus save() {
        saveProperties();

        return Status.OK_STATUS;
    }

    /**
     * Getter for the {@link Properties} backing this object
     *
     * @return the {@link Properties} backing this object
     */
    protected Properties getProperties() {
        return properties;
    }

    /**
     * Loads the properties for this configuration section
     */
    protected void loadProperties() {
        final IPath path = getParent().getConfigurationFile(getFilename());

        try {
            final InputStream in = new FileInputStream(path.toFile());

            properties = new Properties();
            properties.load(in);

            in.close();
        } catch (Exception e) {
            KarafCorePluginActivator.getLogger().error(
                            "Unable to load configuration file: " + path.toOSString(), e);
        }
    }

    /**
     * Saves the properties for this configuration section if the parent model
     * is not read-only.<br>
     * <br>
     * If the parent model is read-only this method does nothing.
     */
    protected void saveProperties() {
        if (getParent().isReadOnly()) {
            KarafCorePluginActivator.getLogger().info(
                            "Attempting to write to read-only target platform: "
                                            + getParent().getConfigurationDirectory().toOSString());
            return;
        }

        final IPath path = getParent().getConfigurationFile(getFilename());

        try {
            final OutputStream out = new FileOutputStream(path.toFile());
            properties.store(out, getId());

            out.flush();
            out.close();
        } catch (Exception e) {
            KarafCorePluginActivator.getLogger().error(
                            "Unable to save configuration file: " + path.toOSString(), e);
        }
    }

    /**
     * Setter for the {@link Properties} backing this object
     *
     * @param p
     *            the {@link Properties} that will back this object
     */
    protected void setProperties(Properties p) {
        this.properties = p;
    }
}
