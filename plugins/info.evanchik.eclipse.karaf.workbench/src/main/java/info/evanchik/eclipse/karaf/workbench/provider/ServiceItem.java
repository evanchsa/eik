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
package info.evanchik.eclipse.karaf.workbench.provider;

import java.util.Properties;

import javax.management.openmbean.CompositeData;

import org.apache.aries.jmx.codec.ServiceData;
import org.eclipse.core.runtime.IAdaptable;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
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
        } else if (serviceReference.compareTo(other.serviceReference) == 0) {
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
