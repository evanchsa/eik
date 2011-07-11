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
package info.evanchik.eclipse.karaf.obr;

import info.evanchik.eclipse.karaf.core.LogWrapper;
import info.evanchik.eclipse.karaf.obr.impl.PopulateObrFileJob;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.osgi.framework.BundleContext;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafObrActivator extends Plugin {

    /**
     *
     * @author Stephen Evanchik (evanchsa@gmail.com)
     *
     */
    private static final class EmptyInputStream extends InputStream {
        @Override
        public int read() throws IOException {
            return -1;
        }

        @Override
        public int read(final byte[] b) throws IOException {
            return -1;
        }

        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            return -1;
        }
    }

    /**
     *
     * @author Stephen Evanchik (evanchsa@gmail.com)
     *
     */
    private final class ObrJobListener extends JobChangeAdapter {

        @Override
        public void done(final IJobChangeEvent event) {
            if (event.getResult().isOK()) {
            } else {
                // TODO: What to do?
            }
        }
    }

    public static final String ID = "info.evanchik.eclipse.karaf.obr"; //$NON-NLS-1$

    private static final EmptyInputStream emptyInputStream = new EmptyInputStream();

    private static KarafObrActivator plugin = null;

    /**
     * Returns the shared instance of this plugin.
     *
     * @return the shared instance
     */
    public static KarafObrActivator getDefault() {
        return plugin;
    }

    public static LogWrapper getLogger() {
        return new LogWrapper(getDefault().getLog(), ID);
    };

    private final ObrJobListener obrJobListener = new ObrJobListener();

    private final PopulateObrFileJob populateObrJob = new PopulateObrFileJob("Karaf OBR Population Job");

    /**
     *
     * @return
     */
    public InputStream getObrInputStream() {
        if (!populateObrJob.isObrPopulationComplete()) {
            return emptyInputStream;
        }

        final File obrFile = populateObrJob.getObrFile();

        try {
            final FileInputStream fin = new FileInputStream(obrFile);
            final GZIPInputStream compressInputStream = new GZIPInputStream(fin);

            return compressInputStream;
        } catch (final FileNotFoundException e) {
            // TODO: What to do?
        } catch (final IOException e) {
            // TODO: What to do?
        }

        return emptyInputStream;
    }

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);

        plugin = this;

        populateObrJob.addJobChangeListener(obrJobListener);
        // populateObrJob.schedule(30 * 1000);
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        populateObrJob.cancel();

        super.stop(context);

        plugin = null;
    }
}
