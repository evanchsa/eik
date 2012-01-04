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
package info.evanchik.eclipse.karaf.core.configuration;

import java.io.File;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface ShellSection extends ConfigurationSection {

    /**
     * Getter for the network port that the SSH server listens on
     *
     * @return the network port of the SSH server
     */
    Integer getSshPort();

    /**
     * Getter for the host address that the SSH server will bind to. This can be
     * {@code 0.0.0.0} which means the SSH server will bind to all addresses
     *
     * @return the host address that the SSH server will bind to.
     */
    String getSshHost();

    /**
     * Getter for the JAAS domain to use for password authentication
     *
     * @return the JAAS domain to use for password authentication
     */
    String getSshRealm();

    /**
     * Getter for the location of the {@link File} that defines the SSH server's
     * public/private key pair
     *
     * @return the location of the {@code File} that defines the SSH server's
     *         public/private key pair.
     */
    File getHostKey();
}
