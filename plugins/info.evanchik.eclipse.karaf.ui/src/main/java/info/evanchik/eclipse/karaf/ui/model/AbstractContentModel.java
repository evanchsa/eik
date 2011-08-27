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
package info.evanchik.eclipse.karaf.ui.model;

import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.ui.IKarafProject;

import org.eclipse.core.runtime.PlatformObject;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public abstract class AbstractContentModel extends PlatformObject implements ContentModel {

    protected final KarafPlatformModel karafPlatformModel;

    protected final IKarafProject project;

    /**
     *
     * @param project
     */
    public AbstractContentModel(final IKarafProject project) {
        this.project = project;
        this.karafPlatformModel = (KarafPlatformModel) project.getAdapter(KarafPlatformModel.class);
    }

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") final Class adapter) {
        if (KarafPlatformModel.class.equals(adapter)) {
            return karafPlatformModel;
        } else {
            return super.getAdapter(adapter);
        }
    }

    @Override
    public Object getParent() {
        return project.getProjectHandle();
    }
}
