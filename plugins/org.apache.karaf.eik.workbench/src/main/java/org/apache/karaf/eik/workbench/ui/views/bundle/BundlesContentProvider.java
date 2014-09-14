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
package org.apache.karaf.eik.workbench.ui.views.bundle;

import org.apache.karaf.eik.workbench.KarafWorkbenchActivator;
import org.apache.karaf.eik.workbench.WorkbenchServiceListener;
import org.apache.karaf.eik.workbench.WorkbenchServiceManager;
import org.apache.karaf.eik.workbench.provider.RuntimeDataProvider;
import org.apache.karaf.eik.workbench.provider.RuntimeDataProviderListener;

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
            return ((RuntimeDataProvider) parentElement).getBundles().toArray();
        }

        return null;
    }

    @Override
    public Object[] getElements(final Object inputElement) {
        return runtimeDataProviderManager.getServices().toArray();
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
