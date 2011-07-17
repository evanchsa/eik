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

import info.evanchik.eclipse.karaf.core.KarafCorePluginUtils;
import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.pde.ui.IProvisionerWizard;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafInstallationProvisionerWizard extends Wizard implements IProvisionerWizard {

    private static final int MAX_DIRECTORY_RECURSE_DEPTH = 50;

    private KarafInstallationSelectionPage page;

    private KarafPlatformModel karafPlatform;

    public KarafInstallationProvisionerWizard() {
        setDialogSettings(KarafUIPluginActivator.getDefault().getDialogSettings());

        setWindowTitle("Select an Apache Karaf Installation Directory");
    }

    @Override
    public boolean performFinish() {
        karafPlatform = page.getKarafPlatform();
        return true;
    }

    /**
     * Constructs an array of directories that contain plugins in the Karaf
     * platform specified by the user. This array includes the {@code lib}
     * directory as well as all directories that contain JARs under {@code
     * system} and {@code deploy}.
     *
     * @return the directories that contain JARs in the Karaf platform
     */
    @Override
    public File[] getLocations() {
        final List<File> jarFiles = new ArrayList<File>();
        KarafCorePluginUtils.getJarFileList(
                karafPlatform.getPluginRootDirectory().toFile(),
                jarFiles,
                MAX_DIRECTORY_RECURSE_DEPTH);

        KarafCorePluginUtils.getJarFileList(
                karafPlatform.getUserDeployedDirectory().toFile(),
                jarFiles,
                MAX_DIRECTORY_RECURSE_DEPTH);

        // Add each JAR file's directory to the list of directories that contain
        // plugins
        final Set<File> directories = new HashSet<File>();
        for (final File f : jarFiles) {
            directories.add(f.getParentFile());
        }

        // Add the lib directory for completeness, if the developer is pulling a
        // target platform they better know what the are doing
        directories.add(karafPlatform.getRootDirectory().append("lib").toFile()); // $NON-NLS-1$

        return directories.toArray(new File[0]);
    }

    @Override
    public void addPages() {
        page = new KarafInstallationSelectionPage("Locate an Apache Karaf Installation");
        addPage(page);
        super.addPages();
    }
}
