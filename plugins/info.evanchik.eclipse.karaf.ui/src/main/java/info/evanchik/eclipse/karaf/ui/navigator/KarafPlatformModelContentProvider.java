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

import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.ui.IKarafProject;
import info.evanchik.eclipse.karaf.ui.model.AbstractContentModel;
import info.evanchik.eclipse.karaf.ui.model.BootClasspath;
import info.evanchik.eclipse.karaf.ui.model.ConfigurationFiles;
import info.evanchik.eclipse.karaf.ui.model.SystemBundles;
import info.evanchik.eclipse.karaf.ui.model.UserBundles;
import info.evanchik.eclipse.karaf.ui.project.KarafProject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Platform;
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
            final IProject project = (IProject) parentElement;
            final IKarafProject karafProject =
                (IKarafProject) Platform.getAdapterManager().getAdapter(project, IKarafProject.class);

            final KarafPlatformModel karafPlatformModel = (KarafPlatformModel) karafProject.getAdapter(KarafPlatformModel.class);

            return new Object[] {
                    new ConfigurationFiles(project, karafPlatformModel),
                    new BootClasspath(project, karafPlatformModel),
                    new SystemBundles(project, karafPlatformModel),
                    new UserBundles(project, karafPlatformModel)
            };
        } else if (parentElement instanceof AbstractContentModel) {
            final AbstractContentModel model = (AbstractContentModel) parentElement;
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
