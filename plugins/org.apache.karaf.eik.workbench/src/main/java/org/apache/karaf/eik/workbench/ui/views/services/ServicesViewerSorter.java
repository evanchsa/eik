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

import org.apache.karaf.eik.workbench.provider.BundleItem;
import org.apache.karaf.eik.workbench.provider.ServiceItem;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class ServicesViewerSorter extends ViewerSorter {

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        if (e1 instanceof ServiceItem == false || e2 instanceof ServiceItem == false) {
            return 0;
        }

        final ServiceItem lhs = (ServiceItem) e1;
        final ServiceItem rhs = (ServiceItem) e2;

        final BundleItem lhsBundle = (BundleItem) lhs.getAdapter(BundleItem.class);
        final BundleItem rhsBundle = (BundleItem) rhs.getAdapter(BundleItem.class);

        if (lhsBundle == null && rhsBundle == null) {
            return 0;
        } else if (lhsBundle == null) {
            return -1;
        } else if (rhsBundle == null) {
            return 1;
        } else {
            int value = lhsBundle.getSymbolicName().compareTo(rhsBundle.getSymbolicName());

            if (value == 0) {
                value = lhs.getServiceInterfaces()[0].compareTo(rhs.getServiceInterfaces()[0]);
            }

            return value;
        }
    }

}
