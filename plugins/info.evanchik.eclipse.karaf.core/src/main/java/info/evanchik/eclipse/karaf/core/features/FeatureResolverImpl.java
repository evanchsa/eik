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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class FeatureResolverImpl {

    private final List<FeaturesRepository> featuresRepositories = new ArrayList<FeaturesRepository>();

    /**
     *
     * @param repositories
     */
    public FeatureResolverImpl(final Collection<FeaturesRepository> repositories) {
        featuresRepositories.addAll(repositories);
    }

    /**
     *
     * @param featureName
     * @return
     */
    public Feature findFeature(final String featureName) {
        final Object[] path = getFeaturePath(featureName);

        if (path.length > 0) {
            return (Feature) path[path.length - 1];
        } else {
            return null;
        }
    }

    public Object[] getFeaturePath(final String featureName) {
        final List<Object> path = new ArrayList<Object>();

        for (final FeaturesRepository r : featuresRepositories) {
            for (final Feature f : r.getFeatures().getFeatures()) {
                if (f.getName().equals(featureName)) {
                    path.addAll(Arrays.asList(r, r.getFeatures(), f));
                    return path.toArray();
                }
            }
        }

        return path.toArray();
    }
}
