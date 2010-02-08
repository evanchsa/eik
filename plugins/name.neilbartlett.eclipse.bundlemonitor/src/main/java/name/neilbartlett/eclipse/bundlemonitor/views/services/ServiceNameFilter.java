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
package name.neilbartlett.eclipse.bundlemonitor.views.services;


import info.evanchik.eclipse.karaf.workbench.provider.OSGiServiceWrapper;
import info.evanchik.eclipse.karaf.workbench.provider.RuntimeDataProvider;
import name.neilbartlett.eclipse.bundlemonitor.views.shared.PropertyEntry;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class ServiceNameFilter extends ViewerFilter {

    private String serviceName;

    public ServiceNameFilter() {
        this("");
    }

    public ServiceNameFilter(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        boolean result;

        if (element instanceof RuntimeDataProvider) {
            result = true;
        } else if (element instanceof PropertyEntry) {
            result = true;
        } else if (element instanceof OSGiServiceWrapper) {
            result = false;

            final String[] interfaces = ((OSGiServiceWrapper) element).getOSGiService().getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                if (interfaces[i].toLowerCase().indexOf(serviceName.toLowerCase()) > -1) {
                    result = true;
                    break;
                }
            }
        } else {
            result = false;
        }

        return result;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

}
