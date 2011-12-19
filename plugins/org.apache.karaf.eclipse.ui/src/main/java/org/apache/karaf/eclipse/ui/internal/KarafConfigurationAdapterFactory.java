/**
 * Copyright (c) 2011 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package org.apache.karaf.eclipse.ui.internal;

import org.apache.karaf.eclipse.core.IKarafConstants;
import org.apache.karaf.eclipse.core.KarafCorePluginUtils;
import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.core.shell.KarafSshConnectionUrl;
import org.apache.karaf.eclipse.ui.configuration.FeaturesSection;
import org.apache.karaf.eclipse.ui.configuration.GeneralSection;
import org.apache.karaf.eclipse.ui.configuration.ManagementSection;
import org.apache.karaf.eclipse.ui.configuration.ShellSection;
import org.apache.karaf.eclipse.ui.configuration.StartupSection;
import org.apache.karaf.eclipse.ui.configuration.SystemSection;
import org.apache.karaf.eclipse.ui.configuration.internal.FeaturesSectionImpl;
import org.apache.karaf.eclipse.ui.configuration.internal.GeneralSectionImpl;
import org.apache.karaf.eclipse.ui.configuration.internal.ManagementSectionImpl;
import org.apache.karaf.eclipse.ui.configuration.internal.ShellSectionImpl;
import org.apache.karaf.eclipse.ui.configuration.internal.StartupSectionImpl;
import org.apache.karaf.eclipse.ui.configuration.internal.SystemSectionImpl;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafConfigurationAdapterFactory implements IAdapterFactory {

    @Override
    public Object getAdapter(final Object adaptableObject, @SuppressWarnings("rawtypes") final Class adapterType) {

        if (!(adaptableObject instanceof KarafPlatformModel)) {
            return null;
        }

        final Object adaptedObject;
        final KarafPlatformModel karafPlatformModel = (KarafPlatformModel) adaptableObject;

        if (adapterType == FeaturesSection.class) {
            adaptedObject = new FeaturesSectionImpl(karafPlatformModel);
        } else if (adapterType == ShellSection.class) {
            adaptedObject = adaptShellSection(karafPlatformModel);
        }else if (adapterType == GeneralSection.class) {
            adaptedObject = new GeneralSectionImpl(karafPlatformModel);
        } else if (adapterType == ManagementSection.class) {
            adaptedObject = adaptManagementSection(karafPlatformModel);
        } else if (adapterType == StartupSection.class) {
            adaptedObject = new StartupSectionImpl(karafPlatformModel);
        } else if (adapterType == KarafSshConnectionUrl.class) {
            adaptedObject = adaptKarafSshConnectionUrl(karafPlatformModel);
        } else if (adapterType == SystemSection.class) {
            return new SystemSectionImpl(karafPlatformModel);
        } else {
            adaptedObject = Platform.getAdapterManager().getAdapter(adaptableObject, adapterType);
        }

        return adaptedObject;
    }

    /**
     * @param karafPlatformModel
     * @return
     */
    private Object adaptShellSection(final KarafPlatformModel karafPlatformModel) {
        final Object adaptedObject;
        adaptedObject = new ShellSectionImpl(karafPlatformModel);
        return adaptedObject;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class[] getAdapterList() {
        return new Class<?>[] {
                FeaturesSection.class,
                ShellSection.class,
                GeneralSection.class,
                ManagementSection.class,
                StartupSection.class,
                SystemSection.class,
                ShellSection.class,
                KarafSshConnectionUrl.class
        };
    }

    /**
     *
     * @param karafPlatformModel
     * @return
     */
    private Object adaptKarafSshConnectionUrl(final KarafPlatformModel karafPlatformModel) {
        final ShellSection shellSection = (ShellSection) adaptShellSection(karafPlatformModel);
        shellSection.load();

        return new KarafSshConnectionUrl(shellSection.getSshHost(), shellSection.getSshPort());
    }

    /**
     * @param karafModel
     * @return
     */
    private Object adaptManagementSection(final KarafPlatformModel karafPlatformModel) {
        final Object adaptedObject;
        if(KarafCorePluginUtils.isServiceMix(karafPlatformModel)) {
            adaptedObject = new ManagementSectionImpl(karafPlatformModel, IKarafConstants.ORG_APACHE_SERVICEMIX_MANAGEMENT_CFG_FILENAME);
        } else if (KarafCorePluginUtils.isFelixKaraf(karafPlatformModel)) {
            adaptedObject = new ManagementSectionImpl(karafPlatformModel, IKarafConstants.ORG_APACHE_FELIX_KARAF_MANAGEMENT_CFG_FILENAME);
        } else if (KarafCorePluginUtils.isKaraf(karafPlatformModel)) {
            adaptedObject = new ManagementSectionImpl(karafPlatformModel, IKarafConstants.ORG_APACHE_KARAF_MANAGEMENT_CFG_FILENAME);
        } else {
            adaptedObject = null;
        }

        return adaptedObject;
    }
}
