/**
 * Copyright (c) 2011 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package info.evanchik.eclipse.karaf.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface IKarafProject extends IAdaptable {

    /**
     *
     * @param name
     * @return
     */
    public IFile getFile(String name);

    /**
     *
     * @param name
     * @return
     */
    public IFolder getFolder(String name);

    /**
     *
     * @return
     */
    public IPath getLocation();

    /**
     *
     * @return
     */
    public String getName();

    /**
     *
     * @return
     */
    public IPath getPlatformRootDirectory();

    /**
     *
     * @return
     */
    public IProject getProjectHandle();
}
