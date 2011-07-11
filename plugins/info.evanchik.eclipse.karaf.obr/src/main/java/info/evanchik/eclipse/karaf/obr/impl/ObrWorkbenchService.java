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

import info.evanchik.eclipse.karaf.core.IKarafConstants;
import info.evanchik.eclipse.karaf.core.KarafCorePluginUtils;
import info.evanchik.eclipse.karaf.core.KarafWorkingPlatformModel;
import info.evanchik.eclipse.karaf.core.equinox.BundleEntry;
import info.evanchik.eclipse.karaf.obr.KarafObrActivator;
import info.evanchik.eclipse.karaf.ui.workbench.KarafWorkbenchService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public final class ObrWorkbenchService implements KarafWorkbenchService {

    private static final String OBR_XML = "obr.xml";

    private static final int MAX_BYTES = 32 * 1024;

    private static final String OBR_REPOSITORY_URL = "obr.repository.url";

    @Override
    public List<BundleEntry> getAdditionalBundles(
            final KarafWorkingPlatformModel platformModel,
            final ILaunchConfiguration configuration)
    {
        return Collections.emptyList();
    }

    @Override
    public Map<String, String> getAdditionalEquinoxConfiguration(
            final KarafWorkingPlatformModel platformModel,
            final ILaunchConfiguration configuration)
    {
        return Collections.emptyMap();
    }

    @Override
    public List<String> getVMArguments(
            final KarafWorkingPlatformModel platformModel,
            final ILaunchConfiguration configuration)
        throws CoreException
    {
        registerEclipseObr(platformModel);

        return Collections.emptyList();
    }

    @Override
    public void initialize(
            final KarafWorkingPlatformModel platformModel,
            final ILaunchConfigurationWorkingCopy configuration)
    {
        copyObrContent(platformModel);
    }

    @Override
    public void launch(
            final KarafWorkingPlatformModel platformModel,
            final ILaunchConfiguration configuration,
            final String mode,
            final ILaunch launch,
            final IProgressMonitor monitor)
        throws CoreException
    {
        copyObrContent(platformModel);
    }

    /**
     *
     * @param platformModel
     */
    private void copyObrContent(final KarafWorkingPlatformModel platformModel) {
        final InputStream in = KarafObrActivator.getDefault().getObrInputStream();
        final File obr = new File(platformModel.getRootDirectory().toFile(), OBR_XML);

        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(obr);
            final byte[] b = new byte[MAX_BYTES];
            int read = in.read(b, 0, MAX_BYTES);

            while (read > 0 ) {
                fout.write(b, 0, read);
                read = in.read(b, 0, MAX_BYTES);
            }
        } catch (final IOException e) {

        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (final IOException e) {
                    // Intentionally left blank
                }
            }

            try {
                in.close();
            } catch (final IOException e) {
                // Intentionally left blank
            }
        }
    }

    /**
     * @param platformModel
     */
    private void registerEclipseObr(
            final KarafWorkingPlatformModel platformModel)
    {
        final File obr = new File(platformModel.getRootDirectory().toFile(), OBR_XML);
        final File configFile = new File(platformModel.getParentKarafModel().getConfigurationDirectory().toFile(), IKarafConstants.KARAF_DEFAULT_CONFIG_PROPERTIES_FILE);

        final Properties currentConfig;
        try {

            currentConfig =
                KarafCorePluginUtils.loadProperties(
                    platformModel.getParentKarafModel().getConfigurationDirectory().toFile(),
                    IKarafConstants.KARAF_DEFAULT_CONFIG_PROPERTIES_FILE,
                    true);

            final String obrRepositoryUrl = (String) currentConfig.get(OBR_REPOSITORY_URL);
            if (!obrRepositoryUrl.contains(obr.toURI().toString())) {
                final String newObrRepositoryUrl =
                    KarafCorePluginUtils.join(Arrays.asList(obrRepositoryUrl, obr.toURI().toString()), " ");
                currentConfig.put(OBR_REPOSITORY_URL, newObrRepositoryUrl);

                KarafCorePluginUtils.save(configFile, currentConfig);
            }
        } catch(final CoreException e) {
            KarafObrActivator.getLogger().error("Unable to load configuration file: " + platformModel.getParentKarafModel().getConfigurationDirectory(), e);
        }
    }
}
