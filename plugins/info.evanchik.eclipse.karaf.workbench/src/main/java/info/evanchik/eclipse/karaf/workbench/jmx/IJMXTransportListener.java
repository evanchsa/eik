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
package info.evanchik.eclipse.karaf.workbench.jmx;

import info.evanchik.eclipse.karaf.workbench.WorkbenchServiceListener;

import javax.management.remote.JMXConnectorProvider;

/**
 * A {@code IJMXTransportListener} is notified when a
 * {@link JMXConnectorProvider} is added or removed from a
 * {@link IJMXTransportRegistry}.
 *
 * @see IJMXTransportRegistry
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface IJMXTransportListener extends WorkbenchServiceListener<JMXConnectorProvider> {

}
