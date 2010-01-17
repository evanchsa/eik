/**
 * Copyright (c) 2009 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package info.evanchik.eclipse.karaf.hooks;

import info.evanchik.eclipse.karaf.hooks.impl.SystemPropertyLoader;

import java.io.IOException;
import java.net.URLConnection;
import java.util.Properties;

import org.eclipse.osgi.baseadaptor.BaseAdaptor;
import org.eclipse.osgi.baseadaptor.hooks.AdaptorHook;
import org.eclipse.osgi.framework.log.FrameworkLog;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafAdapterHook implements AdaptorHook {

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.osgi.baseadaptor.hooks.AdaptorHook#addProperties(java.util
     * .Properties)
     */
    public void addProperties(Properties properties) {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.osgi.baseadaptor.hooks.AdaptorHook#createFrameworkLog()
     */
    public FrameworkLog createFrameworkLog() {
        return null;
    }

    /**
     * Loads the Karaf system properties when the OSGi framework starts.
     *
     * @see org.eclipse.osgi.baseadaptor.hooks.AdaptorHook#frameworkStart(org.osgi
     *      .framework.BundleContext)
     */
    public void frameworkStart(BundleContext context) throws BundleException {
        final SystemPropertyLoader loader = new SystemPropertyLoader();
        loader.loadSystemProperties();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.osgi.baseadaptor.hooks.AdaptorHook#frameworkStop(org.osgi
     * .framework.BundleContext)
     */
    public void frameworkStop(BundleContext context) throws BundleException {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.osgi.baseadaptor.hooks.AdaptorHook#frameworkStopping(org.
     * osgi.framework.BundleContext)
     */
    public void frameworkStopping(BundleContext context) {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.osgi.baseadaptor.hooks.AdaptorHook#handleRuntimeError(java
     * .lang.Throwable)
     */
    public void handleRuntimeError(Throwable error) {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.osgi.baseadaptor.hooks.AdaptorHook#initialize(org.eclipse
     * .osgi.baseadaptor.BaseAdaptor)
     */
    public void initialize(BaseAdaptor adaptor) {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.osgi.baseadaptor.hooks.AdaptorHook#mapLocationToURLConnection
     * (java.lang.String)
     */
    public URLConnection mapLocationToURLConnection(String location)
            throws IOException {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.osgi.baseadaptor.hooks.AdaptorHook#matchDNChain(java.lang
     * .String, java.lang.String[])
     */
    public boolean matchDNChain(String pattern, String[] dnChain) {
        return false;
    }

}
