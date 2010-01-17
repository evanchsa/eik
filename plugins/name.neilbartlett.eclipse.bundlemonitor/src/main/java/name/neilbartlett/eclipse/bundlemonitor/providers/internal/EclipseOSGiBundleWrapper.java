/**
 * Copyright (c) 2009 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package name.neilbartlett.eclipse.bundlemonitor.providers.internal;

import static org.osgi.jmx.codec.Util.getBundleExportedPackages;
import static org.osgi.jmx.codec.Util.getBundleFragments;
import static org.osgi.jmx.codec.Util.getBundleHeaders;
import static org.osgi.jmx.codec.Util.getBundleState;
import static org.osgi.jmx.codec.Util.isBundleFragment;
import static org.osgi.jmx.codec.Util.isBundlePersistentlyStarted;
import static org.osgi.jmx.codec.Util.isBundleRequired;
import static org.osgi.jmx.codec.Util.isRequiredBundleRemovalPending;
import static org.osgi.jmx.codec.Util.serviceIds;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.jmx.codec.OSGiBundle;
import org.osgi.jmx.codec.Util;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;

/**
 * Implementation of an {@link OSGiBundle} that defers making expensive data
 * gathering calls in to the framework on construction.
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class EclipseOSGiBundleWrapper extends OSGiBundle {

    private final BundleContext context;

    private final PackageAdmin admin;

    @SuppressWarnings("unused")
    private final StartLevel startLevel;

    private final Bundle bundle;

    /*
     * Data that is cached
     */
    private String[] importedPackages;
    private long[] requiredBundles;
    private long[] requiringBundles;

    /**
     * Constructor that defers making expensive data gathering calls in to the
     * OSGi framework.
     *
     * @param bc
     *            the {@link BundleContext}
     * @param admin
     *            the {@link PackageAdmin} service
     * @param sl
     *            the {@link StartLevel} service
     * @param b
     *            the {@link Bundle} that is being wrapped
     */
    public EclipseOSGiBundleWrapper(BundleContext bc, PackageAdmin admin, StartLevel sl, Bundle b) {
        super(b.getLocation(), b.getBundleId(), b.getSymbolicName(), sl.getBundleStartLevel(b), getBundleState(b), b.getLastModified(),
                isBundlePersistentlyStarted(b, sl), isRequiredBundleRemovalPending(b, bc, admin), isBundleRequired(b, bc, admin),
                isBundleFragment(b, admin), serviceIds(b.getRegisteredServices()), serviceIds(b.getServicesInUse()), getBundleHeaders(b),
                getBundleExportedPackages(b, admin), new String[0], getBundleFragments(b, admin), Util.bundleIds(admin.getHosts(b)),
                new long[0], new long[0]);

        this.context = bc;
        this.admin = admin;
        this.startLevel = sl;
        this.bundle = b;
    }

    @Override
    public String[] getImportedPackages() {
        if (importedPackages == null) {
            importedPackages = Util.getBundleImportedPackages(bundle, context, admin);
        }
        return importedPackages;
    }

    @Override
    public long[] getRequiredBundles() {
        if (requiredBundles == null) {
            requiredBundles = Util.getBundleDependencies(bundle, admin);
        }
        return requiredBundles;
    }

    @Override
    public long[] getRequiringBundles() {
        if (requiringBundles == null) {
            requiringBundles = Util.getBundlesRequiring(bundle, context, admin);
        }
        return requiringBundles;
    }
}
