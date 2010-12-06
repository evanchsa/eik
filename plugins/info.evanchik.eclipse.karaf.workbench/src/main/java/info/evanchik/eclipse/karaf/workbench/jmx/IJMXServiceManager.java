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

import java.util.List;

/**
 * {@code IJMXServiceManager} is the interface for managing
 * {@link JMXServiceDescriptors} registered in the workbench.
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface IJMXServiceManager {

	/**
	 * Adds a {@link JMXServiceDescriptor} to this manager. This method will
	 * notify all {@link IJMXServiceListener}s registered.
	 *
	 * @param jmxService the {@code JMXServiceDescriptor} to add
	 */
	public void addJMXService(JMXServiceDescriptor jmxService);

	/**
	 * Adds a {@link IJMXServiceListener} to the list of listeners for this
	 * manager. The listener will be notified whenever a
	 * {@link JMXServiceDescriptor} is added or removed from the manager.<br>
	 * <br>
	 * Adding the same listener multiple times has no effect.
	 *
	 * @param listener the {@code IJMXServiceListener} to add
	 */
	public void addJMXServiceListener(IJMXServiceListener listener);

	/**
	 * Adds a {@link List} of {@link JMXServiceDescriptor}s to this manager.
	 * This method will notify all {@link IJMXServiceListener}s registered.
	 *
	 * @param jmxServices
	 * 			the {@code List} of {@code JMXServiceDescriptor}s to add
	 */
	public void addJMXService(List<JMXServiceDescriptor> jmxServices);

	/**
	 * Returns all of the registered {@link JMXServiceDescriptor}s. The
	 * resulting {@code List} is read-only.
	 *
	 * @return a read-only {@code List} of {@code JMXServiceDescriptor}s
	 */
	public List<JMXServiceDescriptor> getJMXServices();

	/**
	 * Determines if the given {@link JMXServiceDescriptor} is registered with
	 * the manager.
	 *
	 * @param jmxService the {@code JMXServiceDescriptor} to search for
	 * @return true if the {@code JMXServiceDescriptor} was found, false otherwise
	 */
	public boolean isRegistered(JMXServiceDescriptor jmxService);

	/**
	 * Removes the given {@link JMXServiceDescriptor} from the manager.<br>
	 * <br>
	 * If the {@code JMXServiceDescriptor} is not registered with the manager,
	 * this method has no effect.
	 *
	 * @param jmxService the {@code JMXServiceDescriptor} to remove
	 */
	public void removeJMXService(JMXServiceDescriptor jmxService);

	/**
	 * Removes the given {@link IJMXServiceListener} from the manager.<br>
	 * <br>
	 * If the {@code IJMXServiceListener} is not registered with the manager,
	 * this method has no effect.
	 *
	 * @param listener the {@code IJMXServiceListener} to remove
	 */
	public void removeJMXServiceListner(IJMXServiceListener listener);

	/**
	 * Removes a {@link List} of {@link JMXServiceDescriptor}s from the
	 * manager.<br>
	 * <br>
	 * {@code JMXServiceDescriptors} present in the remove list but not
	 * registered with the manager are ignored.
	 *
	 * @param jmxServices
	 * 			the {@code List} of {@code JMXServiceDescriptor}s to remove
	 */
	public void removeJMXService(List<JMXServiceDescriptor> jmxServices);
}
