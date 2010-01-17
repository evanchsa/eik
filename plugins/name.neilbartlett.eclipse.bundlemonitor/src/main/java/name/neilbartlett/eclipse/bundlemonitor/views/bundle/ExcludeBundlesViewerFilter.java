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

import info.evanchik.eclipse.karaf.ui.provider.RuntimeDataProvider;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.osgi.framework.Bundle;
import org.osgi.jmx.codec.OSGiBundle;

public class ExcludeBundlesViewerFilter extends ViewerFilter {

    private final String state;

    public ExcludeBundlesViewerFilter(int state) {
        this.state = stateName(state);
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof RuntimeDataProvider) {
            return true;
        }

        final OSGiBundle bundle = (OSGiBundle) element;

        return bundle.getState().equals(state) == false;
    }

    protected String stateName(int i) {
        String name;

        switch (i) {
        case Bundle.ACTIVE:
            name = "ACTIVE";
            break;
        case Bundle.INSTALLED:
            name = "INSTALLED";
            break;
        case Bundle.RESOLVED:
            name = "RESOLVED";
            break;
        case Bundle.STARTING:
            name = "STARTING";
            break;
        case Bundle.STOPPING:
            name = "STOPPING";
            break;
        default:
            name = "<<unknown>>";
            break;
        }

        return name;
    }

}
