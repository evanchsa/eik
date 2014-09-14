/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.karaf.eik.workbench.ui.views.services;

import org.apache.karaf.eik.workbench.provider.RuntimeDataProvider;
import org.apache.karaf.eik.workbench.provider.ServiceItem;
import org.apache.karaf.eik.workbench.ui.views.PropertyEntry;
import org.apache.karaf.eik.workbench.ui.views.bundle.BundlesContentProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ServicesContentProvider extends BundlesContentProvider {

    @Override
    public Object[] getChildren(final Object parentElement) {
        Object[] result;
        if (parentElement instanceof RuntimeDataProvider) {
            result = ((RuntimeDataProvider) parentElement).getServices().toArray(new Object[0]);
        } else if (parentElement instanceof ServiceItem) {

            final ServiceItem service = (ServiceItem) parentElement;

            final Properties properties = (Properties) service.getAdapter(Properties.class);

            final List<PropertyEntry> entries = new ArrayList<PropertyEntry>();

            if (properties == null) {
                final PropertyEntry pi = new PropertyEntry(service, "Properties unavailable", "");
                entries.add(pi);
            } else {

                for (final Object o: properties.keySet()) {
                    final String key = (String)o;
                    final PropertyEntry pi = new PropertyEntry(service, key, properties.get(key));
                    entries.add(pi);
                }
            }

            result = entries.toArray(new Object[0]);
            Arrays.sort(result);

        } else {
            result = new Object[0];
        }

        return result;
    }

    @Override
    public Object getParent(final Object element) {
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
    public boolean hasChildren(final Object element) {
        final boolean children = super.hasChildren(element);

        if (children == true) {
            return children;
        } else if (element instanceof ServiceItem) {
            return true;
        } else {
            return false;
        }
    }

}
