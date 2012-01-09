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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FeatureResolverImpl {

    private final List<FeaturesRepository> featuresRepositories = new ArrayList<FeaturesRepository>();

    public FeatureResolverImpl(final Collection<FeaturesRepository> repositories) {
        featuresRepositories.addAll(repositories);
    }

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
