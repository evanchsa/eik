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


import info.evanchik.eclipse.karaf.workbench.provider.RuntimeDataProvider;

import org.apache.aries.jmx.codec.BundleData;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class BundleTableLabelProvider extends LabelProvider implements ITableLabelProvider {

    public Image getColumnImage(Object element, int columnIndex) {
        if (columnIndex == 0 && element instanceof RuntimeDataProvider) {
            return ((RuntimeDataProvider) element).getIcon();
        }

        return null;
    }

    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof RuntimeDataProvider) {
            if (columnIndex == 0) {
                return ((RuntimeDataProvider) element).getName();
            }

            return "";
        }

        final BundleData bundle = (BundleData) element;
        String label;

        switch (columnIndex) {
        case 1:
            label = Long.toString(bundle.getIdentifier());
            break;
        case 2:
            label = bundle.getState();
            break;
        case 3:
            label = bundle.getSymbolicName();
            break;
        case 4:
            label = bundle.getLocation();
            break;
        default:
            label = "";
        }

        return label;
    }

}
