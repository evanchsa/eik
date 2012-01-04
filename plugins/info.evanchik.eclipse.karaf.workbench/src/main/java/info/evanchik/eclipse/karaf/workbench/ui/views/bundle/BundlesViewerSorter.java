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
package info.evanchik.eclipse.karaf.workbench.ui.views.bundle;

import info.evanchik.eclipse.karaf.workbench.provider.BundleItem;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 *
 * @author Neil Bartlett
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public abstract class BundlesViewerSorter extends ViewerSorter {

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        if (e1 instanceof BundleItem == false || e1 instanceof BundleItem == false) {
            return 0;
        }

        final BundleItem b1 = (BundleItem) e1;
        final BundleItem b2 = (BundleItem) e2;

        return compareBundles(b1, b2);
    }

    protected abstract int compareBundles(BundleItem b1, BundleItem b2);
}
