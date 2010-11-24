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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public abstract class BundlesViewerSorter extends ViewerSorter {

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        if (e1 instanceof BundleData == false || e1 instanceof BundleData == false) {
            return 0;
        }

        final BundleData b1 = (BundleData) e1;
        final BundleData b2 = (BundleData) e2;

        return compareBundles(b1, b2);
    }

    protected abstract int compareBundles(BundleData b1, BundleData b2);
}
