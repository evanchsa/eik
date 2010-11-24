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


public class BundleIdSorter extends BundlesViewerSorter {

    @Override
    protected int compareBundles(BundleData b1, BundleData b2) {
        return new Long(b1.getIdentifier()).compareTo(b2.getIdentifier());
    }

}
