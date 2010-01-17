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
package info.evanchik.eclipse.karaf.hooks;

import java.security.ProtectionDomain;
import java.util.ArrayList;

import org.eclipse.osgi.baseadaptor.BaseData;
import org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry;
import org.eclipse.osgi.baseadaptor.hooks.ClassLoadingHook;
import org.eclipse.osgi.baseadaptor.loader.BaseClassLoader;
import org.eclipse.osgi.baseadaptor.loader.ClasspathEntry;
import org.eclipse.osgi.baseadaptor.loader.ClasspathManager;
import org.eclipse.osgi.framework.adaptor.BundleProtectionDomain;
import org.eclipse.osgi.framework.adaptor.ClassLoaderDelegate;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafClassloadingHook implements ClassLoadingHook {

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.osgi.baseadaptor.hooks.ClassLoadingHook#addClassPathEntry
     * (java.util.ArrayList, java.lang.String,
     * org.eclipse.osgi.baseadaptor.loader.ClasspathManager,
     * org.eclipse.osgi.baseadaptor.BaseData, java.security.ProtectionDomain)
     */
    public boolean addClassPathEntry(ArrayList cpEntries, String cp,
            ClasspathManager hostmanager, BaseData sourcedata,
            ProtectionDomain sourcedomain) {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.osgi.baseadaptor.hooks.ClassLoadingHook#createClassLoader
     * (java.lang.ClassLoader,
     * org.eclipse.osgi.framework.adaptor.ClassLoaderDelegate,
     * org.eclipse.osgi.framework.adaptor.BundleProtectionDomain,
     * org.eclipse.osgi.baseadaptor.BaseData, java.lang.String[])
     */
    public BaseClassLoader createClassLoader(ClassLoader parent,
            ClassLoaderDelegate delegate, BundleProtectionDomain domain,
            BaseData data, String[] bundleclasspath) {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.osgi.baseadaptor.hooks.ClassLoadingHook#findLibrary(org.eclipse
     * .osgi.baseadaptor.BaseData, java.lang.String)
     */
    public String findLibrary(BaseData data, String libName) {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @seeorg.eclipse.osgi.baseadaptor.hooks.ClassLoadingHook#
     * getBundleClassLoaderParent()
     */
    public ClassLoader getBundleClassLoaderParent() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.osgi.baseadaptor.hooks.ClassLoadingHook#initializedClassLoader
     * (org.eclipse.osgi.baseadaptor.loader.BaseClassLoader,
     * org.eclipse.osgi.baseadaptor.BaseData)
     */
    public void initializedClassLoader(BaseClassLoader baseClassLoader,
            BaseData data) {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.osgi.baseadaptor.hooks.ClassLoadingHook#processClass(java
     * .lang.String, byte[], org.eclipse.osgi.baseadaptor.loader.ClasspathEntry,
     * org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry,
     * org.eclipse.osgi.baseadaptor.loader.ClasspathManager)
     */
    public byte[] processClass(String name, byte[] classbytes,
            ClasspathEntry classpathEntry, BundleEntry entry,
            ClasspathManager manager) {
        return null;
    }

}
