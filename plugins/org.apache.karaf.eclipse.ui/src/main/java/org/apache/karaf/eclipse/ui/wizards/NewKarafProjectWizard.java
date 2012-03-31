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
package org.apache.karaf.eclipse.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.core.KarafWorkingPlatformModel;
import org.apache.karaf.eclipse.core.model.WorkingKarafPlatformModel;
import org.apache.karaf.eclipse.ui.IKarafProject;
import org.apache.karaf.eclipse.ui.KarafUIPluginActivator;
import org.apache.karaf.eclipse.ui.project.KarafProject;
import org.apache.karaf.eclipse.ui.project.NewKarafProjectOperation;
import org.apache.karaf.eclipse.ui.wizards.provisioner.KarafInstallationSelectionPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class NewKarafProjectWizard extends Wizard implements INewWizard {

    private KarafInstallationSelectionPage installationSelectionPage;

    private KarafPlatformModel karafPlatformModel;

    private NewKarafProjectWizardPage mainPage;

    private IStructuredSelection selection;

    /**
     *
     */
    public NewKarafProjectWizard() {
        setDefaultPageImageDescriptor(KarafUIPluginActivator.getDefault().getImageRegistry().getDescriptor(KarafUIPluginActivator.LOGO_64X64_IMG));
        setDialogSettings(KarafUIPluginActivator.getDefault().getDialogSettings());
        setNeedsProgressMonitor(true);
    }

    @Override
    public void addPages() {
        mainPage = new NewKarafProjectWizardPage("Create a new Apache Karaf Project", selection);

        addPage(mainPage);

        installationSelectionPage = new KarafInstallationSelectionPage("Locate an Apache Karaf Installation");

        addPage(installationSelectionPage);

        super.addPages();
    }

    @Override
    public void init(final IWorkbench workbench, final IStructuredSelection selection) {
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
                KarafUIPluginActivator.getLogger().error("Unable to create Apache Karaf project", e);
                return false;
            } catch (final InterruptedException e) {
                KarafUIPluginActivator.getLogger().warn("Interrupted while creating Apache Karaf project", e);
                return false;
            }
        } else {
            return false;
        }
    }
}
