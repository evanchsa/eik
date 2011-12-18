/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.karaf.eclipse.workbench.ui.editor;

import org.apache.karaf.eclipse.workbench.ui.forms.FeaturesManagementForm;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;

/**
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafFeaturesFormPage extends FormPage {

    public static final String ID = "org.apache.karaf.eclipse.editors.page.Features";

    private static final String TITLE = "Features";

    private final KarafPlatformEditorPart editor;

    private FeaturesManagementForm featuresManagementForm;

    /**
     *
     * @param editor
     */
    public KarafFeaturesFormPage(final KarafPlatformEditorPart editor) {
        super(editor, ID, TITLE);

        this.editor = editor;
    }

    @Override
    public void doSave(final IProgressMonitor monitor) {
        super.doSave(monitor);
    }

    @Override
    protected void createFormContent(final IManagedForm managedForm) {
        final GridLayout layout = new GridLayout(1, true);
        final GridData data = new GridData(GridData.FILL_BOTH);

        managedForm.getForm().getBody().setLayout(layout);
        managedForm.getForm().getBody().setLayoutData(data);

        featuresManagementForm = new FeaturesManagementForm(
                editor.getKarafEditorInput().getKarafPlatform(),
                managedForm.getForm().getBody(),
                managedForm.getToolkit());

        managedForm.addPart(featuresManagementForm);
    }
}
