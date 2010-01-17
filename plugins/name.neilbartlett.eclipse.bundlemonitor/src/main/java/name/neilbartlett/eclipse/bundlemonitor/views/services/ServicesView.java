/*
 * Copyright (c) 2008 Neil Bartlett
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Neil Bartlett - initial implementation
 *     Stephen Evanchik - Updated to use data provider services
 */
package name.neilbartlett.eclipse.bundlemonitor.views.services;

import name.neilbartlett.eclipse.bundlemonitor.internal.Activator;
import name.neilbartlett.eclipse.bundlemonitor.views.shared.FilteredViewPart;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.osgi.framework.BundleContext;

public class ServicesView extends FilteredViewPart {

    protected static final int MAX_COLS = 2;

    private static final String TAG_COLUMN_WIDTH = "columnWidth";

    private Tree tree;
    private TreeViewer viewer;

    private ServicesContentProvider contentProvider;

    private ServiceNameFilter nameFilter;

    private BundleContext context;

    protected final int[] colWidth = new int[] { 400, 200 };

    @Override
    public void createMainControl(Composite parent) {
        final GridLayout layout = new GridLayout(1, false);
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 0;

        parent.setLayout(layout);

        tree = new Tree(parent, SWT.FULL_SELECTION);
        tree.setLinesVisible(true);
        tree.setHeaderVisible(true);

        TreeColumn col;
        col = new TreeColumn(tree, SWT.NONE);
        col.setWidth(colWidth[0]);
        col.setText("Service Interfaces");

        col = new TreeColumn(tree, SWT.NONE);
        col.setWidth(colWidth[1]);
        col.setText("Parent Bundle");

        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

        viewer = new TreeViewer(tree);
        viewer.setLabelProvider(new ServiceLabelProvider());
        viewer.setSorter(new ServicesViewerSorter());

        nameFilter = new ServiceNameFilter();
        viewer.addFilter(nameFilter);

        context = Activator.getDefault().getBundleContext();
        contentProvider = new ServicesContentProvider(viewer, context);
        viewer.setContentProvider(contentProvider);

        viewer.setInput(context);

        contentProvider.start();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException {
        super.init(site, memento);

        for (int i = 0; i < MAX_COLS; i++) {

            if (memento != null) {
                final Integer in = memento.getInteger(TAG_COLUMN_WIDTH + i);
                if (in != null && in.intValue() > 5) {
                    colWidth[i] = in.intValue();
                }
            }
        }
    }

    @Override
    public void saveState(IMemento memento) {
        final TreeColumn[] tc = tree.getColumns();

        for (int i = 0; i < MAX_COLS; i++) {
            final int width = tc[i].getWidth();

            if (width != 0) {
                memento.putInteger(TAG_COLUMN_WIDTH + i, width);
            }
        }
    }

    @Override
    protected void updatedFilter(String filterString) {
        nameFilter.setServiceName(filterString);
        viewer.refresh();
    }
}
