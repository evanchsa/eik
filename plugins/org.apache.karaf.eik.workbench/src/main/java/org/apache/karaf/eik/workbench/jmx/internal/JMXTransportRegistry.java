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
package org.apache.karaf.eik.workbench.jmx.internal;

import org.apache.karaf.eik.workbench.KarafWorkbenchActivator;
import org.apache.karaf.eik.workbench.jmx.IJMXTransportListener;
import org.apache.karaf.eik.workbench.jmx.IJMXTransportRegistry;
import org.apache.karaf.eik.workbench.jmx.JMXServiceDescriptor;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorProvider;
import javax.management.remote.JMXServiceURL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;

public class JMXTransportRegistry implements IJMXTransportRegistry {

	private final Map<String, JMXConnectorProvider> transports;

	protected final ListenerList jmxTransportListeners;

	private static enum EventType {
		ADDED,
		REMOVED;
	}

	private class JMXTransportNotifier implements ISafeRunnable {

		private EventType event;

		private IJMXTransportListener listener;

		private JMXConnectorProvider jmxConnectorProvider;

		public JMXTransportNotifier() {
			// Intentionally left blank
		}

		@Override
        public void handleException(Throwable exception) {
			final IStatus status =
				new Status(
						IStatus.ERROR,
						KarafWorkbenchActivator.PLUGIN_ID,
						120,
						"An exception occurred during breakpoint change notification.", exception);  //$NON-NLS-1$
			KarafWorkbenchActivator.getDefault().getLog().log(status);
		}

		@Override
        public void run() throws Exception {
			switch (event) {
				case ADDED:
					listener.serviceAdded(jmxConnectorProvider);
					break;
				case REMOVED:
					listener.serviceRemoved(jmxConnectorProvider);
					break;
			}
		}

		/**
		 * Notifies the listeners of the add/remove
		 *
		 * @param jmxConnectorProviders
		 * 		the {@link JMXConnectorProvider}s that changed
		 * @param event
		 * 			the type of change
		 */
		public void notify(List<JMXConnectorProvider> jmxConnectorProviders, EventType e) {
			this.event = e;

			Object[] copiedListeners = jmxTransportListeners.getListeners();
			for (int i= 0; i < copiedListeners.length; i++) {
				listener = (IJMXTransportListener)copiedListeners[i];
				for(JMXConnectorProvider connector : jmxConnectorProviders) {
					jmxConnectorProvider = connector;
                    SafeRunner.run(this);
				}
			}

			listener = null;
			jmxConnectorProvider = null;
		}
	}

	public JMXTransportRegistry() {
		transports = Collections.synchronizedMap(new HashMap<String, JMXConnectorProvider>());
		jmxTransportListeners = new ListenerList();
	}

	@Override
    public void addJMXTransportListener(IJMXTransportListener listener) {
		jmxTransportListeners.add(listener);
	}

	@Override
    public Set<String> getConnectorNames() {
		return Collections.unmodifiableSet(transports.keySet());
	}

	@Override
    public JMXConnectorProvider getConnectorProvider(String key) {
		final JMXConnectorProvider connector = transports.get(key);

		// TODO: Add logging

		return connector;
	}

	@Override
    public JMXConnector getJMXConnector(JMXServiceDescriptor serviceDescriptor) {
		try {
			final String transport = serviceDescriptor.getUrl().getProtocol();

			final JMXConnectorProvider connectorProvider = getConnectorProvider(transport);

			final JMXServiceURL url = getJMXServiceURL(
									serviceDescriptor.getUrl().getHost(),
									serviceDescriptor.getUrl().getPort(),
									serviceDescriptor.getUrl().getProtocol(),
									serviceDescriptor.getDomain());

			Map<String, Object> environment = null;
			if (serviceDescriptor.getUsername() != null) {
				environment = new HashMap<String, Object>();
				String[] credentials = new String[] {
							serviceDescriptor.getUsername(),
							serviceDescriptor.getPassword()
				};
				environment.put(JMXConnector.CREDENTIALS, credentials);
			}

			return connectorProvider.newJMXConnector(url, environment);
		} catch (Exception e) {
			KarafWorkbenchActivator.getLogger().error(e.getMessage(), e);
			return null;
		}
	}

	@Override
    public void removeJMXTransportListner(IJMXTransportListener listener) {
		jmxTransportListeners.remove(listener);
	}

	private JMXTransportNotifier getJMXTransportrNotifier() {
		return new JMXTransportNotifier();
	}

	@Override
    public void loadTransportExtensions() {
		final IExtensionPoint point =
			RegistryFactory.getRegistry().getExtensionPoint(
					KarafWorkbenchActivator.PLUGIN_ID,
					KarafWorkbenchActivator.JMX_CONNECTOR_PROVIDER_EXTENSION_ID);

		final IExtension[] types = point.getExtensions();

		for (int i = 0; i < types.length; i++) {
			loadTransportConfigurationElements(types[i].getConfigurationElements());
		}
	}

	private void loadTransportConfigurationElements(IConfigurationElement[] configElems) {
		for (int j = 0; j < configElems.length; j++) {
			final IConfigurationElement element = configElems[j];
			final String elementName = element.getName();
			String transport;
			if (elementName.equals("transport") //$NON-NLS-1$
					&& null != element.getAttribute("class") //$NON-NLS-1$
					&& null != (transport = element.getAttribute("protocol"))) //$NON-NLS-1$
			{
				try {
					Object obj = element.createExecutableExtension("class"); //$NON-NLS-1$
					if (obj instanceof JMXConnectorProvider) {
						transports.put(transport, (JMXConnectorProvider)obj);
					}
				} catch (CoreException e) {
					KarafWorkbenchActivator.getLogger().error(e.getMessage(), e);
				}
			}
		}

		final List<JMXConnectorProvider> transportsAdded = new ArrayList<JMXConnectorProvider>();
		transportsAdded.addAll(transports.values());

		getJMXTransportrNotifier().notify(transportsAdded, EventType.ADDED);
	}

    private static JMXServiceURL getJMXServiceURL(
            String host,
            int port,
            String protocol,
            String domain)
        throws MalformedURLException
    {
        return new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/" + domain);
    }

}
