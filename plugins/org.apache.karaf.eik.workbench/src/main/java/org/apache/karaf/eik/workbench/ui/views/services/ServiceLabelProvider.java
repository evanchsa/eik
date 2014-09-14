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

import org.apache.karaf.eik.workbench.KarafWorkbenchActivator;
import org.apache.karaf.eik.workbench.provider.BundleItem;
import org.apache.karaf.eik.workbench.provider.RuntimeDataProvider;
import org.apache.karaf.eik.workbench.provider.ServiceItem;
import org.apache.karaf.eik.workbench.ui.views.PropertyEntry;
import org.apache.karaf.eik.workbench.ui.views.bundle.BundleTableLabelProvider;

import java.util.Arrays;

import org.eclipse.swt.graphics.Image;

public class ServiceLabelProvider extends BundleTableLabelProvider {

    private static final String LABEL_NULL = "<null>";
    private static final String LABEL_ERROR = "<error>";

    public ServiceLabelProvider() {
        super();
    }

    @Override
    public Image getColumnImage(final Object element, final int columnIndex) {
        Image image = super.getColumnImage(element, columnIndex);

        if (image != null) {
            return image;
        }

        if (element instanceof ServiceItem && columnIndex == 0) {
            image = KarafWorkbenchActivator.getDefault().getImageRegistry().get(KarafWorkbenchActivator.SERVICE_IMG);
        }

        return image;
    }

    @Override
    public String getColumnText(final Object element, final int columnIndex) {
        String label;
        if (element instanceof RuntimeDataProvider) {
            if (columnIndex == 0) {
                label = ((RuntimeDataProvider) element).getName();
            } else {
                label = "";
            }
        } else if (element instanceof ServiceItem) {
            final ServiceItem service = (ServiceItem) element;

            if (columnIndex == 0) {
                final String[] interfaces = service.getServiceInterfaces();
                Arrays.sort(interfaces);
                label = arrayToString(interfaces);
            } else {
                final BundleItem bundle = (BundleItem) service.getAdapter(BundleItem.class);
                if (bundle != null) {
                    label = bundle.getSymbolicName();
                } else {
                    label = LABEL_ERROR;
                }
            }
        } else if (element instanceof PropertyEntry) {
            final PropertyEntry prop = (PropertyEntry) element;

            if (columnIndex == 0) {
                label = prop.getKey();
            } else if (columnIndex == 1) {
                final Object value = prop.getValue();

                if (value == null) {
                    label = LABEL_NULL;
                } else if (value instanceof Object[]) {
                    label = arrayToString((Object[]) value);
                } else {
                    label = value.toString();
                }
            } else {
                label = null;
            }
        } else {
            label = LABEL_ERROR;
        }

        return label;
    }

    // TODO: This is just join
    protected static String arrayToString(final Object[] array) {
        final StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(',');
            }

            buffer.append(array[i] == null ? LABEL_NULL : array[i].toString());
        }

        return buffer.toString();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

}
