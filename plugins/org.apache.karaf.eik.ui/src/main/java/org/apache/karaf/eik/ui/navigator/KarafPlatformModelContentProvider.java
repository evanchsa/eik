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
package org.apache.karaf.eik.ui.navigator;

import org.apache.karaf.eik.ui.model.AbstractContentModel;
import org.apache.karaf.eik.ui.model.ContentModel;
import org.apache.karaf.eik.ui.project.KarafProject;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class KarafPlatformModelContentProvider implements ITreeContentProvider {

    @Override
    public void dispose() {
    }

    @Override
    public Object[] getChildren(final Object parentElement) {
        if (parentElement instanceof IProject && KarafProject.isKarafProject((IProject) parentElement)) {
            return new Object[0];
        } else if (parentElement instanceof ContentModel) {
            final ContentModel model = (ContentModel) parentElement;
            return model.getElements();
        } else {
            return new Object[0];
        }
    }

    @Override
    public Object[] getElements(final Object inputElement) {
        return getChildren(inputElement);
    }

    @Override
    public Object getParent(final Object element) {
        if (element instanceof AbstractContentModel) {
            final AbstractContentModel model = (AbstractContentModel) element;
            return model.getParent();
        }

        return null;
    }

    @Override
    public boolean hasChildren(final Object element) {
        if (element instanceof AbstractContentModel) {
            final AbstractContentModel model = (AbstractContentModel) element;
            return model.getElements() != null && model.getElements().length > 0;
        }

        return false;
    }

    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {

    }

}
