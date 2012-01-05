/**
 * Copyright (c) 2010 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package info.evanchik.eclipse.karaf.workbench.internal;

import info.evanchik.eclipse.karaf.workbench.MBeanProvider;
import info.evanchik.eclipse.karaf.workbench.provider.BundleItem;
import info.evanchik.eclipse.karaf.workbench.provider.ServiceItem;

import java.util.Collections;
import java.util.Map;

import javax.management.openmbean.CompositeData;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class MBeanServiceItem extends ServiceItem {

    private final Map<Long, BundleItem> bundleCache;

    @SuppressWarnings("unused")
    private final MBeanProvider mbeanProvider;

    public MBeanServiceItem(final CompositeData compositeData, final MBeanProvider mbeanProvider, final Map<Long, BundleItem> bundleCache) {
        super(compositeData);

        this.mbeanProvider = mbeanProvider;
        this.bundleCache = Collections.synchronizedMap(bundleCache);
    }

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
        Object o;
        if (adapter == null) {
            o = null;
        } else {
            o = super.getAdapter(adapter);
            if (o == null && adapter.equals(BundleItem.class)) {
                if (bundleCache.containsKey(Long.valueOf(getBundleId()))) {
                    o = bundleCache.get(Long.valueOf(getBundleId()));
                } else {
                    // TODO: Fetch this data from the remote source
                    o = null;
                }
            }
        }

        return o;
    }
}
