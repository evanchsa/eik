/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
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