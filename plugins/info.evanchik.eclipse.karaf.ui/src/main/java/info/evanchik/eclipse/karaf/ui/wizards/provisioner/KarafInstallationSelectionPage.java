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
import info.evanchik.eclipse.karaf.core.model.DirectoryKarafPlatformModel;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafInstallationSelectionPage extends WizardPage {

    protected KarafPlatformModel karafPlatform;

    /**
     * The Karaf Runtime installation directory as selected by the user
     */
    protected Text installDir;

    /**
     * Constructs a page in the wizard with the given name
     *
     * @param pageName
     *            the name of the page
     */
    protected KarafInstallationSelectionPage(String pageName) {
        super(pageName);

        setTitle("Apache Felix Karaf Provisioner");
        setDescription("Browse for a Karaf installation directory to use as your target platform");

        setPageComplete(false);
        setImageDescriptor(KarafUIPluginActivator.getImageDescriptor("logo64")); // $NON-NLS-1$
    }

    public void createControl(Composite parent) {
        final Composite client = new Composite(parent, SWT.NONE);

        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;

        client.setLayout(layout);

        GridData data = new GridData();
        data.horizontalSpan = 2;

        // The installation directory selection controls
        Label label = new Label(client, SWT.NONE);
        label.setText("Installation directory");
        data = new GridData();
        data.horizontalSpan = 2;
        label.setLayoutData(data);

        installDir = new Text(client, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        installDir.setLayoutData(data);
        installDir.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validateWizardState();
            }
        });

        // File system browse button
        Button browse = new Button(client, SWT.PUSH);
        browse.setText("Browse");
        browse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent se) {
                final DirectoryDialog dialog = new DirectoryDialog(
                        KarafInstallationSelectionPage.this.getShell());
                dialog.setMessage("Select Karaf installation directory");
                dialog.setFilterPath(installDir.getText());

                final String selectedDirectory = dialog.open();

                if (selectedDirectory != null) {
                    installDir.setText(selectedDirectory);
                }
            }
        });

        Dialog.applyDialogFont(label);

        setControl(client);
    }

    /**
     * Getter for the newly identified Karaf platform
     *
     * @return the newly identified {@link KarafPlatformModel}
     */
    public KarafPlatformModel getKarafPlatform() {
        return karafPlatform;
    }

    private void validateWizardState() {
        karafPlatform = new DirectoryKarafPlatformModel(new Path(installDir.getText()));
        setPageComplete(karafPlatform.isValid());
    }
}
