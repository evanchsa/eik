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
package org.apache.karaf.eik.workbench.jmx;

import java.util.Set;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorProvider;

public interface IJMXTransportRegistry {

	/**
	 * Adds a {@link IJMXTransportListener} to the list of listeners for this
	 * registry. The listener will be notified whenever a
	 * {@link JMXConnectorProvider} is added or removed from the registry.<br>
	 * <br>
	 * Adding the same listener multiple times has no effect.
	 *
	 * @param listener the {@code IJMXTransportListener} to add
	 */
	public void addJMXTransportListener(IJMXTransportListener listener);

	/**
	 * Returns a {@link Set} of names representing
	 * {@link JMXConnectorProvider}s
	 *
	 * @return
	 * 		a {@code Set} of names representing {@link JMXConnectorProvider}s
	 */
	public Set<String> getConnectorNames();

	/**
	 * Getter for a {@link JMXConnectorProvider} using its name as a retrieval
	 * key
	 *
	 * @param name the name of the {@code JMXConnectorProvider}
	 *
	 * @return
	 * 		the {@code JMXConnectorProvider} for the given name, or null if it
	 * 		does not exist
	 */
	public JMXConnectorProvider getConnectorProvider(String name);

	/**
	 * Gets a {@link JMXConnector} for a {@link JMXServiceDescriptor} making use
	 * of the {@code JMXConnectorProvider} registered with the transport
	 * registry
	 *
	 * @param serviceDescriptor the {@code JMXServiceDescriptor} for which the
	 * {@code JMXConnector} is requested
	 *
	 * @return
	 * 		the {@code JMXConnector} used to connect to the service
	 *		represented by the {@code JMXServiceDescriptor}
	 */
	public JMXConnector getJMXConnector(JMXServiceDescriptor serviceDescriptor);

	/**
	 * Removes the given {@link IJMXTransportListener} from the registry.<br>
	 * <br>
	 * If the {@code JMXConnectorProvider} is not registered with the registry,
	 * this method has no effect.
	 *
	 * @param listener the {@code IJMXTransportListener} to remove
	 */
	public void removeJMXTransportListner(IJMXTransportListener listener);

	// TODO: This should not be here
	public void loadTransportExtensions();

}
