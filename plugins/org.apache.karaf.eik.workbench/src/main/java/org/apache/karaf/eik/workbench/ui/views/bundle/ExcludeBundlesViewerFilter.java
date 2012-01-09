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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.osgi.framework.Bundle;

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

        final BundleItem bundle = (BundleItem) element;

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
