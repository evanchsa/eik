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
package org.apache.karaf.eik.workbench.internal;

import org.apache.karaf.eik.ui.IKarafProject;
import org.apache.karaf.eik.workbench.KarafWorkbenchActivator;
import org.apache.karaf.eik.workbench.ui.editor.KarafPlatformEditorInput;
import org.apache.karaf.eik.workbench.ui.editor.KarafPlatformEditorPart;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

public class WorkbenchActionProvider extends CommonActionProvider {

    @Override
    public void init(final ICommonActionExtensionSite aSite) {
        super.init(aSite);

        aSite.getStructuredViewer().addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(final DoubleClickEvent event) {
                // TODO: This method is a mess: clean it up!
                if (!(event.getSelection() instanceof IStructuredSelection)) {
                    return;
                }

                final IStructuredSelection structuredSelection = (IStructuredSelection) event.getSelection();
                if (structuredSelection.size() > 1) {
                    return;
                }

                final Object element = structuredSelection.getFirstElement();
                if (!(element instanceof IProject)) {
                    return;
                }

                final IProject project = (IProject) element;
                final IKarafProject karafProject = (IKarafProject) project.getAdapter(IKarafProject.class);
                final IEditorInput editorInput = new KarafPlatformEditorInput(karafProject);

                try {
                    final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                    page.openEditor(editorInput, KarafPlatformEditorPart.ID);
                } catch (final PartInitException e) {
                    KarafWorkbenchActivator.getLogger().error("Unable to open editor for " + karafProject.getName(), e);
                }
            }
        });
    }

}
