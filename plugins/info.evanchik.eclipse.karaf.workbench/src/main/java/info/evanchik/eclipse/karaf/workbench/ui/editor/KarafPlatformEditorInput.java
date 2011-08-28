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
package info.evanchik.eclipse.karaf.workbench.ui.editor;

import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.ui.IKarafProject;
import info.evanchik.eclipse.karaf.workbench.KarafWorkbenchActivator;
import info.evanchik.eclipse.karaf.workbench.MBeanProvider;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafPlatformEditorInput extends AbstractEditorInput {

    private final IKarafProject karafProject;

    public KarafPlatformEditorInput(final IKarafProject karafProject) {
        this.karafProject = karafProject;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof KarafPlatformEditorInput)) {
            return false;
        }

        final KarafPlatformEditorInput other = (KarafPlatformEditorInput) obj;
        if (karafProject == null) {
            if (other.karafProject!= null) {
                return false;
            }
        } else if (!karafProject.equals(other.karafProject)) {
            return false;
        }

        return true;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return KarafWorkbenchActivator.getDefault().getImageRegistry().getDescriptor(KarafWorkbenchActivator.LOGO_16X16_IMG);
    }

    public KarafPlatformModel getKarafPlatform() {
        return (KarafPlatformModel) karafProject.getAdapter(KarafPlatformModel.class);
    }

    public MBeanProvider getMBeanProvider() {
        return (MBeanProvider) karafProject.getAdapter(MBeanProvider.class);
    }

    @Override
    public String getName() {
        return karafProject.getName();
    }

    @Override
    public String getToolTipText() {
        return karafProject.getName() + " located at: " + getKarafPlatform().getRootDirectory().toOSString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (karafProject == null ? 0 : karafProject.hashCode());
        return result;
    }
}
