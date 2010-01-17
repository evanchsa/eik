/*
 * Copyright (c) 2008 Neil Bartlett
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Neil Bartlett - initial implementation
 */
package name.neilbartlett.eclipse.bundlemonitor.internal;

import org.eclipse.jface.viewers.Viewer;

/**
 * Instances of this interface represent update operations to be carried out on
 * a Viewer object in the UI thread.
 *
 * @see SWTConcurrencyUtils#safeAsyncUpdate(Viewer, ViewerUpdater)
 */
public interface ViewerUpdater {
    void updateViewer(Viewer viewer);
}
