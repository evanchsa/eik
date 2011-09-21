/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.karaf.eclipse.workbench.ui.views.services;

import org.apache.karaf.eclipse.workbench.KarafWorkbenchActivator;

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
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.BundleContext;

/**
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class ServicesView extends ViewPart {

    public static final String VIEW_ID = "org.apache.karaf.eclipse.workbench.karafServices";

    protected static final int MAX_COLS = 2;

    private static final String TAG_COLUMN_WIDTH = "columnWidth";

    private Tree tree;

    private TreeViewer viewer;

    private ServicesContentProvider contentProvider;

    private ServiceNameFilter nameFilter;

    private BundleContext context;

    protected final int[] colWidth = new int[] { 400, 200 };

    @Override
    public void createPartControl(final Composite parent) {
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

        context = KarafWorkbenchActivator.getDefault().getBundle().getBundleContext();
        contentProvider = new ServicesContentProvider();
        viewer.setContentProvider(contentProvider);

        viewer.setInput(context);
    }

    @Override
    public void init(final IViewSite site, final IMemento memento) throws PartInitException {
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
    public void saveState(final IMemento memento) {
        final TreeColumn[] tc = tree.getColumns();

        for (int i = 0; i < MAX_COLS; i++) {
            final int width = tc[i].getWidth();

            if (width != 0) {
                memento.putInteger(TAG_COLUMN_WIDTH + i, width);
            }
        }
    }

    @Override
    public void setFocus() {
    }
}
