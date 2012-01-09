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
package org.apache.karaf.eik.core;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class LogWrapper {

    /**
     * The PDE logger instance
     */
    private final ILog log;

    /**
     * The ID of the plugin that owns the underlying logger
     */
    private final String pluginId;

    /**
     * Constructor. This class wraps the PDE logger in to an object that is more
     * compact to invoke for the caller.
     *
     * @param theLog
     *            the PDE logger instance
     * @param pluginId
     *            the ID plugin that owns the logger
     */
    public LogWrapper(ILog theLog, String pluginId) {
        this.log = theLog;
        this.pluginId = pluginId;
    }

    public void error(String message) {
        error(message, null);
    }

    public void error(String message, Throwable t) {
        final IStatus status = new Status(IStatus.ERROR, pluginId, message, t);
        log.log(status);
    }

    public void info(String message) {
        info(message, null);
    }

    public void info(String message, Throwable t) {
        final IStatus status = new Status(IStatus.INFO, pluginId, message, t);
        log.log(status);
    }

    public void warn(String message) {
        warn(message, null);
    }

    public void warn(String message, Throwable t) {
        final IStatus status = new Status(IStatus.WARNING, pluginId, message, t);
        log.log(status);
    }

}
