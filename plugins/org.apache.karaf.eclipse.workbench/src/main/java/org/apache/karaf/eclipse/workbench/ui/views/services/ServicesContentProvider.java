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

import org.apache.karaf.eclipse.workbench.provider.RuntimeDataProvider;
import org.apache.karaf.eclipse.workbench.provider.ServiceItem;
import org.apache.karaf.eclipse.workbench.ui.views.bundle.BundlesContentProvider;

/**
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class ServicesContentProvider extends BundlesContentProvider {

    @Override
    public Object[] getChildren(final Object parentElement) {
        Object[] result;
        if (parentElement instanceof RuntimeDataProvider) {
            result = ((RuntimeDataProvider) parentElement).getServices().toArray(new Object[0]);
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
