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
package name.neilbartlett.eclipse.bundlemonitor.views.bundle.actions;

import java.util.Iterator;

import name.neilbartlett.eclipse.bundlemonitor.internal.Activator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

public class StopBundleAction implements IObjectActionDelegate {

	private IWorkbenchPart targetPart;
	private ISelection selection;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	public void run(IAction action) {
		MultiStatus status = new MultiStatus(Activator.PLUGIN_ID, 0,
				"Problem(s) stopping bundle(s)", null);

		IStructuredSelection structSel = (IStructuredSelection) selection;

		for (Iterator iterator = structSel.iterator(); iterator.hasNext();) {
			Bundle bundle = (Bundle) iterator.next();
			try {
				bundle.stop();
			} catch (BundleException e) {
				status.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0,
						"Error stopping " + bundle.getSymbolicName(), e));
			}
		}

		if (!status.isOK()) {
			ErrorDialog.openError(targetPart.getSite().getShell(), "Error",
					null, status);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
