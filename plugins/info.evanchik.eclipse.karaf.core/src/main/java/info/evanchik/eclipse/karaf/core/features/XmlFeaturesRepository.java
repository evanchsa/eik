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
    public List<Repository> getRepositories() {
        return Collections.unmodifiableList(features.getRepositories());
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
    public String toString() {
        return repositoryName;
    }
}
