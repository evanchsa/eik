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
package info.evanchik.eclipse.karaf.workbench.provider;

import info.evanchik.eclipse.karaf.workbench.KarafWorkbenchActivator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public abstract class AbstractRuntimeDataProvider implements RuntimeDataProvider {

    protected final Set<BundleItem> bundleSet;
    protected final Map<Long, BundleItem> idToBundleMap;

    protected final Set<ServiceItem> serviceSet;
    protected final Map<Long, ServiceItem> idToServiceMap;

    private final List<RuntimeDataProviderListener> listeners;

    /**
     * Constructor that simply initializes all internal data structures.
     */
    public AbstractRuntimeDataProvider() {
        listeners = Collections.synchronizedList(new ArrayList<RuntimeDataProviderListener>());

        bundleSet = Collections.synchronizedSet(new HashSet<BundleItem>());
        idToBundleMap = Collections.synchronizedMap(new HashMap<Long, BundleItem>());

        serviceSet = Collections.synchronizedSet(new HashSet<ServiceItem>());
        idToServiceMap = Collections.synchronizedMap(new HashMap<Long, ServiceItem>());
    }

    @Override
    public void addListener(final RuntimeDataProviderListener listener) {
        if (listeners.contains(listener)) {
            return;
        }

        listeners.add(listener);
    }

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") final Class adapter) {
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }

    @Override
    public BundleItem getBundle(final long id) {
        return idToBundleMap.get(id);
    }

    @Override
    public Set<BundleItem> getBundles() {
        synchronized (bundleSet) {
            return Collections.unmodifiableSet(bundleSet);
        }
    }

    @Override
    public Image getIcon() {
        return KarafWorkbenchActivator.getDefault().getImageRegistry().get(KarafWorkbenchActivator.BUNDLE_OBJ_IMG);
    }

    @Override
    public ServiceItem getService(final long id) {
        return idToServiceMap.get(id);
    }

    @Override
    public Set<ServiceItem> getServices() {
        synchronized (serviceSet) {
            return Collections.unmodifiableSet(serviceSet);
        }
    }

    @Override
    public void removeListener(final RuntimeDataProviderListener listener) {
        if (!listeners.contains(listener)) {
            return;
        }

        listeners.remove(listener);
    }

    /**
     * Sends the change event to all registered
     * {@link RuntimeDataProviderListener}S
     *
     * @param type
     *            the type of change event as indicated by a {@link EnumSet} of
     *            {@link RuntimeDataProviderListener.EventType}
     */
    protected void fireProviderChangeEvent(final EnumSet<RuntimeDataProviderListener.EventType> type) {
        synchronized (listeners) {
            for (final RuntimeDataProviderListener l : listeners) {
                l.providerChange(this, type);
            }
        }
    }

    /**
     * Sends the start event to all registered
     * {@link RuntimeDataProviderListener}S
     */
    protected void fireStartEvent() {
        synchronized (listeners) {
            for (final RuntimeDataProviderListener l : listeners) {
                l.providerStart(this);
            }
        }
    }

    /**
     * Sends the stop event to all registered
     * {@link RuntimeDataProviderListener}S
     */
    protected void fireStopEvent() {
        synchronized (listeners) {
            for (final RuntimeDataProviderListener l : listeners) {
                l.providerStop(this);
            }
        }
    }
}
