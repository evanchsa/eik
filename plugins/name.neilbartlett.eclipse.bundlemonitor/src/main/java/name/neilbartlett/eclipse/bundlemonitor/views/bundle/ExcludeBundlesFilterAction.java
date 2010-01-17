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
package name.neilbartlett.eclipse.bundlemonitor.views.bundle;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.StructuredViewer;

public class ExcludeBundlesFilterAction extends Action {

    private final ExcludeBundlesViewerFilter filter;
    private final StructuredViewer viewer;

    public ExcludeBundlesFilterAction(String label, int state, StructuredViewer viewer) {
        super(label, IAction.AS_CHECK_BOX);
        this.viewer = viewer;
        setChecked(true);
        filter = new ExcludeBundlesViewerFilter(state);
    }

    @Override
    public void run() {
        if (!isChecked()) {
            viewer.addFilter(filter);
        } else {
            viewer.removeFilter(filter);
        }
    }

}
