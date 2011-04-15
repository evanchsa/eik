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
package info.evanchik.eclipse.karaf.ui;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafConfigurationTab extends AbstractLaunchConfigurationTab {

    public static final String ID = "info.evanchik.eclipse.karaf.ui.karafGeneralLaunchConfigurationTab"; //$NON-NLS-1$

    private Composite control;

    private ILaunchConfigurationWorkingCopy configuration;

    @Override
    public void createControl(final Composite parent) {
        control = new Composite(parent, SWT.NONE);

        final GridLayout layout = new GridLayout();
        control.setLayout(layout);

        final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        control.setLayoutData(gd);

        createCommandBlock(control);
        createConsoleBlock(control);

        setControl(control);
        Dialog.applyDialogFont(control);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(control, ID);
    }

    @Override
    public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
    }

    @Override
    public void initializeFrom(final ILaunchConfiguration configuration) {
    }

    @Override
    public void performApply(final ILaunchConfigurationWorkingCopy configuration) {
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Karaf";
    }

    @Override
    public Image getImage() {
        return KarafUIPluginActivator.getDefault().getImageRegistry().get(KarafUIPluginActivator.LOGO_16X16_IMG);
    }

    private void createCommandBlock(final Composite parent) {
        final Font font = parent.getFont();
        final Composite comp = new Composite(parent, SWT.NONE);
        final RowLayout layout = new RowLayout();
        comp.setLayout(layout);
        comp.setFont(font);

        final Button button = new Button(comp, SWT.PUSH);
        button.setText("Restore Karaf Defaults");
        button.setToolTipText("Restores the default launch characteristics");

        button.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
            }

            @Override
            public void widgetSelected(final SelectionEvent e) {
            }
        });
    }

    /**
     * Creates the necessary UI elements that control what kind of console to
     * use (i.e. remote, local or both)
     *
     * @param parent
     */
    private void createConsoleBlock(final Composite parent) {
        final Font font = parent.getFont();
        final Composite comp = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, true);
        comp.setLayout(layout);
        comp.setFont(font);

        final GridData gd = new GridData(GridData.FILL_BOTH);
        comp.setLayoutData(gd);
        setControl(comp);

        final Group group = new Group(comp, SWT.NONE);
        group.setFont(font);
        layout = new GridLayout();
        group.setLayout(layout);
        group.setLayoutData(new GridData(GridData.FILL_BOTH));

        group.setText("Console");
    }
}
