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
        for (final FeaturesRepository r : featuresRepositories) {
            for (final Feature f : r.getFeatures().getFeatures()) {
                if (f.getName().equals(featureName)) {
                    return f;
                }
            }
        }

        return null;
    }
}
