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
package info.evanchik.eclipse.karaf.core.shell;

import java.io.IOException;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface KarafRemoteShellConnection {

    /**
     * Connects to the remote shell of a Karaf instance.
     * <p>
     * This method will block until the connection has been established.
     */
    public void connect() throws IOException;

    /**
     * Disconnects from the remote shell of a Karaf instance
     * <p>
     * This method will block until the connection has been severed
     */
    public void disconnect() throws IOException;

    /**
     * Determines whether or not there is a connection to the Karaf remote shell
     *
     * @return true if a connection exists, false otherwise
     */
    public boolean isConnected();
}
