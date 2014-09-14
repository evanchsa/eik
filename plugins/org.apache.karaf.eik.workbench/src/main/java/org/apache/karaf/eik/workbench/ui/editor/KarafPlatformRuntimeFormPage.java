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
import org.apache.karaf.eik.workbench.provider.BundleItem;
import org.apache.karaf.eik.workbench.provider.RuntimeDataProvider;
import org.apache.karaf.eik.workbench.provider.RuntimeDataProviderListener;
import org.apache.karaf.eik.workbench.ui.views.bundle.BundleIdSorter;

import java.util.EnumSet;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.Section;

public class KarafPlatformRuntimeFormPage extends FormPage {

    private final class BundlesTableLabelProvider extends LabelProvider implements ITableLabelProvider {

        @Override
        public Image getColumnImage(final Object element, final int columnIndex) {
            return null;
        }

        @Override
        public String getColumnText(final Object element, final int columnIndex) {
            if (!(element instanceof BundleItem)) {
                return "";
            }

            final BundleItem bundle = (BundleItem) element;
            String label;

            switch (columnIndex) {
            case 0:
                label = bundle.getSymbolicName();
                break;
            case 1:
                label = Long.toString(bundle.getIdentifier());
                break;
            case 2:
                label = bundle.getState();
                break;
            case 3:
                label = bundle.getLocation();
                break;
            default:
                label = "";
            }

            return label;
        }
    }

    private final class BundlesTableContentProvider implements IStructuredContentProvider, RuntimeDataProviderListener {

        private RuntimeDataProvider runtimeDataProvider;

        @Override
        public void dispose() {
            if (runtimeDataProvider != null) {
                runtimeDataProvider.removeListener(this);
            }
        }

        @Override
        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
            if (!(newInput instanceof RuntimeDataProvider)) {
                return;
            }

            if (oldInput != null) {
                final RuntimeDataProvider oldRuntimeDataProvider = (RuntimeDataProvider) oldInput;
                oldRuntimeDataProvider.removeListener(this);
            }

            runtimeDataProvider = (RuntimeDataProvider) newInput;
            runtimeDataProvider.addListener(this);
        }

        @Override
        public Object[] getElements(final Object inputElement) {
            if (inputElement instanceof RuntimeDataProvider) {
                return runtimeDataProvider.getBundles().toArray();
            } else {
                return new Object[0];
            }
        }

        @Override
        public void providerChange(final RuntimeDataProvider source, final EnumSet<EventType> type) {
            safeRefresh(bundlesViewer);
        }

        @Override
        public void providerStart(final RuntimeDataProvider source) {
            safeRefresh(bundlesViewer);
        }

        @Override
        public void providerStop(final RuntimeDataProvider source) {
            safeRefresh(bundlesViewer);
        }
    }

    private final class RuntimeListener implements WorkbenchServiceListener<RuntimeDataProvider> {
        @Override
        public void serviceAdded(final RuntimeDataProvider service) {
            final MBeanProvider mbeanProvider  = (MBeanProvider) service.getAdapter(MBeanProvider.class);

            if (mbeanProvider == null) {
                return;
            }

            final IPath rootDirecotry = getKarafPlatformRootPath(mbeanProvider);
            if (editor.getKarafPlatform().getRootDirectory().equals(rootDirecotry)) {
                bundlesViewer.setInput(service);
            }
        }

        @Override
        public void serviceRemoved(final RuntimeDataProvider service) {
        }
    }

    public static final String ID = "org.apache.karaf.eik.editors.page.Runtime";

    private static final String TITLE = "Runtime";

    private final KarafPlatformEditorPart editor;

    private final int[] colWidth = new int[] { 250, 40, 100, 250 };

    private Table bundlesTable;

    private TableViewer bundlesViewer;

    private final WorkbenchServiceListener<RuntimeDataProvider> runtimeDataProviderListener;

    private WorkbenchServiceManager<RuntimeDataProvider> runtimeDataProviderManager;

    public KarafPlatformRuntimeFormPage(final KarafPlatformEditorPart editor) {
        super(editor, ID, TITLE);

        this.editor = editor;

        runtimeDataProviderListener = new RuntimeListener();
        runtimeDataProviderManager = KarafWorkbenchActivator.getDefault().getRuntimeDataProviderManager();
    }

    @Override
    public void dispose() {
        super.dispose();

        runtimeDataProviderManager.removeListener(runtimeDataProviderListener);
    }

    public void setRuntimeDataProviderManager(final WorkbenchServiceManager<RuntimeDataProvider> runtimeDataProviderManager) {
        this.runtimeDataProviderManager = runtimeDataProviderManager;
    }

    @Override
    protected void createFormContent(final IManagedForm managedForm) {
        runtimeDataProviderManager.addListener(runtimeDataProviderListener);

        final GridLayout layout = new GridLayout(2, true);
        GridData data = new GridData(GridData.FILL_BOTH);

        managedForm.getForm().getBody().setLayout(layout);
        managedForm.getForm().getBody().setLayoutData(data);

        managedForm.getForm().setText("Runtime Details");
        managedForm.getForm().setImage(KarafWorkbenchActivator.getDefault().getImageRegistry().get(KarafWorkbenchActivator.BUNDLE_OBJ_IMG));

        final Section section = managedForm.getToolkit().createSection(
                managedForm.getForm().getBody(),
                Section.TITLE_BAR
                | Section.EXPANDED);

        section.setText("Bundles");

        data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalSpan = 2;
        section.setLayoutData(data);
        section.setLayout(new GridLayout(1, true));

        final Composite sectionClient = managedForm.getToolkit().createComposite(section);
        sectionClient.setLayout(new GridLayout(1, false));
        data = new GridData(GridData.FILL_HORIZONTAL);
        sectionClient.setLayoutData(data);

        section.setClient(sectionClient);

        bundlesTable = managedForm.getToolkit().createTable(sectionClient, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
        bundlesTable.setLinesVisible(true);
        bundlesTable.setHeaderVisible(true);

        data = new GridData(GridData.FILL_HORIZONTAL);
        data.heightHint = 225;

        bundlesTable.setLayoutData(data);
        bundlesTable.setLayout(new FillLayout());

        TableColumn col = new TableColumn(bundlesTable, SWT.LEFT);
        col.setWidth(colWidth[0]);
        col.setText("Name");

        col = new TableColumn(bundlesTable, SWT.LEFT);
        col.setWidth(colWidth[1]);
        col.setText("Id");

        col = new TableColumn(bundlesTable, SWT.LEFT);
        col.setWidth(colWidth[2]);
        col.setText("State");

        col = new TableColumn(bundlesTable, SWT.LEFT);
        col.setWidth(colWidth[3]);
        col.setText("Location");

        bundlesViewer = new TableViewer(bundlesTable);
        bundlesViewer.setLabelProvider(new BundlesTableLabelProvider());
        bundlesViewer.setContentProvider(new BundlesTableContentProvider());
        bundlesViewer.setSorter(new BundleIdSorter());


        managedForm.reflow(true);

        for (final RuntimeDataProvider runtimeDataProvider : runtimeDataProviderManager.getServices()) {
            final MBeanProvider mbeanProvider  = (MBeanProvider) runtimeDataProvider.getAdapter(MBeanProvider.class);

            if (mbeanProvider == null) {
                continue;
            }

            final IPath rootDirecotry = getKarafPlatformRootPath(mbeanProvider);
            if (editor.getKarafPlatform().getRootDirectory().equals(rootDirecotry)) {
                bundlesViewer.setInput(runtimeDataProvider);
            }
        }
    }

    private void safeRefresh(final Viewer viewer) {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                if (!viewer.getControl().isDisposed()) {
                    viewer.refresh();
                }
            }

        });
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

}
