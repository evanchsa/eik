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
package org.apache.karaf.eik.ui.wizards;

import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.core.KarafWorkingPlatformModel;
import org.apache.karaf.eik.core.model.WorkingKarafPlatformModel;
import org.apache.karaf.eik.ui.IKarafProject;
import org.apache.karaf.eik.ui.KarafUIPluginActivator;
import org.apache.karaf.eik.ui.project.KarafProject;
import org.apache.karaf.eik.ui.project.NewKarafProjectOperation;
import org.apache.karaf.eik.ui.wizards.provisioner.KarafInstallationSelectionPage;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class NewKarafProjectWizard extends Wizard implements INewWizard {

    private KarafInstallationSelectionPage installationSelectionPage;

    private KarafPlatformModel karafPlatformModel;

    private NewKarafProjectWizardPage mainPage;

    private IStructuredSelection selection;

    private IWorkbench workbench;;

    public NewKarafProjectWizard() {
        setDefaultPageImageDescriptor(KarafUIPluginActivator.getDefault().getImageRegistry().getDescriptor(KarafUIPluginActivator.LOGO_64X64_IMG));
        setDialogSettings(KarafUIPluginActivator.getDefault().getDialogSettings());
        setNeedsProgressMonitor(true);
    }

    @Override
    public void addPages() {
        mainPage = new NewKarafProjectWizardPage("Create a new Apache Karaf project", selection);

        addPage(mainPage);

        installationSelectionPage = new KarafInstallationSelectionPage("Locate an Apache Karaf Installation");

        addPage(installationSelectionPage);

        super.addPages();
    }

    @Override
    public void init(final IWorkbench workbench, final IStructuredSelection selection) {
        this.workbench = workbench;
        this.selection = selection;
    }

    @Override
    public boolean performFinish() {
        karafPlatformModel = installationSelectionPage.getKarafPlatformModel();
        if (karafPlatformModel != null) {
            try {
                final KarafWorkingPlatformModel workingPlatformModel =
                    new WorkingKarafPlatformModel(mainPage.getLocationPath().append(mainPage.getProjectName()), karafPlatformModel);

                final IKarafProject karafProject = new KarafProject(mainPage.getProjectHandle());
                if (mainPage.getProjectHandle().exists()) {
                    return false;
                }

                getContainer().run(false, true, new NewKarafProjectOperation(karafPlatformModel, workingPlatformModel, karafProject));

                return true;
            } catch (final InvocationTargetException e) {
                return false;
            } catch (final InterruptedException e) {
                return false;
            }
        } else {
            return false;
        }
    }

}
