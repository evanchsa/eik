/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.karaf.eclipse.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.karaf.eclipse.core.internal.KarafCorePluginActivator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public final class KarafPlatformModelRegistry {

    public static final String ATT_CLASS = "class";

    private static final String TAG_PLATFORM_MODEL_ITEMTYPE = "model";

    private static final String ATT_SYMBOLIC_NAME = "symbolicName";

    private static final String TAG_TRIGGER_BUNDLE_ITEMTYPE = "triggerBundle";

    /**
     *
     * @param path
     * @return
     * @throws CoreException
     */
    public static KarafPlatformModel findPlatformModel(final IPath path) throws CoreException {
        final KarafPlatformModelFactory factory = findPlatformModelFactory(path);

        if (factory == null) {
            return null;
        }

        return factory.getPlatformModel(path);
    }

    /**
     *
     * @param path
     * @return
     */
    public static KarafPlatformModelFactory findPlatformModelFactory(final IPath path) throws CoreException {
        final List<KarafPlatformModelFactory> factories = getPlatformModelFactories();

        for (final KarafPlatformModelFactory f : factories) {
            if (f.getPlatformValidator().isValid(path)) {
                return f;
            }
        }

        return null;
    }

    /**
     * Gets all {@link KarafPlatformModelFactory} objects that are a "model"
     *
     * @return a {@link List<KarafPlatformModelFactory>} of 0 or more {@code
     *         KarafPlatformModelFactory}
     * @throws CoreException
     *             if there is a problem fetching information from the extension
     *             registry
     */
    public static List<KarafPlatformModelFactory> getPlatformModelFactories() throws CoreException {
        final List<KarafPlatformModelFactory> factories =
            new ArrayList<KarafPlatformModelFactory>();

        final IExtension[] extensions = Platform.getExtensionRegistry()
                                    .getExtensionPoint(KarafCorePluginActivator.PLUGIN_ID, "platformModel")
                                    .getExtensions();

        for (final IExtension e : extensions) {
            for (final IConfigurationElement c : e.getConfigurationElements()) {

                if (!c.getName().equals(TAG_PLATFORM_MODEL_ITEMTYPE)) {
                    continue;
                }

                final KarafPlatformModelFactory f =
                    (KarafPlatformModelFactory)c.createExecutableExtension(ATT_CLASS);



                factories.add(f);
            }
        }

        return factories;
    }

    /**
     *
     * @return
     * @throws CoreException
     */
    public static List<String> getTriggerBundles() throws CoreException {
        final List<String> triggerBundles = new ArrayList<String>();

        triggerBundles.addAll(getTriggerBundlePlatformFactoryMap().keySet());

        return triggerBundles;
    }

    /**
     *
     * @return
     * @throws CoreException
     */
    public static Map<String, IConfigurationElement> getTriggerBundlePlatformFactoryMap() throws CoreException {
        final Map<String, IConfigurationElement> triggerBundleMap =
            new LinkedHashMap<String, IConfigurationElement>();

        final IExtension[] extensions =
            Platform.getExtensionRegistry()
                .getExtensionPoint(KarafCorePluginActivator.PLUGIN_ID, "platformModel")
                .getExtensions();

        for (final IExtension e : extensions) {
            for (final IConfigurationElement c : e.getConfigurationElements()) {

                if (!c.getName().equals(TAG_PLATFORM_MODEL_ITEMTYPE)) {
                    continue;
                }

                final IConfigurationElement[] children = c.getChildren(TAG_TRIGGER_BUNDLE_ITEMTYPE);
                for (final IConfigurationElement tb : children) {
                    triggerBundleMap.put(tb.getAttribute(ATT_SYMBOLIC_NAME), c);
                }

            }
        }

        return triggerBundleMap;
    }
}
