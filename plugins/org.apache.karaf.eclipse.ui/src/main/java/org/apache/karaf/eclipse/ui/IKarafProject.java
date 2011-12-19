/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.karaf.eclipse.ui;

import java.util.Properties;

import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface IKarafProject extends IAdaptable {

    /**
     * The root path of of the {@link IProject} resources that are specific to
     * the Karaf integration
     */
    public static final IPath ROOT_PATH = new Path(".bin");

    /**
     * The root path of the {@link IProject} resources specific to the installed
     * {@link KarafPlatformModel}
     * <p>
     * The directory layout under this path is specific to the
     * {@code KarafPlatformModel} to which this {@code IKarafProject} is
     * attached
     */
    public static final IPath ROOT_PLATFORM_PATH = ROOT_PATH.append("platform");

    /**
     * Returns a handle to the named file within this {@code IKarafProject}
     * relative to {@link #ROOT_PATH}
     *
     * @param name
     *            the name of the file resource to retrieve
     * @return the handle of the file
     */
    public IFile getFile(String name);

    /**
     * Returns a handle to the named folder within this {@code IKarafProject}
     * relative to {@link #ROOT_PATH}
     *
     * @param name
     *            the name of the folder resource to retrieve
     * @return the handle of the folder
     */
    public IFolder getFolder(String name);

    /**
     * Returns the {@link IPath} where this {@code IKarafProject} is contained
     * <p>
     * This is usually the workspace root
     *
     * @return the {@code IPath} where this {@code IKarafProject} is contained
     */
    public IPath getLocation();

    /**
     * Returns the name of this {@code IKarafProject}
     * <p>
     * This is always equal to {@link IProject#getName()}
     *
     * @return the name of the {@code IKarafProject}
     */
    public String getName();

    /**
     * Returns a handle to the named file within this {@code IKarafProject}
     * relative to {@link #ROOT_PLATFORM_PATH}
     * <p>
     * The file layout of
     *
     * @param name
     *            the name of the file resource to retrieve
     * @return the handle of the file
     */
    public IFile getPlatformFile(String name);

    /**
     * Returns the root directory for the {@link KarafPlatformModel} to which
     * this {@code IKarafProject} is attached
     *
     * @return the root directory of the {@code KarafPlatformModel}
     */
    public IPath getPlatformRootDirectory();

    /**
     * Returns the raw {@link IProject} handle for this {@code IKarafProject}
     *
     * @return the raw {@code IProject} handle
     */
    public IProject getProjectHandle();

    /**
     *
     * @return
     */
    public Properties getRuntimeProperties();
}
