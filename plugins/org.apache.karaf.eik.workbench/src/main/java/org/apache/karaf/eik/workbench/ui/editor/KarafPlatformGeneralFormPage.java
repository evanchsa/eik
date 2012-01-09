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
package org.apache.karaf.eik.workbench.ui.editor;

import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.core.KarafWorkingPlatformModel;
import org.apache.karaf.eik.workbench.KarafWorkbenchActivator;
import org.apache.karaf.eik.workbench.MBeanProvider;
import org.apache.karaf.eik.workbench.WorkbenchServiceListener;
import org.apache.karaf.eik.workbench.WorkbenchServiceManager;
import org.apache.karaf.eik.workbench.jmx.JMXServiceDescriptor;

import java.lang.management.RuntimeMXBean;

import javax.management.ObjectName;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.Section;

public class KarafPlatformGeneralFormPage extends FormPage {

    public static final String ID = "org.apache.karaf.eik.editors.page.General";

    private static final String TITLE = "General";

    private final KarafPlatformEditorPart editor;

    private final MBeanProviderListener mbeanProviderListener;

    private WorkbenchServiceManager<MBeanProvider> mbeanProviderManager;

    private Text platformDescription;

    private Text platformName;

    private Text platformVersion;

    private Text vmName;

    private Text vmVendor;

    private Text vmVersion;

    private final class MBeanProviderListener implements WorkbenchServiceListener<MBeanProvider> {
        @Override
        public void serviceAdded(final MBeanProvider service) {
            final IPath rootDirectory = getKarafPlatformRootPath(service);

            if (editor.getKarafPlatform().getRootDirectory().equals(rootDirectory)) {
                safeUpdateMBeanData(service);
            }
        }

        @Override
        public void serviceRemoved(final MBeanProvider service) {
        }
    }

    public KarafPlatformGeneralFormPage(final KarafPlatformEditorPart editor) {
        super(editor, ID, TITLE);

        this.editor = editor;
        this.mbeanProviderListener = new MBeanProviderListener();

        this.mbeanProviderManager = KarafWorkbenchActivator.getDefault().getMBeanProviderManager();
    }

    @Override
    public void dispose() {
        super.dispose();

        if (mbeanProviderManager != null) {
            mbeanProviderManager.removeListener(mbeanProviderListener);
        }
    }

    public void setMbeanProviderManager(final WorkbenchServiceManager<MBeanProvider> mbeanProviderManager) {
        this.mbeanProviderManager = mbeanProviderManager;
    }

    @Override
    protected void createFormContent(final IManagedForm managedForm) {
        mbeanProviderManager.addListener(mbeanProviderListener);

        final GridLayout layout = new GridLayout(2, true);
        GridData data = new GridData(GridData.FILL_BOTH);

        managedForm.getForm().getBody().setLayout(layout);
        managedForm.getForm().getBody().setLayoutData(data);

        managedForm.getForm().setText("Platform Overview");

        final Composite left = managedForm.getToolkit().createComposite(managedForm.getForm().getBody());
        data = new GridData(GridData.FILL_BOTH);
        left.setLayout(new GridLayout(1, true));
        left.setLayoutData(data);

        createKarafPlatformDetailsSection(managedForm, left);
        createKarafJVMDetailsSection(managedForm, left);

        final Composite right = managedForm.getToolkit().createComposite(managedForm.getForm().getBody());

        for (final MBeanProvider mbeanProvider : mbeanProviderManager.getServices()) {
            final IPath platformPath = getKarafPlatformRootPath(mbeanProvider);
            if (editor.getKarafPlatform().getRootDirectory().equals(platformPath)) {
                updateMBeanData(mbeanProvider);
            }
        }
    }

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

        managedForm.getToolkit().createLabel(sectionClient, "Virtual Machine");

        vmVersion = managedForm.getToolkit().createText(sectionClient, "", SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        vmVersion.setLayoutData(data);
        vmVersion.setEnabled(false);

        managedForm.getToolkit().createLabel(sectionClient, "Vendor");

        vmVendor = managedForm.getToolkit().createText(sectionClient, "", SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        vmVendor.setLayoutData(data);
        vmVendor.setEnabled(false);

        managedForm.getToolkit().createLabel(sectionClient, "Name");

        vmName = managedForm.getToolkit().createText(sectionClient, "", SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        vmName.setLayoutData(data);
        vmName.setEnabled(false);
    }

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

    private IPath getKarafPlatformRootPath(final MBeanProvider service) {
        final JMXServiceDescriptor jmxServiceDescriptor = service.getJMXServiceDescriptor();
        final KarafPlatformModel karafPlatformModel =
            (KarafPlatformModel) jmxServiceDescriptor.getAdapter(KarafPlatformModel.class);

        if (karafPlatformModel == null) {
            return new Path("");
        }

        // TODO: It should be easy to compare to KarafPlatformModel's for equality
        final IPath rootDirectory;
        if (karafPlatformModel instanceof KarafWorkingPlatformModel) {
            rootDirectory = ((KarafWorkingPlatformModel) karafPlatformModel).getParentKarafModel().getRootDirectory();
        } else {
            rootDirectory = karafPlatformModel.getRootDirectory();
        }

        return rootDirectory;
    }

    private void updateMBeanData(final MBeanProvider mbeanProvider) {
        final RuntimeMXBean runtimeMXBean =
            mbeanProvider.getMBean(createObjectName("java.lang:type=Runtime"), RuntimeMXBean.class);

        final String version = runtimeMXBean.getVmName() + " version " + runtimeMXBean.getVmVersion();
        vmVersion.setText(version);

        final String vendor = runtimeMXBean.getVmVendor();
        vmVendor.setText(vendor);

        final String name = runtimeMXBean.getName();
        vmName.setText(name);
    }

    private void safeUpdateMBeanData(final MBeanProvider service) {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                updateMBeanData(service);
            }
        });
    }

}
