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

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;

/**
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafFeaturesFormPage extends FormPage {

    public static final String ID = "info.evanchik.eclipse.karaf.editors.page.Features";

    private static final String TITLE = "Features";

    /**
     *
     * @param editor
     */
    public KarafFeaturesFormPage(final KarafPlatformEditorPart editor) {
        super(editor, ID, TITLE);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    protected void createFormContent(final IManagedForm managedForm) {
        final GridLayout layout = new GridLayout(2, true);
        GridData data = new GridData(GridData.FILL_BOTH);

        managedForm.getForm().getBody().setLayout(layout);
        managedForm.getForm().getBody().setLayoutData(data);

        managedForm.getForm().setText("Manage Platform Features");

        final Composite left = managedForm.getToolkit().createComposite(managedForm.getForm().getBody());
        data = new GridData(GridData.FILL_BOTH);
        left.setLayout(new GridLayout(1, true));
        left.setLayoutData(data);

        final Composite right = managedForm.getToolkit().createComposite(managedForm.getForm().getBody());
        data = new GridData(GridData.FILL_BOTH);
        right.setLayout(new GridLayout(1, false));
        right.setLayoutData(data);
    }
}
