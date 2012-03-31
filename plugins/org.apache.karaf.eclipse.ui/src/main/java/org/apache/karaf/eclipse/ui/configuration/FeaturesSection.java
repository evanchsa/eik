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
package org.apache.karaf.eclipse.ui.configuration;

import java.util.List;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface FeaturesSection  extends ConfigurationSection {

    /**
     *
     * @return
     */
    public List<String> getFeaturesBoot();

    /**
     *
     * @return
     */
    public List<String> getFeaturesRepositories();

    /**
     *
     * @param bootFeatureNames
     */
    public void setFeaturesBoot(List<String> bootFeatureNames);

    /**
     *
     * @param featuresRepositoryList
     */
    public void setFeaturesRepositories(List<String> featuresRepositoryList);
}