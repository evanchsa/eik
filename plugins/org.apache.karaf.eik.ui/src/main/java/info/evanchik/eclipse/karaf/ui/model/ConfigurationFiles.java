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

import info.evanchik.eclipse.karaf.ui.IKarafProject;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class ConfigurationFiles extends AbstractContentModel {

    /**
     *
     * @param project
     */
    public ConfigurationFiles(final IKarafProject project) {
        super(project);
    }

    @Override
    public Object[] getElements() {
        final List<Object> elements = new ArrayList<Object>();
        return elements.toArray();
    }

    @Override
    public Image getImage() {
        return KarafUIPluginActivator.getDefault().getImageRegistry().get("details_view");
    }

    @Override
    public String toString() {
        return "Configuration Files";
    }
}
