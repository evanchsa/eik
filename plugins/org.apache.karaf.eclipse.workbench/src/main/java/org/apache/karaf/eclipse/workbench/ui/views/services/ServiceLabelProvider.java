/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.karaf.eclipse.workbench.ui.views.services;

import org.apache.karaf.eclipse.core.KarafCorePluginUtils;
import org.apache.karaf.eclipse.workbench.KarafWorkbenchActivator;
import org.apache.karaf.eclipse.workbench.provider.BundleItem;
import org.apache.karaf.eclipse.workbench.provider.RuntimeDataProvider;
import org.apache.karaf.eclipse.workbench.provider.ServiceItem;
import org.apache.karaf.eclipse.workbench.ui.views.bundle.BundleTableLabelProvider;

import java.util.Arrays;

import org.eclipse.swt.graphics.Image;

/**
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class ServiceLabelProvider extends BundleTableLabelProvider {

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
                label = KarafCorePluginUtils.join(Arrays.asList(interfaces), ",");
            } else {
                final BundleItem bundle = (BundleItem) service.getAdapter(BundleItem.class);
                if (bundle != null) {
                    label = bundle.getSymbolicName();
                } else {
                    label = LABEL_ERROR;
                }
            }
        } else {
            label = LABEL_ERROR;
        }

        return label;
    }
}
