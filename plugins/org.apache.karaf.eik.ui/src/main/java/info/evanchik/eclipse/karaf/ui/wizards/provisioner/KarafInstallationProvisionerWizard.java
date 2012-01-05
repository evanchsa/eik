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
package info.evanchik.eclipse.karaf.ui.wizards.provisioner;

import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;
import info.evanchik.eclipse.karaf.ui.internal.KarafLaunchUtils;

import java.io.File;
import java.util.Collection;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.pde.ui.IProvisionerWizard;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafInstallationProvisionerWizard extends Wizard implements IProvisionerWizard {

    private KarafPlatformModel karafPlatformModel;

    private KarafInstallationSelectionPage karafInstallationPage;

    public KarafInstallationProvisionerWizard() {
        setDialogSettings(KarafUIPluginActivator.getDefault().getDialogSettings());

        setWindowTitle("Select an Apache Karaf Installation Directory");
    }

    @Override
    public void addPages() {
        karafInstallationPage = new KarafInstallationSelectionPage("Locate an Apache Karaf Installation");
        addPage(karafInstallationPage);
        super.addPages();
    }

    /**
     * Getter for all directories that contain JARs in the Karaf Platform
     *
     * @return the directories that contain JARs in the Karaf Platform
     */
    @Override
    public File[] getLocations() {
        final Collection<File> directories = KarafLaunchUtils.getJarDirectories(karafPlatformModel);
        return directories.toArray(new File[0]);
    }

    @Override
    public boolean performFinish() {
        karafPlatformModel = karafInstallationPage.getKarafPlatformModel();
        return true;
    }
}
