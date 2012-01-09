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

import org.apache.karaf.eik.core.KarafCorePluginUtils;
import org.apache.karaf.eik.core.features.internal.ElementTransformer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.list.TransformedList;
import org.jdom.Element;

public final class Features implements ParentAwareObject<FeaturesRepository> {

    private final Element element;

    private final FeaturesRepository featuresRepository;

    private final String name;

    public Features(final Element element, final FeaturesRepository featuresRepository) {
        this.name = element.getAttributeValue("name");
        this.element = element;
        this.featuresRepository = featuresRepository;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Features)) {
            return false;
        }

        final Features other = (Features) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    public List<Feature> getFeatures() {
        final List<Feature> rawList = new ArrayList<Feature>();
        final List<Feature> transformedList = TransformedList.decorate(rawList, new ElementTransformer());
        transformedList.addAll(element.getChildren());

        return Collections.unmodifiableList(KarafCorePluginUtils.filterList(transformedList, new FeatureOnlyPredicate()));
    }

    public String getName() {
        return name;
    }

    @Override
    public FeaturesRepository getParent() {
        return featuresRepository;
    }

    @SuppressWarnings("unchecked")
    public List<Repository> getRepositories() {
        final List<Repository> rawList = new ArrayList<Repository>();
        final List<Repository> transformedList = TransformedList.decorate(rawList, new ElementTransformer());
        transformedList.addAll(element.getChildren());

        return Collections.unmodifiableList(KarafCorePluginUtils.filterList(transformedList, new RepositoryOnlyPredicate()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return element.toString();
    }

}