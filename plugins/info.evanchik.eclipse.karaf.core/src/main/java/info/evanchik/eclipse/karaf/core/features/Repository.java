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

import java.net.URI;

import org.apache.commons.collections.Transformer;
import org.jdom.Element;

/**
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public final class Repository implements ParentAwareObject<Object> {

    private final Element element;

    private final String repositoryUrl;

    public Repository(final Element element) {
        repositoryUrl = element.getValue();
        if (repositoryUrl == null) {
            throw new IllegalArgumentException("repository element is invalid: " + element.toString());
        }

        this.element = element;
    }

    @Override
    public Object getParent() {
        final Transformer transformer = new ElementTransformer();
        return transformer.transform(element.getParentElement());
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    @Override
    public String toString() {
        return repositoryUrl;
    }

    /**
     *
     * @return
     */
    public URI toURI() {
        return URI.create(repositoryUrl);
    }
}