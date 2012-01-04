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
package info.evanchik.eclipse.karaf.core.features.internal;

import info.evanchik.eclipse.karaf.core.features.Bundle;
import info.evanchik.eclipse.karaf.core.features.Feature;
import info.evanchik.eclipse.karaf.core.features.Features;
import info.evanchik.eclipse.karaf.core.features.Repository;

import org.apache.commons.collections.Transformer;
import org.jdom.Element;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class ElementTransformer implements Transformer {

    @Override
    public Object transform(final Object object) {
        if (!(object instanceof Element)) {
            return null;
        }

        final Element element = (Element) object;

        if (element.getName().equalsIgnoreCase("repository")) {
            return new Repository(element);
        } else if (element.getName().equalsIgnoreCase("feature")) {
            return new Feature(element);
        } else if (element.getName().equalsIgnoreCase("bundle")) {
            return new Bundle(element);
        } else if (element.getName().equalsIgnoreCase("features")) {
            return new Features(element, null);
        } else {
            return object;
        }
    }
}
