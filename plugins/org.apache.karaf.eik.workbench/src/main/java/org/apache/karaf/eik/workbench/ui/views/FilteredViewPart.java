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
package org.apache.karaf.eik.workbench.ui.views;

import org.apache.karaf.eik.workbench.KarafWorkbenchActivator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public abstract class FilteredViewPart extends ViewPart {

	private Text txtFilter;
	private Action filterAction;

	private Composite stackPanel;
	private StackLayout stack;
	private Composite topPanel;
	private Composite mainPanel;

	/**
	 * Implements {@link ViewPart#createPartControl(Composite)}
	 */
	@Override
    public final void createPartControl(Composite parent) {
		// Create controls
		stackPanel = new Composite(parent, SWT.NONE);
		stack = new StackLayout();
		stackPanel.setLayout(stack);

		topPanel = new Composite(stackPanel, SWT.NONE);

		// Filter panel
		Composite filterPanel = new Composite(topPanel, SWT.NONE);
		new Label(filterPanel, SWT.NONE).setText("Filter:");
		txtFilter = new Text(filterPanel, SWT.SEARCH);

		// Main panel
		mainPanel = new Composite(stackPanel, SWT.NONE);
		createMainControl(mainPanel);

		// Add listeners
		txtFilter.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updatedFilter(txtFilter.getText());
			}
		});

		// Layout
		stack.topControl = mainPanel;

		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		topPanel.setLayout(layout);

		filterPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		filterPanel.setLayout(new GridLayout(2, false));
		txtFilter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		mainPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		// Toolbar
		createActions();
		fillToolBar(getViewSite().getActionBars().getToolBarManager());
	}

	private void createActions() {
		filterAction = new FilterAction();
	}

	/**
	 * Fill the view toolbar. Subclasses may override but must call
	 * <code>super.fillToolBar</code>
	 *
	 * @param toolBar
	 *            The toolbar manager supplied by the workbench
	 */
	protected void fillToolBar(IToolBarManager toolBar) {
		toolBar.add(filterAction);
	}

	/**
	 * Create the main content of the view, below the filter bar.
	 *
	 * @param container
	 *            The parent composite for the main content. Subclasses should
	 *            set an appropriate layout on this composite.
	 */
	protected abstract void createMainControl(Composite container);

	/**
	 * Called when the filter string is modified by the user. Subclasses should
	 * implement this method to apply the filter to the controls they create.
	 *
	 * @param filterString
	 *            The new filter string, or an empty string ("") if there is no
	 *            filter (e.g., because the user hid the filter bar).
	 */
	protected abstract void updatedFilter(String filterString);

	/*
	 * The filter toggle button
	 */
	private class FilterAction extends Action {
		public FilterAction() {
			super("Filter", IAction.AS_CHECK_BOX);
			setImageDescriptor(KarafWorkbenchActivator.imageDescriptorFromPlugin(KarafWorkbenchActivator.PLUGIN_ID, "/icons/obj16/filter.gif"));
		}

		@Override
        public void run() {
			if(filterAction.isChecked()) {
				stack.topControl = topPanel;
				mainPanel.setParent(topPanel);
				updatedFilter(txtFilter.getText());
			} else {
				stack.topControl = mainPanel;
				mainPanel.setParent(stackPanel);
				updatedFilter("");
			}
			stackPanel.layout(true, true);
			setFocus();
		}
	}

	@Override
    public void setFocus() {
		if(filterAction.isChecked()) {
			txtFilter.setFocus();
		} else {
			doSetFocus();
		}
	}

	/**
	 * Called when the view receives keyboard focus. Subclasses should implement to control
	 */
	protected void doSetFocus() {

	}

}
