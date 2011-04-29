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


import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class XmlFeaturesRepository implements FeaturesRepository {

    private final Features features;

    private final String repositoryName;

    /**
     *
     * @param repositoryName
     * @param inputStream
     * @throws IOException
     * @throws NullPointerException
     *             if any argument is null
     */
    public XmlFeaturesRepository(final String repositoryName, final InputStream inputStream) throws IOException {
        if (repositoryName == null) {
            throw new NullPointerException("repositoryName");
        }

        if (inputStream == null) {
            throw new NullPointerException("inputStream");
        }

        try {
            final Document document = new SAXBuilder().build(inputStream);

            final Element rootElement = document.getRootElement();
            if (rootElement == null) {
                // This is bad!
            }

            this.features = new Features(rootElement, this);
            this.repositoryName = repositoryName;
        } catch (final JDOMException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof XmlFeaturesRepository)) {
            return false;
        }

        final XmlFeaturesRepository other = (XmlFeaturesRepository) obj;
        if (features == null) {
            if (other.features != null) {
                return false;
            }
        } else if (!features.equals(other.features)) {
            return false;
        }

        if (repositoryName == null) {
            if (other.repositoryName != null) {
                return false;
            }
        } else if (!repositoryName.equals(other.repositoryName)) {
            return false;
        }
        return true;
    }

    @Override
    public Features getFeatures() {
        return features;
    }

    @Override
    public String getName() {
        return repositoryName;
    }

    @Override
    public List<Repository> getRepositories() {
        return Collections.unmodifiableList(features.getRepositories());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (features == null ? 0 : features.hashCode());
        result = prime * result + (repositoryName == null ? 0 : repositoryName.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return repositoryName;
    }
}
