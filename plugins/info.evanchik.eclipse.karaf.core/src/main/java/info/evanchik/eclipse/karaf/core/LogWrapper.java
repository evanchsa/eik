/**
 * Copyright (c) 2009 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package info.evanchik.eclipse.karaf.core;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
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
