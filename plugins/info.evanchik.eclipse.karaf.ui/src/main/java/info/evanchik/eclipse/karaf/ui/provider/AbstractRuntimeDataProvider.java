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
package info.evanchik.eclipse.karaf.ui.provider;

import info.evanchik.eclipse.karaf.ui.KarafUIPluginActivator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.graphics.Image;
import org.osgi.jmx.codec.OSGiBundle;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public abstract class AbstractRuntimeDataProvider implements RuntimeDataProvider {

    protected final Set<OSGiBundle> bundleSet;
    protected final Map<Long, OSGiBundle> idToBundleMap;

    protected final Set<OSGiServiceWrapper> serviceSet;
    protected final Map<Long, OSGiServiceWrapper> idToServiceMap;

    private final List<RuntimeDataProviderListener> listeners;

    /**
     * Constructor that simply initializes all internal data structures.
     */
    public AbstractRuntimeDataProvider() {
        listeners = new ArrayList<RuntimeDataProviderListener>();

        bundleSet = new HashSet<OSGiBundle>();
        idToBundleMap = new HashMap<Long, OSGiBundle>();

        serviceSet = new HashSet<OSGiServiceWrapper>();
        idToServiceMap = new HashMap<Long, OSGiServiceWrapper>();
    }

    public void addListener(RuntimeDataProviderListener listener) {
        if (listeners.contains(listener)) {
            return;
        }

        listeners.add(listener);
    }

    public OSGiBundle getBundle(long id) {
        return idToBundleMap.get(id);
    }

    public Set<OSGiBundle> getBundles() {
        synchronized (bundleSet) {
            return Collections.unmodifiableSet(bundleSet);
        }
    }

    public Image getIcon() {
        return KarafUIPluginActivator.getDefault().getImageRegistry().get(KarafUIPluginActivator.BUNDLE_OBJ_IMG);
    }

    public OSGiServiceWrapper getService(long id) {
        return idToServiceMap.get(id);
    }

    public Set<OSGiServiceWrapper> getServices() {
        synchronized (serviceSet) {
            return Collections.unmodifiableSet(serviceSet);
        }
    }

    public void removeListener(RuntimeDataProviderListener listener) {
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
    protected void fireProviderChangeEvent(EnumSet<RuntimeDataProviderListener.EventType> type) {
        for (RuntimeDataProviderListener l : listeners) {
            l.providerChange(this, type);
        }
    }

    /**
     * Sends the start event to all registered
     * {@link RuntimeDataProviderListener}S
     */
    protected void fireStartEvent() {
        for (RuntimeDataProviderListener l : listeners) {
            l.providerStart(this);
        }
    }

    /**
     * Sends the stop event to all registered
     * {@link RuntimeDataProviderListener}S
     */
    protected void fireStopEvent() {
        for (RuntimeDataProviderListener l : listeners) {
            l.providerStop(this);
        }
    }
}
