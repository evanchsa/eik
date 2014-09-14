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

import org.apache.karaf.eik.core.KarafCorePluginUtils;
import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.core.PropertyUtils;
import org.apache.karaf.eik.core.configuration.FeaturesSection;
import org.apache.karaf.eik.core.features.FeaturesRepository;
import org.apache.karaf.eik.core.features.XmlFeaturesRepository;
import org.apache.karaf.eik.ui.IKarafProject;
import org.apache.karaf.eik.ui.KarafUIPluginActivator;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.ops4j.pax.url.mvn.MvnURLConnectionFactory;

/**
 * A {@link Job} that loads an Apache Karaf features configuration file
 * {@code org.apache.karaf.features.cfg} and examines all of the referenced
 * Features Repositories. It then attempts to resolve each of the repositories
 * in order to produce a {@link List} of {@link FeaturesRepository}
 */
public final class FeaturesResolverJob extends Job {

    private static final String ORG_OPS4J_PAX_URL_MVN_CFG = "org.ops4j.pax.url.mvn.cfg";

    private final List<FeaturesRepository> featuresRepositories =
            Collections.synchronizedList(new ArrayList<FeaturesRepository>());

    private final FeaturesSection featuresSection;

    private final KarafPlatformModel karafPlatformModel;

    public FeaturesResolverJob(final String name, final KarafPlatformModel karafPlatformModel, final FeaturesSection featuresSection) {
        super("Resolving Features for " + name);

        this.featuresSection = featuresSection;
        this.karafPlatformModel = karafPlatformModel;
    }

    /**
     * Getter for the {@link List} of {@link FeaturesRepository} objects. This
     * {@code List} is read-only and is a synchronized list via
     * {@link Collections#synchronizedList(List)}.
     *
     * @return the {@link List} of {@link FeaturesRepository} objects.
     */
    public List<FeaturesRepository> getFeaturesRepositories() {
        return Collections.unmodifiableList(featuresRepositories);
    }

    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        featuresSection.load();
        featuresRepositories.clear();

        return resolveFeatures(monitor);
    }

    /**
     * Helper method that resolves Karaf Features.
     *
     * @param monitor the {@link IProgressMonitor} instance
     * @return the {@link Status#OK_STATUS} if the Features are successfully
     * resolved
     */
    private IStatus resolveFeatures(final IProgressMonitor monitor) {
        monitor.beginTask("Loading Karaf Features", featuresSection.getRepositoryList().size());
        try {

            for (final String repository : featuresSection.getRepositoryList()) {

                if (monitor.isCanceled()) {
                    return Status.CANCEL_STATUS;
                }

                try {
                    // Begin: Refactor this out in to an OPS4j mvn URL configuration
                    final Properties mvnConfiguration =
                            KarafCorePluginUtils.loadProperties(karafPlatformModel.getConfigurationDirectory().toFile(), ORG_OPS4J_PAX_URL_MVN_CFG);

                    final IKarafProject karafProject = (IKarafProject) karafPlatformModel.getAdapter(IKarafProject.class);
                    final Properties runtimeProperties = karafProject.getRuntimeProperties();

                    PropertyUtils.interpolateVariables(mvnConfiguration, runtimeProperties);

                    final String defaultRepos = (String) mvnConfiguration.get("org.ops4j.pax.url.mvn.defaultRepositories");
                    final String repos = (String) mvnConfiguration.get("org.ops4j.pax.url.mvn.repositories");

                    // In karaf-3.0.0, default repo may be null.
                    // First check if it's null an if not then add it to repo list
                    ArrayList<String> reposList = new ArrayList<String>();
                    if (defaultRepos != null)
                        reposList.add(defaultRepos);
                    if (repos != null)
                        reposList.add(repos);
                    final String combinedRepos = KarafCorePluginUtils.join(reposList, ",");

                    mvnConfiguration.put("org.ops4j.pax.url.mvn.repositories", removeInvalidSuffixes(combinedRepos));
                    // End: Refactor

                    final String repositoryName;
                    final InputStream stream;
                    if (repository.startsWith(FeaturesLabelProvider.MVN_URL_PREFIX)) {
                        final MvnURLConnectionFactory urlConnectionFactory = new MvnURLConnectionFactory(mvnConfiguration);
                        stream = urlConnectionFactory.create(new URL(repository)).getInputStream();

                        final String[] repositoryComponents = repository.split("/"); //$NON-NLS-1$
                        repositoryName = repositoryComponents[1] + "-" + repositoryComponents[2]; //$NON-NLS-1$
                    } else {
                        stream = new URL(repository).openStream();

                        repositoryName = repository;
                    }

                    final FeaturesRepository newRepo = new XmlFeaturesRepository(repositoryName, stream);
                    featuresRepositories.add(newRepo);

                    monitor.worked(1);

                } catch (final MalformedURLException e) {
                    if (monitor.isCanceled()) {
                        return Status.CANCEL_STATUS;
                    } else {
                        return new Status(IStatus.WARNING, KarafUIPluginActivator.PLUGIN_ID, "Unable determine location for Features repository: " + repository, e);
                    }
                } catch (final IOException e) {
                    if (monitor.isCanceled()) {
                        return Status.CANCEL_STATUS;
                    } else {
                        return new Status(IStatus.WARNING, KarafUIPluginActivator.PLUGIN_ID, "Unable load Features repository: " + repository, e);
                    }
                } catch (final CoreException e) {
                    if (monitor.isCanceled()) {
                        return Status.CANCEL_STATUS;
                    } else {
                        return new Status(IStatus.ERROR, KarafUIPluginActivator.PLUGIN_ID, "Unable load Features repository: " + repository, e);
                    }
                }
            }

            return Status.OK_STATUS;
        } finally {
            monitor.done();
        }
    }

    private String removeInvalidSuffixes(String mergedRepositories) {
        String[] repositories = mergedRepositories.split(",");

        for (int i = 0; i < repositories.length; i++) {
            String repository = repositories[0];
            String[] segments = repository.split("@");
            StringBuilder urlBuilder = new StringBuilder(segments[0]);
            for (int j = 0; j < segments.length; ++j) {
                String segment = segments[j];
                if (segment.trim().equalsIgnoreCase("snapshots") || segment.trim().equalsIgnoreCase("noreleases")) {
                    urlBuilder.append("@");
                    urlBuilder.append(segment);
                }
            }
            repositories[i] = urlBuilder.toString();
        }

        return KarafCorePluginUtils.join(Arrays.asList(repositories), ",");
    }

}
