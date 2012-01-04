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

/**
 *
 * @author Neil Bartlett
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class BundleIdSorter extends BundlesViewerSorter {

    @Override
    protected int compareBundles(BundleItem b1, BundleItem b2) {
        return new Long(b1.getIdentifier()).compareTo(b2.getIdentifier());
    }

}
