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

import info.evanchik.eclipse.karaf.workbench.KarafWorkbenchActivator;
import info.evanchik.eclipse.karaf.workbench.WorkbenchServiceListener;
import info.evanchik.eclipse.karaf.workbench.WorkbenchServiceManager;
import info.evanchik.eclipse.karaf.workbench.provider.RuntimeDataProvider;
import info.evanchik.eclipse.karaf.workbench.provider.RuntimeDataProviderListener;

import java.util.EnumSet;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.BundleContext;

public class BundlesContentProvider implements IStructuredContentProvider, ITreeContentProvider {

    private final class RuntimeDataProviderWorkbenchServiceListener implements WorkbenchServiceListener<RuntimeDataProvider> {
        @Override
        public void serviceAdded(final RuntimeDataProvider service) {
            safeRefresh();
        }

        @Override
        public void serviceRemoved(final RuntimeDataProvider service) {
            safeRefresh();
        }
    }

    private final class DataProviderListener implements RuntimeDataProviderListener {
        @Override
        public void providerChange(final RuntimeDataProvider source, final EnumSet<RuntimeDataProviderListener.EventType> type) {
            safeRefresh();
        }

        @Override
        public void providerStart(final RuntimeDataProvider source) {
            safeRefresh();
        }

        @Override
        public void providerStop(final RuntimeDataProvider source) {
            safeRefresh();
        }
    }

    protected BundleContext bundleContext;

    protected RuntimeDataProviderListener dataProviderListener;

    protected WorkbenchServiceListener<RuntimeDataProvider> listener;

    protected WorkbenchServiceManager<RuntimeDataProvider> runtimeDataProviderManager;

    protected volatile Viewer viewer;

    public BundlesContentProvider() {
        runtimeDataProviderManager = KarafWorkbenchActivator.getDefault().getRuntimeDataProviderManager();
        listener = new RuntimeDataProviderWorkbenchServiceListener();
        runtimeDataProviderManager.addListener(listener);

        dataProviderListener = new DataProviderListener();
    }

    @Override
    public void dispose() {
        runtimeDataProviderManager.removeListener(listener);

        for (final RuntimeDataProvider rdp : runtimeDataProviderManager.getServices()) {
            rdp.removeListener(dataProviderListener);
        }
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
        return runtimeDataProviderManager.getServices().toArray(new Object[0]);
    }

    @Override
    public Object getParent(final Object element) {
        if(element instanceof RuntimeDataProvider) {
            return null;
        }

        final List<RuntimeDataProvider> runtimeDataProviders = runtimeDataProviderManager.getServices();
        for (final RuntimeDataProvider b : runtimeDataProviders) {
            if (b.getBundles().contains(element)) {
                return b;
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

        for (final RuntimeDataProvider rdp : runtimeDataProviderManager.getServices()) {
            rdp.addListener(dataProviderListener);
        }
    }

    public void setRuntimeDataProviderManager(final WorkbenchServiceManager<RuntimeDataProvider> runtimeDataProviderManager) {
        this.runtimeDataProviderManager = runtimeDataProviderManager;
    }

    private void safeRefresh() {
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
