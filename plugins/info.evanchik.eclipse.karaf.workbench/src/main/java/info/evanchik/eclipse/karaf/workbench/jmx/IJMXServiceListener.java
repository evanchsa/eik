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


/**
 * A {@code IJMXServiceListener} is notified when {@link JMXServiceDescriptor}s
 * are added or removed from a manager.
 *
 * @see IJMXServiceManager
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface IJMXServiceListener {

	/**
	 * Called when a {@link JMXServiceDescriptor} has been added to an object
	 * that this listener is observing.
	 *
	 * @param jmxService the {@code JMXServiceDescriptor} that was added
	 */
	public void jmxServiceAdded(JMXServiceDescriptor jmxService);

	/**
	 * Called when a {@link JMXServiceDescriptor} has been removed from an
	 * object that this listener is observing.
	 *
	 * @param jmxService the {@code JMXServiceDescriptor} that was removed
	 */
	public void jmxServiceRemoved(JMXServiceDescriptor jmxService);
}
