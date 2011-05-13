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
package info.evanchik.eclipse.karaf.workbench.ui;

import info.evanchik.eclipse.karaf.workbench.ui.views.JmxServersView;
import info.evanchik.eclipse.karaf.workbench.ui.views.bundle.BundlesView;
import info.evanchik.eclipse.karaf.workbench.ui.views.services.ServicesView;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafWorkbenchPerspectiveFactory implements IPerspectiveFactory {

    private static final String BOTTOM_FOLDER = "bottomFolder";
    @Override
    public void createInitialLayout(final IPageLayout layout) {

        layout.addView(JmxServersView.VIEW_ID, IPageLayout.LEFT, 0.20f, layout.getEditorArea());

        final IFolderLayout bottomFolder =
            layout.createFolder(
                    BOTTOM_FOLDER,
                    IPageLayout.BOTTOM,
                    0.75f,
                    layout.getEditorArea());

        bottomFolder.addView(BundlesView.VIEW_ID);
        bottomFolder.addView(ServicesView.VIEW_ID);
        bottomFolder.addView("org.eclipse.ui.console.ConsoleView");
    }
}
