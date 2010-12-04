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
import info.evanchik.eclipse.karaf.workbench.ui.views.bundle.BundlesContentProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.viewers.StructuredViewer;
import org.osgi.framework.BundleContext;

/**
 *
 * @author Neil Bartlett
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class ServicesContentProvider extends BundlesContentProvider {

    public ServicesContentProvider(StructuredViewer viewer, BundleContext context) {
        super(viewer, context);
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        Object[] result;
        if (parentElement instanceof RuntimeDataProvider) {
            result = ((RuntimeDataProvider) parentElement).getServices().toArray(new Object[0]);
        } else if (parentElement instanceof ServiceItem) {

            final ServiceItem service = (ServiceItem) parentElement;

            final Properties properties = (Properties) service.getAdapter(Properties.class);

            final List<PropertyEntry> entries = new ArrayList<PropertyEntry>();

            for (Object o: properties.keySet()) {
                final String key = (String)o;
                final PropertyEntry pi = new PropertyEntry(service, key, properties.get(key));
                entries.add(pi);
            }

            result = entries.toArray(new Object[0]);
            Arrays.sort(result);

        } else {
            result = new Object[0];
        }

        return result;
    }

    @Override
    public Object getParent(Object element) {
        Object result = super.getParent(element);

        if (result != null) {
            return result;
        }

        if (element instanceof PropertyEntry) {
            result = ((PropertyEntry) element).getOwner();
        }

        return result;
    }

    @Override
    public boolean hasChildren(Object element) {
        boolean children = super.hasChildren(element);

        if (children == true) {
            return children;
        } else if (element instanceof ServiceItem) {
            return true;
        } else {
            return false;
        }
    }
}
