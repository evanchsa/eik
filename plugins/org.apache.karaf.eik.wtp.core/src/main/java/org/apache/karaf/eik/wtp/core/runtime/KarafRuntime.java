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
package org.apache.karaf.eik.wtp.core.runtime;

import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.core.KarafPlatformModelRegistry;
import org.apache.karaf.eik.wtp.core.KarafWtpPluginActivator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.model.RuntimeDelegate;

public class KarafRuntime extends RuntimeDelegate {

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    public void setDefaults(final IProgressMonitor monitor) {
        super.setDefaults(monitor);
    }

    /**
     * Determines whether or not this is a valid {@link IRuntime} of a Karaf
     * installation.
     *
     * @return a {@link IStatus} object indicating whether or not this is a
     *         valid Karaf runtime. A valid Karaf Runtime will return
     *         {@link Status#OK_STATUS} otherwise a status based on
     *         {@link IStatus#ERROR}
     */
    @Override
    public IStatus validate() {
        final IPath location = getRuntime().getLocation();

        if (location == null || location.isEmpty()) {
            return new Status(IStatus.ERROR, KarafWtpPluginActivator.PLUGIN_ID, 0, "", null);
        }

        final IStatus status = super.validate();
        if (!status.isOK()) {
            return status;
        }

        KarafPlatformModel karafTargetPlatform;
        try {
            karafTargetPlatform = KarafPlatformModelRegistry.findPlatformModel(location);
            if (karafTargetPlatform != null) {
                return Status.OK_STATUS;
            } else {
                return new Status(
                        IStatus.ERROR,
                        KarafWtpPluginActivator.PLUGIN_ID,
                        0,
                        "Unable to validate Karaf installation",
                        null);
            }
        } catch (final CoreException e) {
            return new Status(
                    IStatus.ERROR,
                    KarafWtpPluginActivator.PLUGIN_ID,
                    0,
                    "Unable to locate Karaf platform",
                    e);
        }
    }

}
