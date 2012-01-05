/**
 * Copyright (c) 2011 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package info.evanchik.eclipse.karaf.ui.navigator;

import info.evanchik.eclipse.karaf.ui.model.AbstractContentModel;
import info.evanchik.eclipse.karaf.ui.model.ContentModel;
import info.evanchik.eclipse.karaf.ui.project.KarafProject;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
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
