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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class ResolveBundlesObjectActionDelegate extends
		AbstractResolveBundlesAction implements IObjectActionDelegate {

	private IWorkbenchPart activePart;
	private ISelection selection;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.activePart = targetPart;
	}

	public void run(IAction action) {
		resolveBundles(activePart, selection);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
