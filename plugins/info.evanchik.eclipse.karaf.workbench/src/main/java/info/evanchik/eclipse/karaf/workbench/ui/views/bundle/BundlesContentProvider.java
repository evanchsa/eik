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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class BundlesContentProvider implements IStructuredContentProvider, ITreeContentProvider,
        RuntimeDataProviderListener, ServiceTrackerCustomizer {

    protected final BundleContext context;

    protected final ViewPart parent;

    protected final StructuredViewer viewer;

    protected final EclipseRuntimeDataProvider eclipseWorkbenchDataProvider;

    protected final List<RuntimeDataProvider> runtimeDataProviders;

    protected final ServiceTracker dataProviderServiceTracker;

    public BundlesContentProvider(final ViewPart parent, final StructuredViewer viewer, final BundleContext context) {
        this.parent = parent;
        this.viewer = viewer;
        this.context = context;

        this.runtimeDataProviders = new ArrayList<RuntimeDataProvider>();
        this.eclipseWorkbenchDataProvider = new EclipseRuntimeDataProvider(context);
        this.dataProviderServiceTracker = new ServiceTracker(context, RuntimeDataProvider.class.getName(), this);

        runtimeDataProviders.add(eclipseWorkbenchDataProvider);
    }

    @Override
    public Object addingService(final ServiceReference reference) {
        final RuntimeDataProvider provider = (RuntimeDataProvider)context.getService(reference);

        if(!runtimeDataProviders.contains(provider)) {
            provider.addListener(this);
            runtimeDataProviders.add(provider);

            Display.getDefault().asyncExec(new Runnable() {

                @Override
                public void run() {
                    viewer.refresh();
                }

            });
        }

        return provider;
    }

    @Override
    public void providerChange(final RuntimeDataProvider source, final EnumSet<RuntimeDataProviderListener.EventType> type) {

        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                viewer.refresh();
            }

        });
    }

    @Override
    public void dispose() {
    }

    @Override
    public Object[] getChildren(final Object parentElement) {
        if (parentElement instanceof RuntimeDataProvider) {
            return ((RuntimeDataProvider) parentElement).getBundles().toArray(new Object[0]);
        }

        return null;
    }

    @Override
    public Object[] getElements(final Object inputElement) {
        return runtimeDataProviders.toArray(new Object[0]);
    }

    @Override
    public Object getParent(final Object element) {
        if(element instanceof RuntimeDataProvider == false) {
            return null;
        }

        synchronized(runtimeDataProviders) {
            for (final RuntimeDataProvider b : runtimeDataProviders) {
                if (b.getBundles().contains(element)) {
                    return b;
                }
            }
        }

        return null;
    }

    @Override
    public boolean hasChildren(final Object element) {
        if (element instanceof RuntimeDataProvider) {
            return true;
        }

        return false;
    }

    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    }

    @Override
    public void modifiedService(final ServiceReference reference, final Object service) {
    }

    @Override
    public void providerStart(final RuntimeDataProvider source) {
    }

    @Override
    public void providerStop(final RuntimeDataProvider source) {
    }

    @Override
    public void removedService(final ServiceReference reference, final Object service) {
        if(runtimeDataProviders.contains(service)) {
            final RuntimeDataProvider provider = (RuntimeDataProvider)service;
            provider.removeListener(this);

            runtimeDataProviders.remove(service);

            Display.getDefault().asyncExec(new Runnable() {

                @Override
                public void run() {
                    viewer.refresh();
                }

            });
        }
    }

    public void start() {
        synchronized (runtimeDataProviders) {
            for (final RuntimeDataProvider provider : runtimeDataProviders) {
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
            for (final RuntimeDataProvider b : runtimeDataProviders) {
                b.removeListener(this);
            }
        }

        dataProviderServiceTracker.close();
    }
}
