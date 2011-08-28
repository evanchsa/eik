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
package info.evanchik.eclipse.karaf.workbench.ui.editor;

import info.evanchik.eclipse.karaf.core.KarafPlatformDetails;
import info.evanchik.eclipse.karaf.core.KarafPlatformModel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafPlatformEditorPart extends FormEditor {

    public static final String ID = "info.evanchik.eclipse.karaf.ui.editors.KarafPlatformEditor";

    private KarafPlatformEditorInput karafEditorInput;

    private KarafPlatformModel karafPlatform;

    private KarafPlatformDetails platformDetails;

    @Override
    public void doSave(final IProgressMonitor monitor) {
    }

    @Override
    public void doSaveAs() {
    }

    public KarafPlatformEditorInput getKarafEditorInput() {
        return karafEditorInput;
    }

    public KarafPlatformModel getKarafPlatform() {
        return karafPlatform;
    }

    public KarafPlatformDetails getPlatformDetails() {
        return platformDetails;
    }

    @Override
    public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
        super.init(site, input);

        karafEditorInput = (KarafPlatformEditorInput) input;

        karafPlatform = karafEditorInput.getKarafPlatform();

        setPartName(karafEditorInput.getName());

        platformDetails = (KarafPlatformDetails) karafPlatform.getAdapter(KarafPlatformDetails.class);
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    protected void addPages() {
        try {
            addPage(new KarafPlatformGeneralFormPage(this));
            addPage(new KarafPlatformRuntimeFormPage(this));
        } catch (final PartInitException e) {
            // TODO: Handle PartInitException
        }
    }
}
