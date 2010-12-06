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
public interface IJMXTransportListener {

	/**
	 * Called when a {@link JMXConnectorProvider} has been added to an object
	 * that this listener is observing.

	 * @param jmxConnectorProvider
	 * 		the {@code JMXConnectorProvider} that was added
	 */
	public void jmxTransportAdded(JMXConnectorProvider jmxConnectorProvider);

	/**
	 * Called when a {@link JMXConnectorProvider} has been removed from an
	 * object that this listener is observing.
	 *
	 * @param jmxConnectorProvider
	 * 		the {@code JMXConnectorProvider} that was removed
	 */
	public void jmxTransportRemoved(JMXConnectorProvider jmxConnectorProvider);
}
