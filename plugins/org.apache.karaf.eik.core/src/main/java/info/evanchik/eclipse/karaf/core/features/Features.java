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

import org.apache.commons.collections.list.TransformedList;
import org.jdom.Element;

/**
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public final class Features implements ParentAwareObject<FeaturesRepository> {

    private final Element element;

    private final FeaturesRepository featuresRepository;

    private final String name;

    /**
     *
     * @param element
     * @param featuresRepository
     */
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