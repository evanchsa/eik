/*
 * Copyright (c) 2008 Neil Bartlett
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Neil Bartlett - initial implementation
 */
package name.neilbartlett.eclipse.bundlemonitor.views.config;

import java.io.IOException;


import name.neilbartlett.eclipse.bundlemonitor.views.shared.PropertyEntry;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class DeleteConfigViewAction implements IViewActionDelegate {

	private IViewPart view;
	private ConfigWrapper selected;

	public void init(IViewPart view) {
		this.view = view;
	}

	public void run(IAction action) {
		try {
			selected.getConfiguration(((ConfigView) view).tracker).delete();
		} catch (IOException e) {
			MessageDialog.openError(view.getSite().getShell(), "Error", e.getLocalizedMessage());
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		selected = null;
		if(selection != null && !selection.isEmpty()) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if(element instanceof ConfigWrapper) {
				selected = (ConfigWrapper) element;
			} else if(element instanceof PropertyEntry) {
				selected = (ConfigWrapper) ((PropertyEntry) element).getOwner();
			}
		}
		
		action.setEnabled(selected != null);	}

}
