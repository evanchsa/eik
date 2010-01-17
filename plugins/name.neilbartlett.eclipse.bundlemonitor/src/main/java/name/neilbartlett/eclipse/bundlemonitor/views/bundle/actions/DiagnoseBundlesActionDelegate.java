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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.PlatformAdmin;
import org.eclipse.osgi.service.resolver.ResolverError;
import org.eclipse.osgi.service.resolver.State;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class DiagnoseBundlesActionDelegate implements IObjectActionDelegate {

	private ISelection selection;
	private IWorkbenchPart targetPart;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	public void run(IAction action) {
		MultiStatus status = new MultiStatus(Activator.PLUGIN_ID, 0,
				"Resolver errors found", null);

		BundleContext context = Activator.getDefault().getBundleContext();
		ServiceReference platAdminRef = context
				.getServiceReference(PlatformAdmin.class.getName());
		if (platAdminRef == null) {
			// TODO - can PlatformAdmin ever not be present??
			return;
		}

		PlatformAdmin platAdmin = (PlatformAdmin) context
				.getService(platAdminRef);
		if (platAdmin == null) {
			// TODO - can PlatformAdmin ever not be present??
			return;
		}

		State state = platAdmin.getState(false);

		// Iterate over selected bundles
		IStructuredSelection structSel = (IStructuredSelection) selection;
		for (Iterator iter = structSel.iterator(); iter.hasNext();) {
			Bundle bundle = (Bundle) iter.next();
			BundleDescription bundleDesc = state
					.getBundle(bundle.getBundleId());

			ResolverError[] resolverErrors = state
					.getResolverErrors(bundleDesc);
			if (resolverErrors != null) {
				for (int j = 0; j < resolverErrors.length; j++) {
					status
							.add(resolverErrorToStatus(bundle,
									resolverErrors[j]));
				}
			}
		}

		if (status.isOK()) {
			MessageDialog.openInformation(targetPart.getSite().getShell(),
					"Diagnose",
					"No resolution problems found in selected bundles.");
		} else {
			ErrorDialog.openError(targetPart.getSite().getShell(), "Diagnose",
					null, status);
		}
	}

	protected IStatus resolverErrorToStatus(Bundle bundle, ResolverError error) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(error.toString()).append(" [in bundle ").append(
				bundle.getSymbolicName()).append(']');

		return new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, buffer
				.toString(), null);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
