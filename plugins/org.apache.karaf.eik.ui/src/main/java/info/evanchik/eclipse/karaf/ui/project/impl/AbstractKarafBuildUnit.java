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
package info.evanchik.eclipse.karaf.ui.project.impl;

import org.apache.karaf.eik.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.ui.IKarafProject;
import info.evanchik.eclipse.karaf.ui.project.KarafBuildUnit;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public abstract class AbstractKarafBuildUnit implements KarafBuildUnit {

    private final KarafPlatformModel karafPlatformModel;

    private final IKarafProject karafProject;

    /**
     *
     * @param karafPlatformModel
     * @param karafProject
     */
    public AbstractKarafBuildUnit(final KarafPlatformModel karafPlatformModel, final IKarafProject karafProject) {
        this.karafPlatformModel = karafPlatformModel;
        this.karafProject = karafProject;
    }

    protected final KarafPlatformModel getKarafPlatformModel() {
        return karafPlatformModel;
    }

    protected final IKarafProject getKarafProject() {
        return karafProject;
    }
}
