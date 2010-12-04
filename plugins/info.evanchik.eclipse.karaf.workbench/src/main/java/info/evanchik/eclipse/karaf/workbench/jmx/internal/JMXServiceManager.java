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
package info.evanchik.eclipse.karaf.workbench.jmx.internal;

import info.evanchik.eclipse.karaf.workbench.KarafWorkbenchActivator;
import info.evanchik.eclipse.karaf.workbench.jmx.IJMXServiceListener;
import info.evanchik.eclipse.karaf.workbench.jmx.IJMXServiceManager;
import info.evanchik.eclipse.karaf.workbench.jmx.JMXServiceDescriptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class JMXServiceManager implements IJMXServiceManager {

	private final List<JMXServiceDescriptor> jmxServiceDescriptors;

	protected final ListenerList jmxServerListeners;

	private static enum EventType {
		ADDED,
		REMOVED;
	}

	private class JMXServerNotifier implements ISafeRunnable {

		private EventType event;

		private IJMXServiceListener listener;

		private JMXServiceDescriptor jmxServiceDescriptor;

		public JMXServerNotifier() {
			// Intentionally left blank
		}

		@Override
        public void handleException(Throwable exception) {
			final IStatus status =
				new Status(
						IStatus.ERROR,
						KarafWorkbenchActivator.PLUGIN_ID,
						120,
						"An exception occurred during JMX Server change notification.", exception);  //$NON-NLS-1$
			KarafWorkbenchActivator.getDefault().getLog().log(status);
		}

		@Override
        public void run() throws Exception {
			switch (event) {
				case ADDED:
					listener.jmxServiceAdded(jmxServiceDescriptor);
					break;
				case REMOVED:
					listener.jmxServiceRemoved(jmxServiceDescriptor);
					break;
			}
		}

		/**
		 * Notifies the listeners of the add/remove
		 *
		 * @param jmxServiceUrls the {@link JMXServiceDescriptor}s that changed
		 * @param event the type of change
		 */
		public void notify(List<JMXServiceDescriptor> jmxServiceUrls, EventType e) {
			this.event = e;

			Object[] copiedListeners = jmxServerListeners.getListeners();
			for (int i= 0; i < copiedListeners.length; i++) {
				listener = (IJMXServiceListener)copiedListeners[i];
				for(JMXServiceDescriptor url : jmxServiceUrls) {
					jmxServiceDescriptor = url;
                    SafeRunner.run(this);
				}
			}

			listener = null;
			jmxServiceDescriptor = null;
		}
	}

	public JMXServiceManager() {
		jmxServiceDescriptors = Collections.synchronizedList(new ArrayList<JMXServiceDescriptor>());
		jmxServerListeners = new ListenerList();
	}

	@Override
    public void addJMXService(JMXServiceDescriptor jmxService) {
		addJMXService(Collections.singletonList(jmxService));
	}

	@Override
    public void addJMXServiceListener(IJMXServiceListener listener) {
		jmxServerListeners.add(listener);
	}

	@Override
    public void addJMXService(List<JMXServiceDescriptor> jmxServices) {
		jmxServiceDescriptors.addAll(jmxServices);
		getJMXServerNotifier().notify(jmxServices, EventType.ADDED);
	}

	@Override
    public List<JMXServiceDescriptor> getJMXServices() {
		return Collections.unmodifiableList(jmxServiceDescriptors);
	}

	@Override
    public boolean isRegistered(JMXServiceDescriptor jmxService) {
		return jmxServiceDescriptors.contains(jmxService);
	}

	@Override
    public void removeJMXService(JMXServiceDescriptor jmxService) {
		removeJMXService(Collections.singletonList(jmxService));
	}

	@Override
    public void removeJMXServiceListner(IJMXServiceListener listener) {
		jmxServerListeners.remove(listener);
	}

	@Override
    public void removeJMXService(List<JMXServiceDescriptor> jmxServices) {
		jmxServiceDescriptors.removeAll(jmxServices);
		getJMXServerNotifier().notify(jmxServices, EventType.REMOVED);
	}

	private JMXServerNotifier getJMXServerNotifier() {
		return new JMXServerNotifier();
	}
}
