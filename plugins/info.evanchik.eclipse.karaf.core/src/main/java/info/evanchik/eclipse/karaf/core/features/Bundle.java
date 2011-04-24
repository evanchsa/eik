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
package info.evanchik.eclipse.karaf.core.features;

import info.evanchik.eclipse.karaf.core.features.internal.ElementTransformer;

import org.apache.commons.collections.Transformer;
import org.jdom.Element;

/**
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public final class Bundle {

    private final String bundleUrl;

    private final Element element;

    /**
     *
     * @param element
     */
    public Bundle(final Element element) {
        bundleUrl = element.getValue();
        if (bundleUrl == null) {
            throw new IllegalArgumentException("bundle element is invalid: " + element.toString());
        }

        this.element = element;
    }

    public String getBundleUrl() {
        return bundleUrl;
    }

    public Object getParent() {
        final Transformer transformer = new ElementTransformer();
        return transformer.transform(element.getParentElement());
    }

    @Override
    public String toString() {
        return bundleUrl;
    }
}