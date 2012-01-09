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
package org.apache.karaf.eik.workbench.ui.editor;

import org.apache.karaf.eik.core.KarafPlatformDetails;
import org.apache.karaf.eik.core.KarafPlatformModel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

public class KarafPlatformEditorPart extends FormEditor {

    public static final String ID = "org.apache.karaf.eik.ui.editors.KarafPlatformEditor";

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
            addPage(new KarafFeaturesFormPage(this));
        } catch (final PartInitException e) {
            // TODO: Handle PartInitException
        }
    }

}
