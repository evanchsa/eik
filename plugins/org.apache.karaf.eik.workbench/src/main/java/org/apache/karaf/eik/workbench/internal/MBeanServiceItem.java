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

import org.apache.karaf.eik.workbench.MBeanProvider;
import org.apache.karaf.eik.workbench.provider.BundleItem;
import org.apache.karaf.eik.workbench.provider.ServiceItem;

import java.util.Collections;
import java.util.Map;

import javax.management.openmbean.CompositeData;

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
