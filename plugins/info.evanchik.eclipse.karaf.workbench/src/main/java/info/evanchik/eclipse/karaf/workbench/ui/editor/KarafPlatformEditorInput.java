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

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafPlatformEditorInput extends AbstractEditorInput implements EditableObject<KarafPlatformModel> {

    private final KarafPlatformModel karafPlatform;

    /**
     *
     * @param karafPlatform
     */
    public KarafPlatformEditorInput(final KarafPlatformModel karafPlatform) {
        this.karafPlatform = karafPlatform;
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

        return true;
    }

    @Override
    public String getName() {
        return karafPlatform.getRootDirectory().lastSegment();
    }

    @Override
    public KarafPlatformModel getObject() {
        return karafPlatform;
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
        return result;
    }

    @Override
    public boolean isNewObject() {
        return false;
    }


}
