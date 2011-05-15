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
package info.evanchik.eclipse.karaf.workbench.ui.editor;

import java.lang.management.RuntimeMXBean;

import javax.management.ObjectName;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafPlatformGeneralFormPage extends FormPage {

    public static final String ID = "info.evanchik.eclipse.karaf.editors.page.General";

    private static final String TITLE = "General";

    private final KarafPlatformEditorPart editor;

    private Text platformDescription;

    private Text platformName;

    private Text platformVersion;

    private Text vmName;

    private Text vmVendor;

    private Text vmVersion;

    public KarafPlatformGeneralFormPage(final KarafPlatformEditorPart editor) {
        super(editor, ID, TITLE);

        this.editor = editor;
    }

    @Override
    protected void createFormContent(final IManagedForm managedForm) {
        final GridLayout layout = new GridLayout(2, true);

        final GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan = 2;

        managedForm.getForm().getBody().setLayout(layout);
        managedForm.getForm().getBody().setLayoutData(data);

        createKarafPlatformDetailsSection(managedForm, data);
        createKarafJVMDetailsSection(managedForm, data);
    }

    /**
     * @param managedForm
     * @param data
     */
    private void createKarafJVMDetailsSection(final IManagedForm managedForm,
            GridData data) {
        final Section section = managedForm.getToolkit().createSection(
                managedForm.getForm().getBody(),
                  Section.DESCRIPTION
                | Section.TITLE_BAR
                | Section.TWISTIE
                | Section.EXPANDED);

        section.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(final ExpansionEvent e) {
                managedForm.reflow(true);
            }
        });

        section.setText("Karaf JVM Information");
        section.setDescription("This is the description that goes below the title");

        final Composite sectionClient = managedForm.getToolkit().createComposite(section);
        sectionClient.setLayout(new GridLayout(2, false));
        sectionClient.setLayoutData(data);

        section.setClient(sectionClient);

        // TODO: These fields need their data populated asynchronously

        final RuntimeMXBean runtimeMXBean =
            editor.getMBeanProvider().getMBean(createObjectName("java.lang:type=Runtime"), RuntimeMXBean.class);

        Label l = managedForm.getToolkit().createLabel(sectionClient, "Virtual Machine");
        Dialog.applyDialogFont(l);

        final String version = runtimeMXBean.getVmName() + " version " + runtimeMXBean.getVmVersion();
        vmVersion = managedForm.getToolkit().createText(sectionClient, version, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        vmVersion.setLayoutData(data);
        vmVersion.setEnabled(false);

        l = managedForm.getToolkit().createLabel(sectionClient, "Vendor");
        Dialog.applyDialogFont(l);

        final String vendor = runtimeMXBean.getVmVendor();
        vmVendor = managedForm.getToolkit().createText(sectionClient, vendor, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        vmVendor.setLayoutData(data);
        vmVendor.setEnabled(false);

        l = managedForm.getToolkit().createLabel(sectionClient, "Name");
        Dialog.applyDialogFont(l);

        final String name = runtimeMXBean.getName();
        vmName = managedForm.getToolkit().createText(sectionClient, name, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        vmName.setLayoutData(data);
        vmName.setEnabled(false);
    }

    /**
     * @param managedForm
     * @param data
     */
    private void createKarafPlatformDetailsSection(final IManagedForm managedForm, GridData data) {
        final Section section = managedForm.getToolkit().createSection(
                managedForm.getForm().getBody(),
                  Section.DESCRIPTION
                | Section.TITLE_BAR
                | Section.TWISTIE
                | Section.EXPANDED);

        section.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(final ExpansionEvent e) {
                managedForm.reflow(true);
            }
        });

        section.setText("Karaf Platform Overview");
        section.setDescription("This is the description that goes below the title");

        final Composite sectionClient = managedForm.getToolkit().createComposite(section);
        sectionClient.setLayout(new GridLayout(2, false));
        sectionClient.setLayoutData(data);

        section.setClient(sectionClient);

        Label l = managedForm.getToolkit().createLabel(sectionClient, "Name");
        Dialog.applyDialogFont(l);

        final String name = editor.getPlatformDetails().getName();
        platformName = managedForm.getToolkit().createText(sectionClient, name, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        platformName.setLayoutData(data);
        platformName.setEnabled(false);

        l = managedForm.getToolkit().createLabel(sectionClient, "Version");
        Dialog.applyDialogFont(l);

        final String version = editor.getPlatformDetails().getVersion();
        platformVersion = managedForm.getToolkit().createText(sectionClient, version, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        platformVersion.setLayoutData(data);
        platformVersion.setEnabled(false);

        l = managedForm.getToolkit().createLabel(sectionClient, "Description");
        Dialog.applyDialogFont(l);

        final String description = editor.getPlatformDetails().getDescription();
        platformDescription = managedForm.getToolkit().createText(sectionClient, description, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        platformDescription.setLayoutData(data);
        platformDescription.setEnabled(false);
    }

    private ObjectName createObjectName(final String objectName) {
        try {
            return new ObjectName(objectName);
        } catch (final Exception e) {
            throw new AssertionError(e);
        }
    }
}
