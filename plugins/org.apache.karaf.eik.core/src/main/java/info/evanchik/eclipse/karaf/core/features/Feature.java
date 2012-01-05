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

import info.evanchik.eclipse.karaf.core.KarafCorePluginUtils;
import info.evanchik.eclipse.karaf.core.features.internal.ElementTransformer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.list.TransformedList;
import org.jdom.Element;

/**
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public final class Feature implements ParentAwareObject<Object> {

    private final String name;

    private final String version;

    private final Element element;

    /**
     *
     * @author Stephen Evanchik (evanchsa@gmail.com)
     *
     */
    private static final class BundleOnlyPredicate implements Predicate {
        @Override
        public boolean evaluate(final Object element) {
            return element instanceof Bundle;
        }
    }

    /**
     *
     * @param element
     */
    public Feature(final Element element) {
        if (element.getAttribute("name") != null) {
            name = element.getAttributeValue("name");
        } else {
            name = element.getValue();
        }

        version = element.getAttributeValue("version");

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
}