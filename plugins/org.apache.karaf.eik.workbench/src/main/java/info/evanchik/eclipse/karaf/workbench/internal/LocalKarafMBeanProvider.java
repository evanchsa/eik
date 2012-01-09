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
package info.evanchik.eclipse.karaf.workbench.internal;

import org.apache.karaf.eik.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.workbench.jmx.JMXServiceDescriptor;

import java.io.IOException;

import javax.management.remote.JMXConnector;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class LocalKarafMBeanProvider extends KarafMBeanProvider {

    private final KarafPlatformModel platformModel;

    /**
     * @param jmxServiceDescriptor
     * @param connector
     * @param platformModel
     * @throws IOException
     */
    public LocalKarafMBeanProvider(final JMXServiceDescriptor jmxServiceDescriptor, final JMXConnector connector, final KarafPlatformModel platforModel) throws IOException {
        super(jmxServiceDescriptor, connector);

        this.platformModel = platforModel;
    }

    @Override
    public Object getAdapter(final @SuppressWarnings("rawtypes") Class adapter) {
        if (KarafPlatformModel.class.equals(adapter)) {
            return platformModel;
        } else {
            return super.getAdapter(adapter);
        }
    }

}
