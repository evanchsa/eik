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
package org.apache.karaf.eik.ui.internal;

import org.apache.karaf.eik.ui.KarafUIPluginActivator;
import org.apache.karaf.eik.ui.workbench.KarafWorkbenchServiceFactory;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

public final class WorkbenchServiceExtensions {

    private static final String ATT_CLASS = "class";

    private static final String TAG_LAUNCHCUSTOMIZER_ITEMTYPE = "launchCustomizer";

    /**
     * Gets all {@link KarafWorkbenchServiceFactory} objects that are a
     * "launchCustomizer"
     *
     * @return a {@link List<KarafWorkbenchServiceFactory>} of 0 or more {@code
     *         KarafWorkbenchServiceFactory}
     * @throws CoreException
     *             if there is a problem fetching information from the extension
     *             registry
     */
    public static List<KarafWorkbenchServiceFactory> getLaunchCustomizerFactories() throws CoreException {
        final List<KarafWorkbenchServiceFactory> factories =
            new ArrayList<KarafWorkbenchServiceFactory>();

        final IExtension[] extensions = Platform.getExtensionRegistry()
                                    .getExtensionPoint(KarafUIPluginActivator.PLUGIN_ID, "service")
                                    .getExtensions();

        for (final IExtension e : extensions) {
            for (final IConfigurationElement c : e.getConfigurationElements()) {

                if (!c.getName().equals(TAG_LAUNCHCUSTOMIZER_ITEMTYPE)) {
                    continue;
                }

                final KarafWorkbenchServiceFactory f =
                    (KarafWorkbenchServiceFactory)c.createExecutableExtension(ATT_CLASS);



                factories.add(f);
            }
        }

        return factories;
    }

}
