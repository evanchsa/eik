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
package org.apache.karaf.eclipse.workbench.properties;

import java.util.Map;
import java.util.Properties;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbenchPropertyPage;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class RuntimePropertiesPage extends KarafPropertyPage implements IWorkbenchPropertyPage {

    private class RuntimePropertiesContentProvider implements IStructuredContentProvider {

        @Override
        public void dispose() {
        }

        @Override
        public Object[] getElements(final Object inputElement) {
            return runtimeProperties.entrySet().toArray();
        }

        @Override
        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        }
    }

    private class NameLabelProvider extends ColumnLabelProvider {

        @Override
        public String getText(final Object element) {
            if (element instanceof Map.Entry) {
                final Map.Entry<?, ?> entry = (Map.Entry<?, ?>) element;
                return entry.getKey().toString();
            } else {
                return super.getText(element);
            }
        }
    }

    private class ValueLabelProvider extends ColumnLabelProvider {
        @Override
        public String getText(final Object element) {
            if (element instanceof Map.Entry) {
                final Map.Entry<?, ?> entry = (Map.Entry<?, ?>) element;
                return entry.getValue().toString();
            } else {
                return super.getText(element);
            }
        }

    }

    private class RuntimePropertySorter extends ViewerSorter {
        @Override
        public int compare(final Viewer viewer, final Object e1, final Object e2) {
            @SuppressWarnings("unchecked")
            final Map.Entry<Object, Object> lhs = (Map.Entry<Object, Object>) e1;
            @SuppressWarnings("unchecked")
            final Map.Entry<Object, Object> rhs = (Map.Entry<Object, Object>) e2;

            return lhs.getKey().toString().compareTo(rhs.getKey().toString());
        }
    }

    private Button addButton;

    private Button editButton;

    private Button removeButton;

    private Properties runtimeProperties;

    private Listener selectionListener;

    private Label variableLabel;

    private TableViewer variableTable;

    public RuntimePropertiesPage() {
        super();
    }

    @Override
    protected Control createContents(final Composite parent) {
        initializeKarafPlatformElements();

        final Composite pageComponent = new Composite(parent, SWT.NULL);
        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        pageComponent.setLayout(layout);

        variableLabel = new Label(pageComponent, SWT.LEFT);
        variableLabel.setText("Variables");

        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.horizontalSpan = 2;
        variableLabel.setLayoutData(data);

        final int tableStyle = SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL;

        final Composite tableComposite = new Composite(pageComponent, SWT.NONE);
        data = new GridData(SWT.FILL, SWT.FILL, true, true);

        tableComposite.setLayoutData(data);

        variableTable = new TableViewer(tableComposite, tableStyle);

        variableTable.getTable().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                updateEnabledState();
                if (selectionListener != null) {
                    selectionListener.handleEvent(new Event());
                }
            }
        });

        ColumnViewerToolTipSupport.enableFor(variableTable, ToolTip.NO_RECREATE);

        final TableViewerColumn nameColumn = new TableViewerColumn(variableTable, SWT.NONE);
        nameColumn.setLabelProvider(new NameLabelProvider());
        nameColumn.getColumn().setText("Name");

        final TableViewerColumn valueColumn = new TableViewerColumn(variableTable, SWT.NONE);
        valueColumn.setLabelProvider(new ValueLabelProvider());
        valueColumn.getColumn().setText("Value");

        final TableColumnLayout tableLayout = new TableColumnLayout();
        tableComposite.setLayout(tableLayout);

        tableLayout.setColumnData(nameColumn.getColumn(), new ColumnWeightData(150));
        tableLayout.setColumnData(valueColumn.getColumn(), new ColumnWeightData(280));

        variableTable.getTable().setHeaderVisible(true);
        data = new GridData(GridData.FILL_BOTH);

        data.heightHint = variableTable.getTable().getItemHeight() * 7;
        variableTable.getTable().setLayoutData(data);

        variableTable.getTable().addMouseListener(new MouseListener() {
            @Override
            public void mouseDoubleClick(final MouseEvent e) {
                final int itemsSelectedCount = variableTable.getTable().getSelectionCount();
                if (itemsSelectedCount == 1 && canChangeSelection()) {
                    // editSelectedVariable();
                }
            }

            @Override
            public void mouseDown(final MouseEvent e) {
            }

            @Override
            public void mouseUp(final MouseEvent e) {
            }
        });

        variableTable.getTable().addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
                updateEnabledState();
            }

            @Override
            public void widgetSelected(final SelectionEvent e) {
                updateEnabledState();
            }
        });

        variableTable.getTable().setToolTipText(null);
        variableTable.setContentProvider(new RuntimePropertiesContentProvider());
        variableTable.setSorter(new RuntimePropertySorter());

        variableTable.setInput(this);

        createButtonGroup(pageComponent);

        return pageComponent;
    }

    private boolean canChangeSelection() {
        return false;
    }

    private void createButtonGroup(final Composite parent) {
        final Font font = parent.getFont();

        final Composite groupComponent = new Composite(parent, SWT.NULL);

        final GridLayout groupLayout = new GridLayout();
        groupLayout.marginWidth = 0;
        groupLayout.marginHeight = 0;
        groupComponent.setLayout(groupLayout);

        final GridData data = new GridData();
        data.verticalAlignment = GridData.FILL;
        data.horizontalAlignment = GridData.FILL;
        groupComponent.setLayoutData(data);
        groupComponent.setFont(font);

        addButton = new Button(groupComponent, SWT.PUSH);
        addButton.setText("Add");
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                // addNewVariable();
            }
        });
        addButton.setFont(font);
        setButtonLayoutData(addButton);

        editButton = new Button(groupComponent, SWT.PUSH);
        editButton.setText("Edit");
        editButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                // editSelectedVariable();
            }
        });
        editButton.setFont(font);
        setButtonLayoutData(editButton);

        removeButton = new Button(groupComponent, SWT.PUSH);
        removeButton.setText("Remove");
        removeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                // removeSelectedVariables();
            }
        });
        removeButton.setFont(font);
        setButtonLayoutData(removeButton);
        updateEnabledState();
    }

    private void initializeKarafPlatformElements() {
        runtimeProperties = getKarafProject().getRuntimeProperties();
    }

    private void updateEnabledState() {
        final int itemsSelectedCount = variableTable.getTable().getSelectionCount();
        editButton.setEnabled(itemsSelectedCount == 1 && canChangeSelection());
        removeButton.setEnabled(itemsSelectedCount > 0 && canChangeSelection());
    }
}
