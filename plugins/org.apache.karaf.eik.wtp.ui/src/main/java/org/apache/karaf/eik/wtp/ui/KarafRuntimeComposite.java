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
package org.apache.karaf.eik.wtp.ui;

import org.apache.karaf.eik.ui.KarafUIPluginActivator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

public class KarafRuntimeComposite extends Composite {

    /**
     * The handle to the wizard that this {@link Composite} is associated with.
     */
    private final IWizardHandle wizard;

    /**
     * The non-persistent work object used to build up the {@link IRuntime}
     * instance for this Karaf Runtime installation.
     */
    private IRuntimeWorkingCopy karafRuntimeWC;

    /*
     * UI Controls
     */

    /**
     * A meaningful name to this runtime as given by the user
     */
    protected Text name;

    /**
     * The Karaf Runtime installation directory as selected by the user
     */
    protected Text installDir;

    /**
     * Constructor.
     *
     * @param parent
     *            the parent of this {@link Composite} control
     * @param w
     *            the {@link IWizardHandle} of the wizard fragment
     */
    public KarafRuntimeComposite(final Composite parent, final IWizardHandle w) {
        super(parent, SWT.NONE);

        this.wizard = w;

        this.wizard.setTitle("Karaf Server");
        this.wizard.setDescription("Specify the installation directory");
        this.wizard.setImageDescriptor(KarafUIPluginActivator.getDefault().getImageRegistry().getDescriptor("logo64"));

        createCompositeControls();
    }

    /**
     * Setter for the non-persistent work object used to build up the
     * {@link IRuntime}
     *
     * @param karafRuntimeWC
     *            the {@link IRuntimeWorkingCopy} used in this instance of the
     *            wizard
     */
    protected void setKarafRuntimeWC(final IRuntimeWorkingCopy karafRuntimeWC) {
        this.karafRuntimeWC = karafRuntimeWC;

        initializeWizard();
        validateWizardState();
    }

    /**
     * Creates the necessary controls on this composite:<br>
     * <br>
     * - A text box for the name<br>
     * - A text box for the directory of the Karaf installation<br>
     */
    private void createCompositeControls() {
        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;

        setLayout(layout);
        setLayoutData(new GridData(GridData.FILL_BOTH));

        PlatformUI.getWorkbench().getHelpSystem().setHelp(this, "org.apache.felix.karaf...");

        /*
         * Runtime name label and text box
         *
         * The runtime name controls
         */
        Label label = new Label(this, SWT.NONE);
        label.setText("Runtime name");
        GridData data = new GridData();
        data.horizontalSpan = 2;
        label.setLayoutData(data);

        name = new Text(this, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        name.setLayoutData(data);
        name.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                karafRuntimeWC.setName(name.getText());
                validateWizardState();
            }
        });

        // The installation directory selection controls
        label = new Label(this, SWT.NONE);
        label.setText("Installation directory");
        data = new GridData();
        data.horizontalSpan = 2;
        label.setLayoutData(data);

        installDir = new Text(this, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        installDir.setLayoutData(data);
        installDir.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                karafRuntimeWC.setLocation(new Path(installDir.getText()));
                validateWizardState();
            }
        });

        // File system browse button
        final Button browse = KarafRuntimeUtils.createButton(this, "Browse");
        browse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent se) {
                final DirectoryDialog dialog = new DirectoryDialog(KarafRuntimeComposite.this.getShell());
                dialog.setMessage("Select Karaf installation directory");
                dialog.setFilterPath(installDir.getText());

                final String selectedDirectory = dialog.open();

                if (selectedDirectory != null) {
                    installDir.setText(selectedDirectory);
                }
            }
        });

        initializeWizard();
        validateWizardState();

        Dialog.applyDialogFont(this);

        name.forceFocus();
    }

    /**
     * Initializes the wizard's state to any previously set values if applicable
     * or sets the defaults (empty text) of no values are present.
     */
    protected void initializeWizard() {
        if (!isWizardDataInitialized()) {
            return;
        }

        if (karafRuntimeWC.getName() != null) {
            name.setText(karafRuntimeWC.getName());
        } else {
            name.setText("");
        }

        if (karafRuntimeWC.getLocation() != null) {
            installDir.setText(karafRuntimeWC.getLocation().toOSString());
        } else {
            installDir.setText("");
        }
    }

    /**
     * Validate the state of the wizard based on the results of the listeners on
     * the various controls.
     */
    protected void validateWizardState() {
        if (karafRuntimeWC == null) {
            wizard.setMessage("", IMessageProvider.ERROR);
            return;
        }

        final IStatus status = karafRuntimeWC.validate(null);
        if (status == null || status.isOK()) {
            wizard.setMessage(null, IMessageProvider.NONE);
        } else if (status.getSeverity() == IStatus.WARNING) {
            wizard.setMessage(status.getMessage(), IMessageProvider.WARNING);
        } else {
            wizard.setMessage(status.getMessage(), IMessageProvider.ERROR);
        }

        wizard.update();
    }

    /**
     * Determines if the wizard has all of its data initialized. The {@code
     * name} field will be initialized when the {@code Composite} is created,
     * the {@link runtime} field will be initialized after the {@link Composite}
     * has been initialized and when the user enter's the wizard.
     *
     * @return true if the wizard's internal data is fully initialized, false
     *         otherwise
     */
    private boolean isWizardDataInitialized() {
        if (name == null || karafRuntimeWC == null) {
            return false;
        }

        return true;
    }

}
