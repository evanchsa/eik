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
package info.evanchik.eclipse.karaf.workbench.internal;

import info.evanchik.eclipse.karaf.workbench.KarafWorkbenchActivator;
import info.evanchik.eclipse.karaf.workbench.WorkbenchServiceListener;
import info.evanchik.eclipse.karaf.workbench.WorkbenchServiceManager;

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
public abstract class AbstractWorkbenchServiceManager<T> implements WorkbenchServiceManager<T> {

    protected final List<T> serviceDescriptors;

    protected final ListenerList listeners;

    protected static enum EventType {
        ADDED,
        REMOVED;
    }

    private final class Notifier implements ISafeRunnable {

        private EventType event;

        private WorkbenchServiceListener<T> listener;

        private T service;

        public Notifier() {
            // Intentionally left blank
        }

        @Override
        public void handleException(final Throwable exception) {
            final IStatus status =
                new Status(
                        IStatus.ERROR,
                        KarafWorkbenchActivator.PLUGIN_ID,
                        120,
                        "An exception occurred during change notification.", exception);  //$NON-NLS-1$

            KarafWorkbenchActivator.getDefault().getLog().log(status);
        }

        @Override
        public void run() throws Exception {
            switch (event) {
                case ADDED:
                    listener.serviceAdded(service);
                    break;
                case REMOVED:
                    listener.serviceRemoved(service);
                    break;
            }
        }

        /**
         * Notifies the listeners of the add/remove
         *
         * @param serviceUrls the {@link T}s that changed
         * @param event the type of change
         */
        @SuppressWarnings("unchecked")
        public void notify(final List<T> serviceUrls, final EventType e) {
            this.event = e;

            final Object[] copiedListeners = listeners.getListeners();
            for (int i= 0; i < copiedListeners.length; i++) {
                listener = (WorkbenchServiceListener<T>) copiedListeners[i];
                for(final T url : serviceUrls) {
                    service = url;
                    SafeRunner.run(this);
                }
            }

            listener = null;
            service = null;
        }
    }

    protected AbstractWorkbenchServiceManager() {
        serviceDescriptors = Collections.synchronizedList(new ArrayList<T>());
        listeners = new ListenerList();
    }

    @Override
    public void add(final T service) {
        add(Collections.singletonList(service));
    }

    @Override
    public void addListener(final WorkbenchServiceListener<T> listener) {
        listeners.add(listener);
    }

    @Override
    public void add(final List<T> services) {
        serviceDescriptors.addAll(services);
        getNotifier().notify(services, EventType.ADDED);
    }

    @Override
    public List<T> getServices() {
        return Collections.unmodifiableList(serviceDescriptors);
    }

    @Override
    public boolean isRegistered(final T service) {
        return serviceDescriptors.contains(service);
    }

    @Override
    public void remove(final T service) {
        remove(Collections.singletonList(service));
    }

    @Override
    public void removeListener(final WorkbenchServiceListener<T> listener) {
        listeners.remove(listener);
    }

    @Override
    public void remove(final List<T> services) {
        serviceDescriptors.removeAll(services);
        getNotifier().notify(services, EventType.REMOVED);
    }

    protected Notifier getNotifier() {
        return new Notifier();
    }
}
