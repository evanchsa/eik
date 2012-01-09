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
package org.apache.karaf.eik.workbench.internal;

import org.apache.karaf.eik.workbench.KarafWorkbenchActivator;
import org.apache.karaf.eik.workbench.WorkbenchServiceListener;
import org.apache.karaf.eik.workbench.WorkbenchServiceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;

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
