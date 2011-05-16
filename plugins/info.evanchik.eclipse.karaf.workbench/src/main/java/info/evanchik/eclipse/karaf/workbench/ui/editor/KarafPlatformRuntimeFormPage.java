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
import info.evanchik.eclipse.karaf.workbench.MBeanProvider;
import info.evanchik.eclipse.karaf.workbench.WorkbenchServiceManager;
import info.evanchik.eclipse.karaf.workbench.provider.BundleItem;
import info.evanchik.eclipse.karaf.workbench.provider.RuntimeDataProvider;
import info.evanchik.eclipse.karaf.workbench.provider.RuntimeDataProviderListener;
import info.evanchik.eclipse.karaf.workbench.ui.views.bundle.BundleIdSorter;

import java.util.EnumSet;

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

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
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
            runtimeDataProvider.removeListener(this);
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

    public static final String ID = "info.evanchik.eclipse.karaf.editors.page.Runtime";

    private static final String TITLE = "Runtime";

    private final KarafPlatformEditorPart editor;

    private final int[] colWidth = new int[] { 250, 40, 100, 250 };

    private Table bundlesTable;

    private TableViewer bundlesViewer;

    private WorkbenchServiceManager<RuntimeDataProvider> runtimeDataProviderManager;

    public KarafPlatformRuntimeFormPage(final KarafPlatformEditorPart editor) {
        super(editor, ID, TITLE);

        this.editor = editor;

        runtimeDataProviderManager = KarafWorkbenchActivator.getDefault().getRuntimeDataProviderManager();
    }

    public void setRuntimeDataProviderManager(final WorkbenchServiceManager<RuntimeDataProvider> runtimeDataProviderManager) {
        this.runtimeDataProviderManager = runtimeDataProviderManager;
    }

    @Override
    protected void createFormContent(final IManagedForm managedForm) {
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

        final RuntimeDataProvider runtimeDataProvider = findRuntimeDataProviderFor(editor.getMBeanProvider());
        bundlesViewer.setInput(runtimeDataProvider);

        managedForm.reflow(true);
    }

    /**
     *
     * @param mbeanProvider
     * @return
     */
    private RuntimeDataProvider findRuntimeDataProviderFor(final MBeanProvider mbeanProvider) {
        for (final RuntimeDataProvider rdp : runtimeDataProviderManager.getServices()) {
            final MBeanProvider candidateMBeanProvider = (MBeanProvider) rdp.getAdapter(MBeanProvider.class);
            if (mbeanProvider.equals(candidateMBeanProvider)) {
                return rdp;
            }
        }

        return null;
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

}
