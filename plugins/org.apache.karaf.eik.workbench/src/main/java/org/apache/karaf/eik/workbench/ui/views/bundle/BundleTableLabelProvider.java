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
package org.apache.karaf.eik.workbench.ui.views.bundle;

import org.apache.karaf.eik.workbench.provider.BundleItem;
import org.apache.karaf.eik.workbench.provider.RuntimeDataProvider;

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

        final BundleItem bundle = (BundleItem) element;
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
