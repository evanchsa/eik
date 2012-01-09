/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.karaf.eik.core.features;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class XmlFeaturesRepository implements FeaturesRepository {

    private final Document document;

    private final Features features;

    private final String repositoryName;

    public XmlFeaturesRepository(final String repositoryName, final InputStream inputStream) throws IOException {
        if (repositoryName == null) {
            throw new NullPointerException("repositoryName");
        }

        if (inputStream == null) {
            throw new NullPointerException("inputStream");
        }

        try {
            document = new SAXBuilder().build(inputStream);

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

    @Override
    public void write(final OutputStream out) throws IOException {
        final XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        xmlOutputter.output(document, out);
    }

}
