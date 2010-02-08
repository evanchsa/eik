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

    public void addProperties(Properties properties) {
    }

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

    public void frameworkStop(BundleContext context) throws BundleException {
    }

    public void frameworkStopping(BundleContext context) {
    }

    public void handleRuntimeError(Throwable error) {
    }

    public void initialize(BaseAdaptor adaptor) {
    }

    public URLConnection mapLocationToURLConnection(String location)
            throws IOException {
        return null;
    }

    public boolean matchDNChain(String pattern, String[] dnChain) {
        return false;
    }

}
