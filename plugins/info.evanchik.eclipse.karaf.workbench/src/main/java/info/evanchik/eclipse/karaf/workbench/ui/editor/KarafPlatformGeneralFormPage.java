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

import info.evanchik.eclipse.karaf.workbench.KarafWorkbenchActivator;

import java.lang.management.RuntimeMXBean;

import javax.management.ObjectName;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
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
        GridData data = new GridData(GridData.FILL_BOTH);

        managedForm.getForm().getBody().setLayout(layout);
        managedForm.getForm().getBody().setLayoutData(data);

        managedForm.getForm().setImage(KarafWorkbenchActivator.getDefault().getImageRegistry().get(KarafWorkbenchActivator.LOGO_16X16_IMG));
        managedForm.getForm().setText("Platform Overview");

        final Composite left = managedForm.getToolkit().createComposite(managedForm.getForm().getBody());
        data = new GridData(GridData.FILL_BOTH);
        left.setLayout(new GridLayout(1, true));
        left.setLayoutData(data);

        createKarafPlatformDetailsSection(managedForm, left);
        createKarafJVMDetailsSection(managedForm, left);

        final Composite right = managedForm.getToolkit().createComposite(managedForm.getForm().getBody());
    }

    /**
     * @param managedForm
     */
    private void createKarafJVMDetailsSection(final IManagedForm managedForm, final Composite parent) {
        final Section section = managedForm.getToolkit().createSection(
                parent,
                  Section.TITLE_BAR
                | Section.EXPANDED);

        section.setText("JVM Information");

        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        section.setLayout(new GridLayout(1, true));
        section.setLayoutData(data);

        data = new GridData(GridData.FILL_HORIZONTAL);

        final Composite sectionClient = managedForm.getToolkit().createComposite(section);
        sectionClient.setLayout(new GridLayout(2, true));
        sectionClient.setLayoutData(data);

        section.setClient(sectionClient);

        // TODO: These fields need their data populated asynchronously

        final RuntimeMXBean runtimeMXBean =
            editor.getMBeanProvider().getMBean(createObjectName("java.lang:type=Runtime"), RuntimeMXBean.class);

        managedForm.getToolkit().createLabel(sectionClient, "Virtual Machine");

        final String version = runtimeMXBean.getVmName() + " version " + runtimeMXBean.getVmVersion();
        vmVersion = managedForm.getToolkit().createText(sectionClient, version, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        vmVersion.setLayoutData(data);
        vmVersion.setEnabled(false);

        managedForm.getToolkit().createLabel(sectionClient, "Vendor");

        final String vendor = runtimeMXBean.getVmVendor();
        vmVendor = managedForm.getToolkit().createText(sectionClient, vendor, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        vmVendor.setLayoutData(data);
        vmVendor.setEnabled(false);

        managedForm.getToolkit().createLabel(sectionClient, "Name");

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
    private void createKarafPlatformDetailsSection(final IManagedForm managedForm, final Composite parent) {
        final Section section = managedForm.getToolkit().createSection(
                parent,
                  Section.TITLE_BAR
                | Section.EXPANDED);

        section.setText("Installation Details");

        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        section.setLayout(new GridLayout(1, true));
        section.setLayoutData(data);

        data = new GridData(GridData.FILL_HORIZONTAL);

        final Composite sectionClient = managedForm.getToolkit().createComposite(section);
        sectionClient.setLayout(new GridLayout(2, true));
        sectionClient.setLayoutData(data);

        section.setClient(sectionClient);

        managedForm.getToolkit().createLabel(sectionClient, "Name");

        final String name = editor.getPlatformDetails().getName();
        platformName = managedForm.getToolkit().createText(sectionClient, name, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        platformName.setLayoutData(data);
        platformName.setEnabled(false);

        managedForm.getToolkit().createLabel(sectionClient, "Version");

        final String version = editor.getPlatformDetails().getVersion();
        platformVersion = managedForm.getToolkit().createText(sectionClient, version, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        platformVersion.setLayoutData(data);
        platformVersion.setEnabled(false);

        managedForm.getToolkit().createLabel(sectionClient, "Description");

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
