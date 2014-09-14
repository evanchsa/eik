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

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.list.TransformedList;
import org.jdom.Element;

public final class Feature implements ParentAwareObject<Object> {

    private final String name;

    private final String version;

    private final Element element;

    private final String startLevel;

    private static final class BundleOnlyPredicate implements Predicate {
        @Override
        public boolean evaluate(final Object element) {
            return element instanceof Bundle;
        }
    }

    public Feature(final Element element) {
        if (element.getAttribute("name") != null) {
            name = element.getAttributeValue("name");
        } else {
            name = element.getValue();
        }

        version = element.getAttributeValue("version");
  
        startLevel = element.getAttributeValue("start-level");

        this.element = element;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Feature)) {
            return false;
        }

        final Feature other = (Feature) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }

        return true;
    }

    public String getName() {
        return name;
    }

    @Override
    public Object getParent() {
        final Transformer transformer = new ElementTransformer();
        return transformer.transform(element.getParentElement());
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("");

        if (name != null) {
            sb.append(name);
        }

        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public List<Bundle> getBundles() {
        final List<Bundle> rawList = new ArrayList<Bundle>();
        final List<Bundle> transformedList = TransformedList.decorate(rawList, new ElementTransformer());
        transformedList.addAll(element.getChildren());

        return Collections.unmodifiableList(KarafCorePluginUtils.filterList(transformedList, new BundleOnlyPredicate()));
    }

    @SuppressWarnings("unchecked")
    public List<Feature> getFeatures() {
        final List<Feature> rawList = new ArrayList<Feature>();
        final List<Feature> transformedList = TransformedList.decorate(rawList, new ElementTransformer());
        transformedList.addAll(element.getChildren());

        return Collections.unmodifiableList(KarafCorePluginUtils.filterList(transformedList, new FeatureOnlyPredicate()));
    }

    public String getStartLevel() {
        return startLevel;
    }

}
