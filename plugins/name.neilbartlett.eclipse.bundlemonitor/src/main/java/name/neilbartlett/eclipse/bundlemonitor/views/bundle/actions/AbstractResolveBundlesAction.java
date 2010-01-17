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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;

public abstract class AbstractResolveBundlesAction {

	/**
	 * Resolve the selected bundles, or all bundles if the
	 * <code>selection</code> parameter is null, using the specified workbench
	 * part for error reporting.
	 * 
	 * @param part
	 * @param selection
	 *            The bundles to resolve, or null to resolve all bundles.
	 */
	protected void resolveBundles(IWorkbenchPart part, ISelection selection) {
		BundleContext context = Activator.getDefault().getBundleContext();
		ServiceReference svcRef = context
				.getServiceReference(PackageAdmin.class.getName());

		if (svcRef == null) {
			MessageDialog.openError(part.getSite().getShell(), "Error",
					"Package Admin service is not available");
		}

		PackageAdmin pkgAdmin = (PackageAdmin) context.getService(svcRef);
		if (pkgAdmin == null) {
			MessageDialog.openError(part.getSite().getShell(), "Error",
					"Package Admin service is not available");
		}

		Bundle[] bundles;
		if(selection == null) {
			bundles = null;
		} else {
			IStructuredSelection structSel = (IStructuredSelection) selection;
			bundles = new Bundle[structSel.size()];
			int i = 0;
			for (Iterator iterator = structSel.iterator(); iterator.hasNext(); i++) {
				bundles[i] = (Bundle) iterator.next();
			}
		}

		boolean success = pkgAdmin.resolveBundles(bundles);

		if (!success) {
			MessageDialog.openWarning(part.getSite().getShell(),
					"Warning", "One or more bundles failed to resolve");
		}
	}
}
