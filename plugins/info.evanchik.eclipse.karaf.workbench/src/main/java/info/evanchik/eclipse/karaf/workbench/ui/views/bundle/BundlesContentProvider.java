/*
 * Copyright (c) 2008 Neil Bartlett
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Neil Bartlett - initial implementation
 *     Stephen Evanchik - Updated to use data provider services
 */
package info.evanchik.eclipse.karaf.workbench.ui.views.bundle;

import info.evanchik.eclipse.karaf.workbench.internal.eclipse.EclipseRuntimeDataProvider;
import info.evanchik.eclipse.karaf.workbench.provider.RuntimeDataProvider;
import info.evanchik.eclipse.karaf.workbench.provider.RuntimeDataProviderListener;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class BundlesContentProvider implements IStructuredContentProvider, ITreeContentProvider,
        RuntimeDataProviderListener, ServiceTrackerCustomizer {

    protected final BundleContext context;

    protected final StructuredViewer viewer;

    protected final EclipseRuntimeDataProvider eclipseWorkbenchDataProvider;

    protected final List<RuntimeDataProvider> runtimeDataProviders;

    protected final ServiceTracker dataProviderServiceTracker;

    public BundlesContentProvider(StructuredViewer viewer, BundleContext context) {
        this.viewer = viewer;
        this.context = context;

        this.runtimeDataProviders = new ArrayList<RuntimeDataProvider>();
        this.eclipseWorkbenchDataProvider = new EclipseRuntimeDataProvider(context);
        this.dataProviderServiceTracker = new ServiceTracker(context, RuntimeDataProvider.class.getName(), this);

        runtimeDataProviders.add(eclipseWorkbenchDataProvider);
    }

    public Object addingService(ServiceReference reference) {
        final RuntimeDataProvider provider = (RuntimeDataProvider)context.getService(reference);

        if(!runtimeDataProviders.contains(provider)) {
            provider.addListener(this);
            runtimeDataProviders.add(provider);
/*
            SWTConcurrencyUtils.safeAsyncUpdate(viewer, new ViewerUpdater() {
                public void updateViewer(Viewer viewer) {
                    viewer.refresh();
                }
            });
*/
        }

        return provider;
    }

    public void providerChange(RuntimeDataProvider source, EnumSet<RuntimeDataProviderListener.EventType> type) {
        // SWTConcurrencyUtils.safeRefresh(viewer);
    }

    public void dispose() {
    }

    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof RuntimeDataProvider) {
            return ((RuntimeDataProvider) parentElement).getBundles().toArray(new Object[0]);
        }

        return null;
    }

    public Object[] getElements(Object inputElement) {
        return runtimeDataProviders.toArray(new Object[0]);
    }

    public Object getParent(Object element) {
        if(element instanceof RuntimeDataProvider == false) {
            return null;
        }

        synchronized(runtimeDataProviders) {
            for (RuntimeDataProvider b : runtimeDataProviders) {
                if (b.getBundles().contains(element)) {
                    return b;
                }
            }
        }

        return null;
    }

    public boolean hasChildren(Object element) {
        if (element instanceof RuntimeDataProvider) {
            return true;
        }

        return false;
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    public void modifiedService(ServiceReference reference, Object service) {
    }

    public void providerStart(RuntimeDataProvider source) {
    }

    public void providerStop(RuntimeDataProvider source) {
    }

    public void removedService(ServiceReference reference, Object service) {
        if(runtimeDataProviders.contains(service)) {
            final RuntimeDataProvider provider = (RuntimeDataProvider)service;
            provider.removeListener(this);

            runtimeDataProviders.remove(service);
/*
            SWTConcurrencyUtils.safeAsyncUpdate(viewer, new ViewerUpdater() {
                public void updateViewer(Viewer viewer) {
                    viewer.refresh();
                }
            });
*/
        }
    }

    public void start() {
        synchronized (runtimeDataProviders) {
            for (RuntimeDataProvider provider : runtimeDataProviders) {
                provider.addListener(this);
            }
        }

        eclipseWorkbenchDataProvider.start();

        dataProviderServiceTracker.open();

        // Refresh
        viewer.refresh();
    }

    public void stop() {
        synchronized (runtimeDataProviders) {
            for (RuntimeDataProvider b : runtimeDataProviders) {
                b.removeListener(this);
            }
        }

        dataProviderServiceTracker.close();
    }
}
