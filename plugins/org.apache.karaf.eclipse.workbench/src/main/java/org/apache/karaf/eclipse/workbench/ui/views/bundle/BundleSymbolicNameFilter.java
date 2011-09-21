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
package org.apache.karaf.eclipse.workbench.ui.views.bundle;

import org.apache.karaf.eclipse.workbench.provider.BundleItem;
import org.apache.karaf.eclipse.workbench.provider.RuntimeDataProvider;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class BundleSymbolicNameFilter extends ViewerFilter {

	private String filterString = "";

	public String getFilterString() {
		return filterString;
	}

	@Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
	    if(element instanceof RuntimeDataProvider) {
	        return true;
	    } else if (!(element instanceof BundleItem)) {
	        return false;
	    }

	    final BundleItem bundle = (BundleItem) element;

		return bundle.getSymbolicName().toLowerCase().indexOf(filterString) > -1;
	}

	public void setFilterString(String filterString) {
		this.filterString = filterString.toLowerCase();
	}
}
