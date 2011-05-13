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
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class BundlesContentProvider implements IStructuredContentProvider, ITreeContentProvider,
        RuntimeDataProviderListener  {

    /**
     *
     * @author Stephen Evanchik (evanchsa@gmail.com)
     *
     */
    private final class RuntimeDataProviderServiceTracker implements ServiceTrackerCustomizer {

        @Override
        public Object addingService(final ServiceReference reference) {
            final RuntimeDataProvider provider = (RuntimeDataProvider)bundleContext.getService(reference);

            if(!runtimeDataProviders.contains(provider)) {
                provider.addListener(BundlesContentProvider.this);
                runtimeDataProviders.add(provider);

                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        if (!viewer.getControl().isDisposed()) {
                            viewer.refresh();
                        }
                    }
                });
            }

            return provider;
        }

        @Override
        public void modifiedService(final ServiceReference reference, final Object service) {
            // Intentionally left blank
        }

        @Override
        public void removedService(final ServiceReference reference, final Object service) {
            if(runtimeDataProviders.contains(service)) {
                final RuntimeDataProvider provider = (RuntimeDataProvider)service;
                provider.removeListener(BundlesContentProvider.this);

                runtimeDataProviders.remove(service);

                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        if (!viewer.getControl().isDisposed()) {
                            viewer.refresh();
                        }
                    }
                });
            }
        }
    };

    protected BundleContext bundleContext;

    protected ServiceTracker dataProviderServiceTracker;

    protected EclipseRuntimeDataProvider eclipseWorkbenchDataProvider;

    protected final List<RuntimeDataProvider> runtimeDataProviders = Collections.synchronizedList(new ArrayList<RuntimeDataProvider>());

    protected ServiceTrackerCustomizer serviceTrackerCustomizer;

    protected volatile Viewer viewer;

    @Override
    public void dispose() {
        synchronized (runtimeDataProviders) {
            for (final RuntimeDataProvider b : runtimeDataProviders) {
                b.removeListener(this);
            }
        }

        dataProviderServiceTracker.close();
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
        if(element instanceof RuntimeDataProvider) {
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
            final RuntimeDataProvider dataProvider = (RuntimeDataProvider) element;
            final boolean hasChildren = dataProvider.getBundles().size() > 0;
            return hasChildren;
        }

        return false;
    }

    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        if (!(newInput instanceof BundleContext)) {
            return;
        }

        this.viewer = viewer;
        this.bundleContext = (BundleContext) newInput;

        /*
         * The Eclipse workbench RuntimeDataProvider and ServiceTracker only
         * need to be initialized once because there is only one Eclipse
         * workbench per content provider instance
         */
        if (eclipseWorkbenchDataProvider == null) {
            eclipseWorkbenchDataProvider = new EclipseRuntimeDataProvider(bundleContext);
            eclipseWorkbenchDataProvider.addListener(this);
            eclipseWorkbenchDataProvider.start();

            runtimeDataProviders.add(eclipseWorkbenchDataProvider);
        }

        if (serviceTrackerCustomizer == null) {
            this.serviceTrackerCustomizer = new RuntimeDataProviderServiceTracker();
            this.dataProviderServiceTracker = new ServiceTracker(bundleContext, RuntimeDataProvider.class.getName(), serviceTrackerCustomizer);
            this.dataProviderServiceTracker.open();
        }
    }

    @Override
    public void providerChange(final RuntimeDataProvider source, final EnumSet<RuntimeDataProviderListener.EventType> type) {

        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                if (!viewer.getControl().isDisposed()) {
                    viewer.refresh();
                }
            }

        });
    }

    @Override
    public void providerStart(final RuntimeDataProvider source) {
    }

    @Override
    public void providerStop(final RuntimeDataProvider source) {
    }
}
