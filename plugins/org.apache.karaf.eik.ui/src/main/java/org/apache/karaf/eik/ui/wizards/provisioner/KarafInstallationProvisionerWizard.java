/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.karaf.eik.ui.wizards.provisioner;

import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.ui.KarafUIPluginActivator;
import org.apache.karaf.eik.ui.internal.KarafLaunchUtils;

import java.io.File;
import java.util.Collection;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.pde.ui.IProvisionerWizard;

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
