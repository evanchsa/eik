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
package info.evanchik.eclipse.karaf.workbench.ui.views.services;


import info.evanchik.eclipse.karaf.workbench.provider.RuntimeDataProvider;
import info.evanchik.eclipse.karaf.workbench.provider.ServiceItem;
import info.evanchik.eclipse.karaf.workbench.ui.views.PropertyEntry;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 *
 * @author Neil Bartlett
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
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
        }

        else if (element instanceof ServiceItem) {
            result = false;

            final String[] interfaces = ((ServiceItem) element).getServiceInterfaces();
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
