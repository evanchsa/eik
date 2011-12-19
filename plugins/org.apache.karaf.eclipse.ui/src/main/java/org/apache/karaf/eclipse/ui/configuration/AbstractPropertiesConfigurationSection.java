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
package org.apache.karaf.eclipse.ui.configuration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.ui.KarafUIPluginActivator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
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
    public AbstractPropertiesConfigurationSection(final String id, final IPath filename, final KarafPlatformModel parent) {
        super(id, filename, parent);
    }

    @Override
    public IStatus load() {
        loadProperties();

        return Status.OK_STATUS;
    }

    @Override
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
        final IFile file = getKarafProject().getPlatformFile(getFilename().toString());

        try {
            file.refreshLocal(1, new NullProgressMonitor());

            final InputStream in = file.getContents();

            properties = new Properties();
            properties.load(in);

            in.close();
        } catch (final Exception e) {
            KarafUIPluginActivator.getLogger().error(
                            "Unable to load configuration file: " + file.getFullPath().toOSString(), e);
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
            KarafUIPluginActivator.getLogger().info(
                            "Attempting to write to read-only target platform: "
                                            + getParent().getConfigurationDirectory().toOSString());
            return;
        }

        final IFile file = getKarafProject().getPlatformFile(getFilename().toString());

        try {
            file.refreshLocal(1, new NullProgressMonitor());

            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            properties.store(out, getId());

            out.flush();
            out.close();

            file.setContents(new ByteArrayInputStream(out.toByteArray()), false, false, new NullProgressMonitor());
        } catch (final Exception e) {
            KarafUIPluginActivator.getLogger().error(
                            "Unable to save configuration file: " + file.getFullPath().toOSString(), e);
        }
    }

    /**
     * Setter for the {@link Properties} backing this object
     *
     * @param p
     *            the {@link Properties} that will back this object
     */
    protected void setProperties(final Properties p) {
        this.properties = p;
    }
}
