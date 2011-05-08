/**
 * Copyright (c) 2010 Stephen Evanchik
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
import info.evanchik.eclipse.karaf.ui.workbench.KarafWorkbenchServiceFactory;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
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
