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

import info.evanchik.eclipse.karaf.core.IKarafConstants;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.pde.launching.IPDELauncherConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafConfigurationTab extends AbstractLaunchConfigurationTab {

    public static final String ID = "info.evanchik.eclipse.karaf.ui.karafGeneralLaunchConfigurationTab"; //$NON-NLS-1$

    private Composite composite;

    @Override
    public void createControl(final Composite parent) {
        composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));

        createConsoleBlock(composite);

        setControl(composite);
        Dialog.applyDialogFont(composite);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, ID);
    }

    @Override
    public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
    }

    @Override
    public void initializeFrom(final ILaunchConfiguration configuration) {
        try {
            final String osgiFrameworkId = configuration.getAttribute(IPDELauncherConstants.OSGI_FRAMEWORK_ID, ""); //$NON-NLS-1$
            if (!IKarafConstants.KARAF_OSGI_FRAMEWORK_ID.equals(osgiFrameworkId)) {
                composite.setEnabled(false);
            }
        } catch (final CoreException e) {

        }
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
