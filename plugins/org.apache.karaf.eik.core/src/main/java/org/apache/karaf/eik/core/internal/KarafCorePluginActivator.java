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
package org.apache.karaf.eik.core.internal;

import org.apache.karaf.eik.core.LogWrapper;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class KarafCorePluginActivator extends Plugin {

    public static final String PLUGIN_ID = "org.apache.karaf.eik.core";

    private static KarafCorePluginActivator plugin = null;

    /**
     * Returns the shared instance of this plugin.
     *
     * @return the shared instance
     */
    public static KarafCorePluginActivator getDefault() {
        return plugin;
    }

    /**
     * Getter for the {@link LogWrapper} object that makes logging much easier
     * on the caller.
     *
     * @return the {@link LogWrapper} instance
     */
    public static LogWrapper getLogger() {
        return new LogWrapper(getDefault().getLog(), PLUGIN_ID);
    }

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        KarafCorePluginActivator.plugin = this;
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        super.stop(context);
        KarafCorePluginActivator.plugin = null;
    }

}
