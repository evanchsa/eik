package info.evanchik.eclipse.karaf.ui.navigator;
import info.evanchik.eclipse.karaf.ui.model.ContentModel;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

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

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafPlatformModelLabelProvider extends LabelProvider implements ILabelProvider {

    public KarafPlatformModelLabelProvider() {
        super();
    }

    @Override
    public Image getImage(final Object element) {
        if (element instanceof ContentModel) {
            return ((ContentModel) element).getImage();
        }

        return super.getImage(element);
    }

    @Override
    public String getText(final Object element) {
        return super.getText(element);
    }
}
