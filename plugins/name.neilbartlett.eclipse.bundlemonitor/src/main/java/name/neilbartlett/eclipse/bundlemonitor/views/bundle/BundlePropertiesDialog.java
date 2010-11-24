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
package name.neilbartlett.eclipse.bundlemonitor.views.bundle;


import org.apache.aries.jmx.codec.BundleData;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class BundlePropertiesDialog extends TitleAreaDialog {

    private final BundleData bundle;

    public BundlePropertiesDialog(Shell parentShell, BundleData bundle) {
        super(parentShell);

        this.bundle = bundle;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite dialogArea = (Composite) super.createDialogArea(parent);

        setTitle("Bundle Properties");

        final Table table = new Table(dialogArea, SWT.FULL_SELECTION);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        TableColumn col;

        col = new TableColumn(table, SWT.NONE);
        col.setWidth(300);
        col.setText("Name");

        col = new TableColumn(table, SWT.NONE);
        col.setWidth(400);
        col.setText("Value");

        final TableViewer viewer = new TableViewer(table);
        viewer.setContentProvider(new DictionaryContentProvider());
        viewer.setLabelProvider(new DictionaryEntryTableLabelProvider());
        viewer.setInput(bundle.getHeaders());
        viewer.setSorter(new ViewerSorter() {

            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                if(e1 instanceof DictionaryEntry == false || e1 instanceof DictionaryEntry == false) {
                    return 0;
                }

                final DictionaryEntry lhs = (DictionaryEntry)e1;
                final DictionaryEntry rhs = (DictionaryEntry)e2;

                return lhs.getKey().toString().compareTo(rhs.getKey().toString());
            }

        });

        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        return dialogArea;
    }

}
