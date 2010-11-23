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
package info.evanchik.eclipse.karaf.core.internal;

import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.model.GenericKarafPlatformModelSynchronizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class StandardKarafPlatformModelSynchronizer extends GenericKarafPlatformModelSynchronizer {

    private static final String SHELL_INIT_SCRIPT = "shell.init.script";

    private static final List<String> fileKeys;

    static {
        final List<String> tempFileKeys = new ArrayList<String>();

        tempFileKeys.add("org.apache.felix.karaf.features.cfg"); //$NON-NLS-1$
        tempFileKeys.add("org.apache.felix.karaf.log.cfg"); //$NON-NLS-1$
        tempFileKeys.add("org.apache.felix.karaf.management.cfg"); //$NON-NLS-1$
        tempFileKeys.add("org.apache.felix.karaf.shell.cfg"); //$NON-NLS-1$

        tempFileKeys.add("org.apache.felix.fileinstall-deploy.cfg"); //$NON-NLS-1$
        tempFileKeys.add("org.apache.karaf.features.cfg"); //$NON-NLS-1$
        tempFileKeys.add("org.apache.karaf.log.cfg"); //$NON-NLS-1$
        tempFileKeys.add("org.apache.karaf.management.cfg"); //$NON-NLS-1$
        tempFileKeys.add("org.apache.karaf.shell.cfg"); //$NON-NLS-1$

        fileKeys = Collections.unmodifiableList(tempFileKeys);
    }

    public StandardKarafPlatformModelSynchronizer(KarafPlatformModel platformModel) {
        super(platformModel);
    }


    @Override
    protected List<String> getFileKeys() {
        final List<String> mergedFileKeys = new ArrayList<String>();
        mergedFileKeys.addAll(super.getFileKeys());
        mergedFileKeys.addAll(fileKeys);
        mergedFileKeys.addAll(getOptionalFileKeys());
        return Collections.unmodifiableList(mergedFileKeys);
    }

    /**
     *
     * @return
     */
    private List<String> getOptionalFileKeys() {
        final List<String> optionalFileKeys = new ArrayList<String>();

        if (platformModel.getConfigurationFile(SHELL_INIT_SCRIPT).toFile().exists()) {
            optionalFileKeys.add(SHELL_INIT_SCRIPT);
        }

        return Collections.unmodifiableList(optionalFileKeys);
    }
}
