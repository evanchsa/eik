/**
 * Copyright (c) 2009 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package info.evanchik.eclipse.karaf.workbench.ui.views.services;

import info.evanchik.eclipse.karaf.workbench.provider.BundleItem;
import info.evanchik.eclipse.karaf.workbench.provider.ServiceItem;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class ServicesViewerSorter extends ViewerSorter {

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        if (e1 instanceof ServiceItem == false || e1 instanceof ServiceItem == false) {
            return 0;
        }

        final ServiceItem lhs = (ServiceItem) e1;
        final ServiceItem rhs = (ServiceItem) e2;

        final BundleItem lhsBundle = (BundleItem) lhs.getAdapter(BundleItem.class);
        final BundleItem rhsBundle = (BundleItem) rhs.getAdapter(BundleItem.class);

        if (lhsBundle == null && rhsBundle == null) {
            return 0;
        } else if (lhsBundle == null) {
            return -1;
        } else if (rhsBundle == null) {
            return 1;
        } else {
            int value = lhsBundle.getSymbolicName().compareTo(rhsBundle.getSymbolicName());

            if (value == 0) {
                value = lhs.getServiceInterfaces()[0].compareTo(rhs.getServiceInterfaces()[0]);
            }

            return value;
        }
    }

}
