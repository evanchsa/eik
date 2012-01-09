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

    private static final String LEFT_FOLDER = "leftFolder";

    @Override
    public void createInitialLayout(final IPageLayout layout) {

        layout.addActionSet("org.eclipse.debug.ui.debugActionSet");
        layout.addActionSet("org.eclipse.debug.ui.launchActionSet");
        layout.addActionSet("org.eclipse.debug.ui.profileActionSet");

        layout.addNewWizardShortcut("org.apache.karaf.eik.ui.karafPlatformProject");

        final IFolderLayout leftFolder =
            layout.createFolder(
                    LEFT_FOLDER,
                    IPageLayout.LEFT,
                    0.20f,
                    layout.getEditorArea());

        leftFolder.addView("org.eclipse.ui.navigator.ProjectExplorer");

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
