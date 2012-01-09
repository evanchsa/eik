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
package org.apache.karaf.eik.hooks;

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

public class KarafClassloadingHook implements ClassLoadingHook {

    @Override
    public boolean addClassPathEntry(@SuppressWarnings("rawtypes") ArrayList cpEntries, String cp,
            ClasspathManager hostmanager, BaseData sourcedata,
            ProtectionDomain sourcedomain) {
        return false;
    }

    @Override
    public BaseClassLoader createClassLoader(ClassLoader parent,
            ClassLoaderDelegate delegate, BundleProtectionDomain domain,
            BaseData data, String[] bundleclasspath) {
        return null;
    }

    @Override
    public String findLibrary(BaseData data, String libName) {
        return null;
    }

    @Override
    public ClassLoader getBundleClassLoaderParent() {
        return null;
    }

    @Override
    public void initializedClassLoader(BaseClassLoader baseClassLoader,
            BaseData data) {
    }

    @Override
    public byte[] processClass(String name, byte[] classbytes,
            ClasspathEntry classpathEntry, BundleEntry entry,
            ClasspathManager manager) {
        return null;
    }

}
