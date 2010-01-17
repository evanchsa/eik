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
import org.eclipse.swt.widgets.Display;

public class SWTConcurrencyUtils {

    private SWTConcurrencyUtils() {
        // Prevent instantiation
    }

    /**
     * Safely execute a UI update against the specified Viewer, if the viewer's
     * control has not been disposed. If the control <em>has</em> been disposed
     * then the update will be discarded. This method may be called from any
     * thread.
     *
     * @param viewer
     *            The viewer to update.
     * @param updater
     *            The update operation to be performed in the UI thread.
     */
    public static void safeAsyncUpdate(final Viewer viewer, final ViewerUpdater updater) {
        Runnable runnable = new Runnable() {
            public void run() {
                if (!viewer.getControl().isDisposed()) {
                    updater.updateViewer(viewer);
                }
            }
        };
        if (!viewer.getControl().isDisposed()) {
            Display display = viewer.getControl().getDisplay();
            if (display.getThread() == Thread.currentThread()) {
                runnable.run();
            } else {
                display.asyncExec(runnable);
            }
        }
    }

    /**
     * Convenience method for refreshing a viewer. This method may be called
     * from any thread.
     *
     * @param viewer
     *            The viewer to refresh.
     */
    public static void safeRefresh(Viewer viewer) {
        safeAsyncUpdate(viewer, new ViewerUpdater() {
            public void updateViewer(Viewer viewer) {
                viewer.refresh();
            }
        });
    }
}
