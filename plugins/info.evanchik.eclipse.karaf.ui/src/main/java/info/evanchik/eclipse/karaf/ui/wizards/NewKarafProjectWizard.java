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
package info.evanchik.eclipse.karaf.ui.wizards;

import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.KarafWorkingPlatformModel;
import info.evanchik.eclipse.karaf.core.model.WorkingKarafPlatformModel;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;
import info.evanchik.eclipse.karaf.ui.internal.NewKarafProjectOperation;
import info.evanchik.eclipse.karaf.ui.wizards.provisioner.KarafInstallationSelectionPage;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class NewKarafProjectWizard extends Wizard implements INewWizard {

    /**
     *
     * @author Stephen Evanchik (evanchsa@gmail.com)
     *
     */
    public interface NewKarafProject {
        /**
         *
         * @return
         */
        IProject getProjectHandle();

        /**
         *
         * @return
         */
        IPath getProjectLocation();

        /**
         *
         * @return
         */
        String getProjectName();
    }

    private KarafInstallationSelectionPage installationSelectionPage;

    private KarafPlatformModel karafPlatformModel;

    private NewKarafProjectWizardPage mainPage;

    private IStructuredSelection selection;

    private IWorkbench workbench;;

    /**
     *
     */
    public NewKarafProjectWizard() {
        setDefaultPageImageDescriptor(KarafUIPluginActivator.getDefault().getImageRegistry().getDescriptor(KarafUIPluginActivator.LOGO_64X64_IMG));
        setDialogSettings(KarafUIPluginActivator.getDefault().getDialogSettings());
        setWindowTitle("Apache Karaf Project");
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
        this.workbench = workbench;
        this.selection = selection;
    }

    @Override
    public boolean performFinish() {
        karafPlatformModel = installationSelectionPage.getKarafPlatform();
        if (karafPlatformModel != null) {
            try {
                final KarafWorkingPlatformModel workingPlatformModel =
                    new WorkingKarafPlatformModel(mainPage.getLocationPath().append(mainPage.getProjectName()), karafPlatformModel);

                final NewKarafProject karafProject = new NewKarafProject() {

                    @Override
                    public IProject getProjectHandle() {
                        return mainPage.getProjectHandle();
                    }

                    @Override
                    public IPath getProjectLocation() {
                        return mainPage.getLocationPath();
                    }

                    @Override
                    public String getProjectName() {
                        return mainPage.getProjectName();
                    }
                };

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
