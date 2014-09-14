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
package org.apache.karaf.eik.workbench.ui;

import org.apache.karaf.eik.workbench.ui.views.bundle.BundlesView;
import org.apache.karaf.eik.workbench.ui.views.services.ServicesView;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

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
