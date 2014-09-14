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
package org.apache.karaf.eik.workbench.ui.views.bundle;

import org.apache.karaf.eik.workbench.KarafWorkbenchActivator;
import org.apache.karaf.eik.workbench.ui.views.FilteredViewPart;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.osgi.framework.Bundle;

public class BundlesView extends FilteredViewPart {

    public static final String VIEW_ID = "org.apache.karaf.eik.workbench.karafBundles";

    protected static final int MAX_COLS = 5;

    private static final String TAG_COLUMN_WIDTH = "columnWidth";

    private Tree treeTable;
    private TreeViewer treeTableViewer;
    private BundlesContentProvider contentProvider;

    private IAction propertiesAction;
    private BundleSymbolicNameFilter nameFilter;

    protected final int[] colWidth = new int[] { 185, 40, 100, 250, 250 };

    @Override
    public void createMainControl(final Composite parent) {
        parent.setLayout(new FillLayout());

        treeTable = new Tree(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
        treeTable.setLinesVisible(true);
        treeTable.setHeaderVisible(true);

        TreeColumn col = new TreeColumn(treeTable, SWT.BORDER);
        col.setWidth(colWidth[0]);
        col.setText("Runtime");

        col = new TreeColumn(treeTable, SWT.BORDER);
        col.setWidth(colWidth[1]);
        col.setText("Id");

        col = new TreeColumn(treeTable, SWT.NONE);
        col.setWidth(colWidth[2]);
        col.setText("State");

        col = new TreeColumn(treeTable, SWT.NONE);
        col.setWidth(colWidth[3]);
        col.setText("Name");

        col = new TreeColumn(treeTable, SWT.NONE);
        col.setWidth(colWidth[4]);
        col.setText("Location");

        treeTableViewer = new TreeViewer(treeTable);
        treeTableViewer.setLabelProvider(new BundleTableLabelProvider());

        nameFilter = new BundleSymbolicNameFilter();
        treeTableViewer.addFilter(nameFilter);

        contentProvider = new BundlesContentProvider();

        treeTableViewer.setContentProvider(contentProvider);
        treeTableViewer.setSorter(new BundleIdSorter());
        treeTableViewer.setInput(KarafWorkbenchActivator.getDefault().getBundle().getBundleContext());

        getViewSite().setSelectionProvider(treeTableViewer);

        fillMenu();

        initContextMenu();
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
        final TreeColumn[] tc = treeTable.getColumns();

        for (int i = 0; i < MAX_COLS; i++) {
            final int width = tc[i].getWidth();
            if (width != 0) {
                memento.putInteger(TAG_COLUMN_WIDTH + i, width);
            }
        }
    }

    @Override
    public void doSetFocus() {
        if (treeTable != null) {
            treeTable.setFocus();
        }
    }

    @Override
    protected void updatedFilter(final String filterString) {
        nameFilter.setFilterString(filterString);
        treeTableViewer.refresh();
    }

    protected void fillMenu() {
        final IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
        menuManager.add(new ExcludeBundlesFilterAction("Installed", Bundle.INSTALLED, treeTableViewer));
        menuManager.add(new ExcludeBundlesFilterAction("Resolved", Bundle.RESOLVED, treeTableViewer));
        menuManager.add(new ExcludeBundlesFilterAction("Starting", Bundle.STARTING, treeTableViewer));
        menuManager.add(new ExcludeBundlesFilterAction("Active", Bundle.ACTIVE, treeTableViewer));
        menuManager.add(new ExcludeBundlesFilterAction("Stopping", Bundle.STOPPING, treeTableViewer));
    }

    protected void initContextMenu() {
        final MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(final IMenuManager manager) {
                menuMgr.add(propertiesAction);
                menuMgr.add(new Separator());
                menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
            }
        });

        final Menu menu = menuMgr.createContextMenu(treeTableViewer.getControl());
        treeTableViewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, treeTableViewer);
    }

    @Override
    public void dispose() {
    }

}
