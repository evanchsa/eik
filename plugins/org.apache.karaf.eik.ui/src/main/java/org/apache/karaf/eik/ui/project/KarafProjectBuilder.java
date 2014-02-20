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
package org.apache.karaf.eik.ui.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.apache.karaf.eik.core.KarafCorePluginUtils;
import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.core.PropertyUtils;
import org.apache.karaf.eik.core.configuration.FeaturesSection;
import org.apache.karaf.eik.core.features.FeaturesRepository;
import org.apache.karaf.eik.ui.IKarafProject;
import org.apache.karaf.eik.ui.KarafUIPluginActivator;
import org.apache.karaf.eik.ui.features.FeaturesResolverJob;
import org.apache.karaf.eik.ui.internal.KarafLaunchUtils;
import org.apache.karaf.eik.ui.internal.PopulateObrFileJob;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetHandle;
import org.eclipse.pde.core.target.ITargetLocation;
import org.eclipse.pde.core.target.ITargetPlatformService;

public class KarafProjectBuilder extends IncrementalProjectBuilder {

    public static final String ID = "org.apache.karaf.eik.ui.karafProjectBuilder";

    public KarafProjectBuilder() { }

    @Override
    protected IProject[] build(final int kind, @SuppressWarnings("rawtypes") final Map args, final IProgressMonitor monitor) throws CoreException {
        final IProject project = getProject();

        monitor.beginTask("Building Apache Karaf project: " + project.getName(), 1);

        try {
            if (kind == IncrementalProjectBuilder.FULL_BUILD) {
                fullBuild(monitor);
            } else {
                final IResourceDelta delta = getDelta(getProject());
                if (delta == null) {
                    fullBuild(monitor);
                } else {
                    incrementalBuild(delta, monitor);
                }
            }

            getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
        } finally {
            monitor.done();
        }

        return null;
    }

    /**
     * Filters all of the JAR entries that begin with {@code org/osgi}.
     *
     * @param karafJar the source JAR
     * @throws CoreException if there is a problem filtering the input JAR's contents
     */
    private void filterOsgiInterfaceClasses(final File karafJar) throws CoreException {
        final IKarafProject karafProject = getKarafProject();

        final IFile generatedKarafFile = karafProject.getFile("runtime");
        final IPath path = generatedKarafFile.getRawLocation();

        JarInputStream sourceJar = null;
        JarOutputStream destJar = null;

        try {
            sourceJar = new JarInputStream(new FileInputStream(karafJar));
            final File filteredKarafJar = new File(path.toFile(), "generatedKaraf.jar");

            final Manifest mf = sourceJar.getManifest();
            if (mf != null) {
                destJar = new JarOutputStream(new FileOutputStream(filteredKarafJar), mf);
            } else {
                destJar = new JarOutputStream(new FileOutputStream(filteredKarafJar));
            }

            ZipEntry z = sourceJar.getNextEntry();
            while (z != null) {
                if (!z.getName().startsWith("org/osgi")) {
                    destJar.putNextEntry(z);

                    copyJarEntryData(sourceJar, destJar);
                } else {
                    sourceJar.closeEntry();
                }

                z = sourceJar.getNextEntry();
            }
        } catch (final FileNotFoundException e) {
            throw new CoreException(new Status(IStatus.ERROR, KarafUIPluginActivator.PLUGIN_ID, "Could not filter OSGi Interfaces from JAR", e));
        } catch (final IOException e) {
            throw new CoreException(new Status(IStatus.ERROR, KarafUIPluginActivator.PLUGIN_ID, "Could not filter OSGi Interfaces from JAR", e));
        } finally {
            if (sourceJar != null) {
                try {
                    sourceJar.close();
                } catch (final IOException e) {
                    // ignore
                }
            }

            if (destJar != null) {
                try {
                    destJar.close();
                } catch (final IOException e) {
                    // ignore
                }
            }
        }
    }

    /**
     * Performs a full build of this {@link IKarafProject}
     *
     * @param monitor
     * @throws CoreException
     */
    private void fullBuild(final IProgressMonitor monitor) throws CoreException {
        createTargetPlatform(monitor);
        buildRuntimeProperties(monitor);
        buildFeaturesRepositories(monitor);
        buildObr(monitor);
        buildFilteredOsgiInterfaceJar(monitor);
    }

    private void buildFilteredOsgiInterfaceJar(final IProgressMonitor monitor) throws CoreException {
        final List<String> karafModelClasspath = getKarafPlatformModel().getBootClasspath();

        File karafJar = null;

        final Iterator<String> itr = karafModelClasspath.iterator();
        while (itr.hasNext()) {
            final String classpathEntry = itr.next();
            karafJar = new File(classpathEntry);

            if (!karafJar.getName().equalsIgnoreCase("karaf.jar")) {
                continue;
            }

            filterOsgiInterfaceClasses(karafJar);
        }
    }

    private void buildRuntimeProperties(final IProgressMonitor monitor) throws CoreException {
        final String karafHome = getKarafPlatformModel().getRootDirectory().toOSString();

        final Properties combinedProperties = new Properties();
        combinedProperties.put("karaf.home", karafHome);
        combinedProperties.put("karaf.base", karafHome);
        combinedProperties.put("karaf.data", getKarafPlatformModel().getRootDirectory().append("data").toOSString());
        // Add ref to karaf.etc for karaf-3.0.0
        combinedProperties.put("karaf.etc", getKarafPlatformModel().getRootDirectory().append("etc").toOSString());

        for (final String filename : new String[]{"config.properties", "system.properties", "users.properties"}) {
            final Properties fileProperties = KarafCorePluginUtils.loadProperties(getKarafPlatformModel().getConfigurationDirectory().toFile(), filename, true);
            combinedProperties.putAll(fileProperties);
        }

        PropertyUtils.interpolateVariables(combinedProperties, combinedProperties);

        final IFolder runtimeFolder = getKarafProject().getFolder("runtime");
        if (!runtimeFolder.exists()) {
            runtimeFolder.create(true, true, monitor);
        }

        final IPath runtimeProperties = runtimeFolder.getRawLocation().append("runtime").addFileExtension("properties");

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(runtimeProperties.toFile());
            combinedProperties.store(out, "Combined interpolated runtime properties");
        } catch (final IOException e) {
            throw new CoreException(
                    new Status(IStatus.ERROR, KarafUIPluginActivator.PLUGIN_ID, "Unable to build runtime property file", e));
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ioException) {
                // ignore
            }
        }
    }

    /**
     * Build the Apache Karaf features repository files in this
     * {@link IKarafProject}'s project location
     *
     * @param monitor
     * @throws CoreException
     */
    private void buildFeaturesRepositories(final IProgressMonitor monitor) throws CoreException {
        final IKarafProject karafProject = getKarafProject();
        final KarafPlatformModel karafPlatformModel = getKarafPlatformModel();

        monitor.subTask("Resolving Features Repository for Apache Karaf Project: " + karafProject.getName());

        final FeaturesSection featuresSection = (FeaturesSection) karafPlatformModel.getAdapter(FeaturesSection.class);
        final FeaturesResolverJob job = new FeaturesResolverJob(karafProject.getName(), karafPlatformModel, featuresSection);
        job.schedule();
        try {
            job.join();

            final List<FeaturesRepository> featuresRepositories = job.getFeaturesRepositories();

            final IFolder folder = getKarafProject().getFolder("features");
            if (!folder.exists()) {
                folder.create(true, true, monitor);
            }

            for (final FeaturesRepository repo : featuresRepositories) {
                final IPath featuresRepositoryFilename = new Path(repo.getName()).addFileExtension("xml");
                final IFile featuresRepositoryFile = folder.getFile(featuresRepositoryFilename.toOSString());
                final File file = new File(featuresRepositoryFile.getRawLocation().toOSString());

                FileOutputStream fout = null;
                try {
                    fout = new FileOutputStream(file);
                    repo.write(fout);
                } finally {
                    if (fout != null) {
                        try {
                            fout.close();
                        } catch (final IOException e) {
                            // This space left blank
                        }
                    }
                }
            }

            monitor.worked(1);
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Builds the OBR file for the installed Eclipse platform in this
     * {@link IKarafProject}'s project location
     *
     * @param monitor
     * @throws CoreException
     */
    private void buildObr(final IProgressMonitor monitor) throws CoreException {
        final IKarafProject karafProject = getKarafProject();

        monitor.subTask("Creating OBR for Apache Karaf Project: " + karafProject.getName());

        final IPath obrFile = karafProject.getFolder("platform").getRawLocation().append("eclipse.obr").addFileExtension("xml");

        final PopulateObrFileJob populateObrJob = new PopulateObrFileJob(karafProject.getName(), obrFile.toFile());
        populateObrJob.schedule();

        try {
            populateObrJob.join();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Copies the data of a {@link JarEntry} or {@link ZipEntry} from one JAR to
     * another
     *
     * @param in  the source JAR {@link JarInputStream}
     * @param out the destination JAR {@link JarOutputStream}
     * @throws IOException thrown if there is a problem copying the data
     */
    private void copyJarEntryData(final JarInputStream in, final JarOutputStream out) throws IOException {
        final byte buffer[] = new byte[4096];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) > 0) {
            out.write(buffer, 0, bytesRead);
        }
    }

    private IFile createTargetDefinitionFile() {
        final String projectName = getKarafProject().getName();
        final IPath targetFilename = new Path(projectName).addFileExtension("target");
        return getKarafProject().getProjectHandle().getFile(targetFilename);
    }

    private void createTargetPlatform(final IProgressMonitor monitor) throws CoreException {
        final ITargetPlatformService targetPlatformService = (ITargetPlatformService) KarafUIPluginActivator.getDefault().getService(ITargetPlatformService.class.getName());

        final IFile targetLocation = createTargetDefinitionFile();
        final ITargetHandle targetHandle = targetPlatformService.getTarget(targetLocation);

        final ITargetDefinition target = targetHandle.getTargetDefinition();

        target.setName(getKarafProject().getName());

        final List<ITargetLocation> bundleContainers = KarafLaunchUtils.getBundleContainers(getKarafPlatformModel());
        target.setTargetLocations(bundleContainers.toArray(new ITargetLocation[0]));

        targetPlatformService.saveTargetDefinition(target);
    }

    /**
     * Getter for the {@link IKarafProject} of this builder's {@link IProject}
     *
     * @return the {@code IKarafProject}
     */
    private IKarafProject getKarafProject() {
        final IProject project = getProject();
        return (IKarafProject) project.getAdapter(IKarafProject.class);
    }

    /**
     * Getter for the {@link KarafPlatformModel} that this builder's
     * {@link IProject} is bound to
     *
     * @return the {@code KarafPlatformModel} that this builder's
     *         {@code IProject} is bound to
     */
    private KarafPlatformModel getKarafPlatformModel() {
        return (KarafPlatformModel) getKarafProject().getAdapter(KarafPlatformModel.class);
    }

    /**
     * Performs an incremental build of this {@link IKarafProject}
     *
     * @param delta
     * @param monitor
     * @throws CoreException
     */
    private void incrementalBuild(final IResourceDelta delta, final IProgressMonitor monitor) throws CoreException {
        try {
            delta.accept(new IResourceDeltaVisitor() {
                @Override
                public boolean visit(final IResourceDelta delta) {
                    // if is a bundle file then schedule an update to the target platform file
                    // if something in eclipse home changes then update obr
                    // if it is a feature file update features repo
                    return true;
                }
            });
            fullBuild(monitor);
        } catch (final CoreException e) {
            e.printStackTrace();
        }
    }

}
