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
package org.apache.karaf.eik.wtp.core.server;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ServerDelegate;

public class KarafServer extends ServerDelegate {

    public KarafServer() {
        super();
    }

    /**
     * Determines whether or not the list of modules can be added and removed
     * from this server instance. A value of {@link Status#OK_STATUS} is
     * returned if the operation succeeds.
     *
     * @param add
     *            List of {@link IModule}S to be added to this server
     * @param remove
     *            List of {@link IModule}S to be removed from this server
     * @return {@Status#OK_STATUS} if the additions/removals
     *         are successful, {@link IStatus.ERROR} otherwise
     */
    @Override
    public IStatus canModifyModules(final IModule[] add, final IModule[] remove) {
        if (add != null) {

        }

        if (remove != null) {

        }

        return Status.OK_STATUS;
    }

    @Override
    public IModule[] getChildModules(final IModule[] module) {
        return null;
    }

    @Override
    public IModule[] getRootModules(final IModule module) throws CoreException {
        return new IModule[] { module };
    }

    /**
     * Performs the actual modification to the server by adding and/or removing
     * modules.
     */
    @Override
    public void modifyModules(final IModule[] add, final IModule[] remove, final IProgressMonitor monitor) throws CoreException {
        final IStatus status = canModifyModules(add, remove);
        if (status == null || !status.isOK()) {
            throw new CoreException(status);
        }

        if (add != null) {

        }

        if (remove != null) {

        }
    }

    @Override
    public String toString() {
        return "KarafServer";
    }

}
