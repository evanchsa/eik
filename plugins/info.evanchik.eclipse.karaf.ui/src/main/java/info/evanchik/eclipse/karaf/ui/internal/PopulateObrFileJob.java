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
package info.evanchik.eclipse.karaf.ui.internal;

import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.Bundle;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class PopulateObrFileJob extends Job {

    private static final String ANT_BUILD_OBR_FILE = "ant/build.xml";

    private final File obrFile;

    private volatile boolean obrComplete;

    /**
     * Creates a {@link Job} that will populate a {@link File} with OBR data
     *
     * @param name
     *            the name of the {@code Job}
     * @param obrFile
     *            the {@code File} where this {@code Job} will write the OBR
     *            data
     */
    public PopulateObrFileJob(final String name, final File obrFile) {
        super(name);

        this.obrFile = obrFile;
    }

    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        if (AntRunner.isBuildRunning()) {
            schedule(15 * 1000);
            return Status.OK_STATUS;
        }

        obrComplete = false;

        File buildFile;
        final AntRunner runner = new AntRunner();

        try {
            buildFile = getAntBuildFile();

            runner.setBuildFileLocation(buildFile.getAbsolutePath());
            runner.setArguments("-Dobr.filename=" + obrFile.getAbsolutePath());
            runner.run(monitor);
        } catch (final CoreException e) {
            monitor.setCanceled(true);

            return Status.CANCEL_STATUS;
        } catch (final IOException e) {
            monitor.setCanceled(true);

            return Status.CANCEL_STATUS;
        } finally {
            monitor.done();
        }

        obrComplete = true;

        return Status.OK_STATUS;
    }

    /**
     * Getter for the {@link File} that contains the OBR data
     *
     * @return the {@code File} that contains the OBR data
     */
    public File getObrFile() {
        return obrFile;
    }

    /**
     * Determines whether or the OBR file population has complated
     *
     * @return true if the OBR file is complete, false otherwise
     */
    public boolean isObrPopulationComplete() {
        return obrComplete;
    }

    /**
     * Getter for the {@code build.xml} file used to execute the
     * {@link AntRunner}
     *
     * @return the {@link File} that the {@code AntRunner} will execute
     * @throws IOException
     *             thrown if the {@code File} cannot be created
     */
    private File getAntBuildFile() throws IOException {
        final Bundle thisBundle = KarafUIPluginActivator.getDefault().getBundle();
        final URL[] urls = FileLocator.findEntries(thisBundle, new Path(ANT_BUILD_OBR_FILE));

        if (urls.length > 1 || urls.length == 0) {
            throw new AssertionError("Error locating Bundle URL for " + ANT_BUILD_OBR_FILE);
        }

        final URL fileUrl = FileLocator.toFileURL(urls[0]);

        try {
            return new File(fileUrl.toURI());
        } catch (final URISyntaxException e) {
            throw new IOException("Unable to convert URI to File", e);
        }
    }
}
