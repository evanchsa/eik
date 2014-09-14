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
package org.apache.karaf.eik.hooks;

import java.io.IOException;
import java.net.URLConnection;
import java.util.Properties;

import org.eclipse.osgi.baseadaptor.BaseAdaptor;
import org.eclipse.osgi.baseadaptor.hooks.AdaptorHook;
import org.eclipse.osgi.framework.log.FrameworkLog;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class KarafAdapterHook implements AdaptorHook {

    @Override
    public void addProperties(Properties properties) {
    }

    @Override
    public FrameworkLog createFrameworkLog() {
        return null;
    }

    /**
     * Loads the Karaf system properties when the OSGi framework starts.
     *
     * @see org.eclipse.osgi.baseadaptor.hooks.AdaptorHook#frameworkStart(org.osgi
     *      .framework.BundleContext)
     */
    @Override
    public void frameworkStart(BundleContext context) throws BundleException {

    }

    @Override
    public void frameworkStop(BundleContext context) throws BundleException {
    }

    @Override
    public void frameworkStopping(BundleContext context) {
    }

    @Override
    public void handleRuntimeError(Throwable error) {
    }

    @Override
    public void initialize(BaseAdaptor adaptor) {
    }

    @Override
    public URLConnection mapLocationToURLConnection(String location)
            throws IOException {
        return null;
    }

    public boolean matchDNChain(String pattern, String[] dnChain) {
        return false;
    }

}
