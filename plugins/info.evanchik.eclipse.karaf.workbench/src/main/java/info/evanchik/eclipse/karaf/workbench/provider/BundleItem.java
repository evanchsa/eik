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

import static org.osgi.jmx.framework.BundleStateMBean.ACTIVE;
import static org.osgi.jmx.framework.BundleStateMBean.INSTALLED;
import static org.osgi.jmx.framework.BundleStateMBean.RESOLVED;
import static org.osgi.jmx.framework.BundleStateMBean.STARTING;
import static org.osgi.jmx.framework.BundleStateMBean.STOPPING;
import static org.osgi.jmx.framework.BundleStateMBean.UNINSTALLED;
import static org.osgi.jmx.framework.BundleStateMBean.UNKNOWN;

import javax.management.openmbean.CompositeData;

import org.apache.aries.jmx.codec.BundleData;
import org.eclipse.core.runtime.IAdaptable;
import org.osgi.framework.Bundle;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class BundleItem implements IAdaptable {

    private final Bundle bundleDelegate;

    private final PackageAdmin packageAdmin;

    private final BundleData remoteBundleDelegate;

    private final StartLevel startLevel;

    /**
     *
     * @param bundle
     * @return
     */
    public static String getBundleState(Bundle bundle) {
        String state = UNKNOWN;
        switch (bundle.getState()) {
        case Bundle.INSTALLED:
            state = INSTALLED;
            break;
        case Bundle.RESOLVED:
            state = RESOLVED;
            break;
        case Bundle.STARTING:
            state = STARTING;
            break;
        case Bundle.ACTIVE:
            state = ACTIVE;
            break;
        case Bundle.STOPPING:
            state = STOPPING;
            break;
        case Bundle.UNINSTALLED:
            state = UNINSTALLED;
        }
        return state;
    }

    public BundleItem(final Bundle bundle, final StartLevel startLevel, final PackageAdmin packageAdmin) {
        if (bundle == null) {
            throw new NullPointerException("bundle");
        }

        this.bundleDelegate = bundle;
        this.remoteBundleDelegate = null;
        this.startLevel = startLevel;
        this.packageAdmin = packageAdmin;
    }

    public BundleItem(final CompositeData compositeData) {
        if (compositeData == null) {
            throw new NullPointerException("compositeData");
        }

        this.bundleDelegate = null;
        this.remoteBundleDelegate = BundleData.from(compositeData);
        this.startLevel = null;
        this.packageAdmin = null;
    }

    public long getIdentifier() {
        if (bundleDelegate != null) {
            return bundleDelegate.getBundleId();
        } else {
            return remoteBundleDelegate.getIdentifier();
        }
    }

    public String getLocation() {
        if (bundleDelegate != null) {
            return bundleDelegate.getLocation();
        } else {
            return remoteBundleDelegate.getLocation();
        }
    }

    public String getState() {
        if (bundleDelegate != null) {
            return getBundleState(bundleDelegate);
        } else {
            return remoteBundleDelegate.getState();
        }
    }

    public String getSymbolicName() {
        if (bundleDelegate != null) {
            return bundleDelegate.getSymbolicName();
        } else {
            return remoteBundleDelegate.getSymbolicName();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bundleDelegate == null) ? 0 : bundleDelegate.hashCode());
        result = prime * result + ((remoteBundleDelegate == null) ? 0 : remoteBundleDelegate.hashCode());
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
        BundleItem other = (BundleItem) obj;
        if (bundleDelegate == null) {
            if (other.bundleDelegate != null) {
                return false;
            }
        } else if (bundleDelegate.getBundleId() != other.bundleDelegate.getBundleId()) {
            return false;
        }
        if (remoteBundleDelegate == null) {
            if (other.remoteBundleDelegate != null) {
                return false;
            }
        } else if (remoteBundleDelegate.getIdentifier() != other.remoteBundleDelegate.getIdentifier()) {
            return false;
        }
        return true;
    }

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
        final Object o;
        if (adapter == null) {
            o = null;
        } else if (adapter.equals(Bundle.class)) {
            if (bundleDelegate != null) {
                o = bundleDelegate;
            } else {
                o = null;
            }
        } else if (adapter.equals(BundleData.class)) {
            if (bundleDelegate != null && packageAdmin != null && startLevel != null) {
                o = new BundleData(
                        bundleDelegate.getBundleContext(),
                        bundleDelegate,
                        packageAdmin,
                        startLevel);
            } else {
                o = remoteBundleDelegate;
            }
        } else {
            o = null;
        }

        return o;
    }
}
