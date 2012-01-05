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
package info.evanchik.eclipse.karaf.ui.project.impl;

import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.ui.IKarafProject;
import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class FilteredOsgiFrameworkJarBuildUnit extends AbstractKarafBuildUnit {

    /**
     * @param karafPlatformModel
     * @param karafProject
     */
    public FilteredOsgiFrameworkJarBuildUnit(final KarafPlatformModel karafPlatformModel, final IKarafProject karafProject) {
        super(karafPlatformModel, karafProject);
    }

    @Override
    public void build(final int kind, @SuppressWarnings("rawtypes") final Map args, final IProgressMonitor monitor) throws CoreException {
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

    /**
     * Copies the data of a {@link JarEntry} or {@link ZipEntry} from one JAR to
     * another
     *
     * @param in
     *            the source JAR {@link JarInputStream}
     * @param out
     *            the destination JAR {@link JarOutputStream}
     * @throws IOException
     *             thrown if there is a problem copying the data
     */
    private void copyJarEntryData(final JarInputStream in, final JarOutputStream out) throws IOException {
        final byte buffer[] = new byte[4096];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) > 0) {
            out.write(buffer, 0, bytesRead);
        }
    }

    /**
     * Filters all of the JAR entries that begin with {@code org/osgi}.
     *
     * @param karafJar
     *            the source JAR
     * @throws CoreException
     *             if there is a problem filtering the input JAR's contents
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
}
