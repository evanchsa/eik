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
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.graphics.Image;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class BootClasspath extends AbstractContentModel {

    /**
     *
     * @param project
     * @param karafPlatformModel
     */
    public BootClasspath(final IProject project, final KarafPlatformModel karafPlatformModel) {
        super(project, karafPlatformModel);
    }

    /**
     *
     * @return
     */
    @Override
    public Object[] getElements() {
        final List<String> bootClasspath = karafPlatformModel.getBootClasspath();

        final File[] files = new File[bootClasspath.size()];
        for (int i = 0; i < files.length; i++) {
            files[i] = new File(bootClasspath.get(i));
        }

        return files;
    }

    @Override
    public Image getImage() {
        return KarafUIPluginActivator.getDefault().getImageRegistry().get("runtime_obj");
    }

    @Override
    public String toString() {
        return "Boot classpath";
    }
}
