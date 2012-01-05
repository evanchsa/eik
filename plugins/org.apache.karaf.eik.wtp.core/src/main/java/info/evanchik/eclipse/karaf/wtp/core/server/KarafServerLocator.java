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
package info.evanchik.eclipse.karaf.wtp.core.server;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.internal.provisional.ServerLocatorDelegate;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
@SuppressWarnings("restriction")
public class KarafServerLocator extends ServerLocatorDelegate {

    @Override
    public void searchForServers(final String host, final IServerSearchListener listener,
            final IProgressMonitor monitor) {

    }

}
