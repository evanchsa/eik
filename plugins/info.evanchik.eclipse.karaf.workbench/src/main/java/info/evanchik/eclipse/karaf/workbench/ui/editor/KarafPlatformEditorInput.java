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
import info.evanchik.eclipse.karaf.workbench.KarafWorkbenchActivator;
import info.evanchik.eclipse.karaf.workbench.MBeanProvider;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafPlatformEditorInput extends AbstractEditorInput {

    private final KarafPlatformModel karafPlatform;

    private final MBeanProvider mbeanProvider;

    /**
     *
     * @param karafPlatform
     * @param mbeanProvider
     */
    public KarafPlatformEditorInput(final KarafPlatformModel karafPlatform, final MBeanProvider mbeanProvider) {
        this.karafPlatform = karafPlatform;
        this.mbeanProvider = mbeanProvider;
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
        if (karafPlatform == null) {
            if (other.karafPlatform != null) {
                return false;
            }
        } else if (!karafPlatform.equals(other.karafPlatform)) {
            return false;
        }

        if (mbeanProvider == null) {
            if (other.mbeanProvider != null) {
                return false;
            }
        } else if (!mbeanProvider.equals(other.mbeanProvider)) {
            return false;
        }

        return true;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return KarafWorkbenchActivator.getDefault().getImageRegistry().getDescriptor(KarafWorkbenchActivator.LOGO_16X16_IMG);
    }

    public KarafPlatformModel getKarafPlatform() {
        return karafPlatform;
    }

    public MBeanProvider getMBeanProvider() {
        return mbeanProvider;
    }

    @Override
    public String getName() {
        return karafPlatform.getRootDirectory().lastSegment();
    }

    @Override
    public String getToolTipText() {
        return karafPlatform.getRootDirectory().toOSString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (karafPlatform == null ? 0 : karafPlatform.hashCode());
        result = prime * result + (mbeanProvider == null ? 0 : mbeanProvider.hashCode());
        return result;
    }
}
