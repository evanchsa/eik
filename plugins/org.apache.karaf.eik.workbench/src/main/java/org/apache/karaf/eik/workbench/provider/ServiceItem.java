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
package org.apache.karaf.eik.workbench.provider;

import java.util.Properties;

import javax.management.openmbean.CompositeData;

import org.apache.aries.jmx.codec.ServiceData;
import org.eclipse.core.runtime.IAdaptable;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

public class ServiceItem implements IAdaptable {

    final CompositeData rawServiceData;

    final ServiceReference serviceReference;

    public ServiceItem(final ServiceReference serviceReference) {
        this(serviceReference, null);
    }

    public ServiceItem(final CompositeData compositeData) {
        this(null, compositeData);
    }

    private ServiceItem(final ServiceReference serviceReference, CompositeData compositeData) {
        this.serviceReference = serviceReference;
        this.rawServiceData = compositeData;
    }

    public long getBundleId() {
        if (serviceReference != null) {
            return serviceReference.getBundle().getBundleId();
        } else {
            return ServiceData.from(rawServiceData).getBundleId();
        }
    }

    public long getIdentifier() {
        if (serviceReference != null) {
            return (Long) serviceReference.getProperty(Constants.SERVICE_ID);
        } else {
            return ServiceData.from(rawServiceData).getServiceId();
        }
    }

    public String[] getServiceInterfaces() {
        if (serviceReference != null) {
            return (String[]) serviceReference.getProperty(Constants.OBJECTCLASS);
        } else {
            return ServiceData.from(rawServiceData).getServiceInterfaces();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((rawServiceData == null) ? 0 : rawServiceData.hashCode());
        result = prime * result + ((serviceReference == null) ? 0 : serviceReference.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ServiceItem other = (ServiceItem) obj;
        if (rawServiceData == null) {
            if (other.rawServiceData != null) {
                return false;
            }
        } else if (!rawServiceData.equals(other.rawServiceData)) {
            return false;
        }
        if (serviceReference == null) {
            if (other.serviceReference != null) {
                return false;
            }
        } else if (!serviceReference.equals(other.serviceReference)) {
            return false;
        }
        return true;
    }

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
        final Object o;
        if (adapter == null) {
            o = null;
        } else if (adapter.equals(BundleItem.class)) {
            if (serviceReference != null) {
                o = new BundleItem(serviceReference.getBundle(), null, null);
            } else {
                o = null;
            }
        } else if (adapter.equals(ServiceData.class)) {
            if (serviceReference != null) {
                o = new ServiceData(serviceReference);
            } else {
                o = ServiceData.from(rawServiceData);
            }
        } else if (adapter.equals(ServiceReference.class)) {
            if (serviceReference != null) {
                o = serviceReference;
            } else {
                o = null;
            }
        } else if (adapter.equals(Bundle.class)) {
            if (serviceReference != null) {
                o = serviceReference.getBundle();
            } else {
                o = null;
            }
        } else if (adapter.equals(Properties.class)) {
            if (serviceReference != null) {
                o = new Properties();
                for (String key : serviceReference.getPropertyKeys()) {
                    ((Properties)o).put(key, serviceReference.getProperty(key));
                }
            } else {
                o = null;
            }
        } else {
            o = null;
        }

        return o;
    }

}
