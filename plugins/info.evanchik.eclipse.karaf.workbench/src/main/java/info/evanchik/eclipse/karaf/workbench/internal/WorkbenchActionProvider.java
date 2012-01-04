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
package info.evanchik.eclipse.karaf.workbench.internal;

import info.evanchik.eclipse.karaf.ui.IKarafProject;
import info.evanchik.eclipse.karaf.workbench.KarafWorkbenchActivator;
import info.evanchik.eclipse.karaf.workbench.ui.editor.KarafPlatformEditorInput;
import info.evanchik.eclipse.karaf.workbench.ui.editor.KarafPlatformEditorPart;

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

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
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
