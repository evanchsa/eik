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
package org.apache.karaf.eik.ui.features;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.karaf.eik.core.IKarafConstants;
import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.core.configuration.FeaturesSection;
import org.apache.karaf.eik.core.features.Bundle;
import org.apache.karaf.eik.core.features.Feature;
import org.apache.karaf.eik.core.features.FeaturesRepository;
import org.apache.karaf.eik.ui.IKarafProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osgi.service.resolver.BundleDescription;

public class FeaturesBundlesStartLevels {

	private final IKarafProject karafProject;
	private final Map<String, String> symbolicNameToStartLevel;
	private final MvnUrlConverter converter;
	
	private KarafPlatformModel karafPlatformModel;
	private String baseBundlesDir;

	public FeaturesBundlesStartLevels(IKarafProject karafProject) {
		this.karafProject = karafProject;
		this.symbolicNameToStartLevel = new HashMap<String, String>();
		this.converter = new MvnUrlConverter();
	}
	
	public void load(){
		karafPlatformModel = (KarafPlatformModel) karafProject.getAdapter(KarafPlatformModel.class);
		baseBundlesDir = getBaseBundleDir();
		
        final FeaturesSection featuresSection = (FeaturesSection) karafPlatformModel.getAdapter(FeaturesSection.class);
        final FeaturesResolverJob job = new FeaturesResolverJob(karafProject.getName(), karafPlatformModel, featuresSection);
        job.run(new NullProgressMonitor());
        List<FeaturesRepository> featuresRepositories = job.getFeaturesRepositories();
        for (FeaturesRepository featuresRepository : featuresRepositories) {
			List<Feature> features = featuresRepository.getFeatures().getFeatures();
			for (Feature feature : features) {
				String featureStartLevel = feature.getStartLevel();
				List<Bundle> bundles = feature.getBundles();
				for (Bundle bundle : bundles) {
					String bundleStartLevel = bundle.getStartLevel();
					
					String startLevel = (bundleStartLevel != null) ? bundleStartLevel : featureStartLevel; 
					if (startLevel != null) {
						String symbolicName = getSymbolicName(bundle.getBundleUrl());
						if (symbolicName != null) {
							symbolicNameToStartLevel.put(symbolicName, startLevel);
						}
					}
				}
			}
		}
	}

    private String getBaseBundleDir() {
        Properties runtimeProperties = karafProject.getRuntimeProperties();
		String karafHome = (String) runtimeProperties.get(IKarafConstants.KARAF_HOME_PROP);
		String defaultRepository = (String) runtimeProperties.get("karaf.default.repository");
		return karafHome + "/" + defaultRepository;
   }
	
	public boolean containsPlugin(String bundleSymbolicName) {
		return symbolicNameToStartLevel.containsKey(bundleSymbolicName);
	}
	
	public String getStartLevel(String bundleSymbolicName) {
		return symbolicNameToStartLevel.get(bundleSymbolicName);
	}
	
	private String getSymbolicName(String bundleUrl) {
		File bundlePath = new File(baseBundlesDir, converter.getPath(bundleUrl));
		BundleDescription bundleDescription = karafPlatformModel.getState().getBundleByLocation(bundlePath.getAbsolutePath());
		if (bundleDescription != null) {
			return bundleDescription.getSymbolicName();
		} else {
			return null;
		}
	}
}
