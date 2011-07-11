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
package info.evanchik.eclipse.karaf.obr.impl;

import info.evanchik.eclipse.karaf.obr.KarafObrActivator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

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

    private volatile File obrFile;

    private volatile boolean obrComplete;

    public PopulateObrFileJob(final String name) {
        super(name);
    }

    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        final String uuid = UUID.randomUUID().toString();
        final String baseObrFilename = "obr-" + uuid;
        final String finalObrFilename = baseObrFilename + ".gz";

        if (AntRunner.isBuildRunning()) {
            schedule(15 * 1000);
        }

        obrComplete = false;
        obrFile = new File(finalObrFilename);

        File buildFile;
        final AntRunner runner = new AntRunner();

        try {
            buildFile = getFile();

            runner.setBuildFileLocation(buildFile.getAbsolutePath());
            runner.setArguments("-Dobr.filename=" + baseObrFilename);
            runner.run(monitor);

            obrFile.deleteOnExit();
        } catch (final CoreException e) {
            e.printStackTrace();

            return Status.CANCEL_STATUS;
        } catch (final IOException e) {
            e.printStackTrace();

            return Status.CANCEL_STATUS;
        } finally {
            monitor.done();
        }

        obrComplete = true;

        return Status.OK_STATUS;
    }

    /**
     *
     * @return
     */
    public File getObrFile() {
        return obrFile;
    }

    /**
     *
     * @return
     */
    public boolean isObrPopulationComplete() {
        return obrComplete;
    }

    /**
     *
     * @return
     * @throws IOException
     */
    protected File getFile() throws IOException {
        final Bundle thisBundle = KarafObrActivator.getDefault().getBundle();
        final URL[] urls = FileLocator.findEntries(thisBundle, new Path(ANT_BUILD_OBR_FILE));

        if (urls.length > 1 || urls.length == 0) {
            // TODO: This is a problem
        }

        final URL fileUrl = FileLocator.toFileURL(urls[0]);

        try {
            return new File(fileUrl.toURI());
        } catch (final URISyntaxException e) {
            throw new IOException("Unable to convert URI to File", e);
        }
    }

}
