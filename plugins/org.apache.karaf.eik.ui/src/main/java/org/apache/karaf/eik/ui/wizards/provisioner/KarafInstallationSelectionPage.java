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

import org.apache.karaf.eik.core.KarafPlatformDetails;
import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.core.KarafPlatformModelRegistry;
import org.apache.karaf.eik.ui.KarafUIPluginActivator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class KarafInstallationSelectionPage extends WizardPage {

    protected KarafPlatformModel karafPlatform;

    /**
     * The Karaf platform installation directory as selected by the user
     */
    protected Text installDir;

    protected Text platformDescription;

    protected Text platformName;

    protected Text platformVersion;

    /**
     * Constructs a page in the wizard with the given name
     *
     * @param pageName
     *            the name of the page
     */
    public KarafInstallationSelectionPage(final String pageName) {
        super(pageName);

        setTitle("Apache Karaf Provisioner");
        setDescription("Browse for an Apache Karaf installation directory to use as your target platform");

        setPageComplete(false);
        setImageDescriptor(KarafUIPluginActivator.getDefault().getImageRegistry().getDescriptor(KarafUIPluginActivator.LOGO_64X64_IMG)); // $NON-NLS-1$
    }

    @Override
    public void createControl(final Composite parent) {
        final Composite client = new Composite(parent, SWT.NONE);

        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;

        client.setLayout(layout);

        GridData data = new GridData();
        data.horizontalSpan = 2;

        // The installation directory selection controls
        final Label label = new Label(client, SWT.NONE);
        label.setText("Installation directory");
        data = new GridData();
        data.horizontalSpan = 2;
        label.setLayoutData(data);

        Dialog.applyDialogFont(label);

        installDir = new Text(client, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        installDir.setLayoutData(data);
        installDir.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                validateWizardState();
            }
        });

        // File system browse button
        final Button browse = new Button(client, SWT.PUSH);
        browse.setText("Browse");
        browse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent se) {
                final DirectoryDialog dialog = new DirectoryDialog(
                        KarafInstallationSelectionPage.this.getShell());
                dialog.setMessage("Select the Apache Karaf installation directory");
                dialog.setFilterPath(installDir.getText());

                final String selectedDirectory = dialog.open();

                if (selectedDirectory != null) {
                    installDir.setText(selectedDirectory);
                }
            }
        });

        final Group group = new Group(client, SWT.BORDER);
        group.setText("Karaf Platform Details");
        group.setLayout(new GridLayout(2, false));
        data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan = 2;
        group.setLayoutData(data);

        Label l = new Label(group, SWT.NONE);
        l.setText("Name");
        Dialog.applyDialogFont(l);

        platformName = new Text(group, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        platformName.setLayoutData(data);
        platformName.setEnabled(false);

        l = new Label(group, SWT.NONE);
        l.setText("Version");
        Dialog.applyDialogFont(l);

        platformVersion = new Text(group, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        platformVersion.setLayoutData(data);
        platformVersion.setEnabled(false);

        l = new Label(group, SWT.NONE);
        l.setText("Description");
        Dialog.applyDialogFont(l);

        platformDescription = new Text(group, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        platformDescription.setLayoutData(data);
        platformDescription.setEnabled(false);


        setControl(client);
    }

    /**
     * Getter for the newly identified Karaf platform
     *
     * @return the newly identified {@link KarafPlatformModel}
     */
    public KarafPlatformModel getKarafPlatformModel() {
        return karafPlatform;
    }

    private void validateWizardState() {
        try {
            karafPlatform = KarafPlatformModelRegistry.findPlatformModel(new Path(installDir.getText()));
            if (karafPlatform == null) {
                setErrorMessage("The specified directory does not contain a recognized installation");
            } else {
                setErrorMessage(null);
                setPageComplete(true);

                final KarafPlatformDetails platformDetails =
                    (KarafPlatformDetails) karafPlatform.getAdapter(KarafPlatformDetails.class);

                platformName.setText(platformDetails.getName());
                platformVersion.setText(platformDetails.getVersion());
                platformDescription.setText(platformDetails.getDescription());
            }
        } catch (final CoreException e) {
            setErrorMessage("There was an error loading the installation: " + e.getMessage());
            KarafUIPluginActivator.getLogger().error("There was an error loading the installation", e);
        }
    }

}
