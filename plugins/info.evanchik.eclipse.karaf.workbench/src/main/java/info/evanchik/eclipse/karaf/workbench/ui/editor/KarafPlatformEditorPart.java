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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafPlatformEditorPart extends EditorPart {

    public static final String ID = "info.evanchik.eclipse.karaf.ui.editors.KarafPlatformEditor";

    private ScrolledForm form;

    private FormToolkit formToolkit;

    protected Text installDir;

    protected Text platformDescription;

    protected Text platformName;

    protected Text platformVersion;

    private KarafPlatformModel karafPlatform;

    private KarafPlatformDetails platformDetails;

    @Override
    public void createPartControl(final Composite parent) {
        formToolkit = new FormToolkit(parent.getDisplay());
        formToolkit.setBorderStyle(SWT.BORDER);

        form = formToolkit.createScrolledForm(parent);

        final GridLayout layout = new GridLayout(2, false);

        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan = 2;

        form.getBody().setLayout(layout);
        form.getBody().setLayoutData(data);

        final Section section = formToolkit.createSection(
                form.getBody(),
                  Section.DESCRIPTION
                | Section.TITLE_BAR
                | Section.TWISTIE
                | Section.EXPANDED);

        section.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(final ExpansionEvent e) {
                form.reflow(true);
            }
        });

        section.setText("Karaf Platform Overview");
        section.setDescription("This is the description that goes below the title");

        final Composite sectionClient = formToolkit.createComposite(section);
        sectionClient.setLayout(new GridLayout(2, false));
        sectionClient.setLayoutData(data);

        section.setClient(sectionClient);

        Label l = formToolkit.createLabel(sectionClient, "Name");
        Dialog.applyDialogFont(l);

        platformName = formToolkit.createText(sectionClient, platformDetails.getName(), SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        platformName.setLayoutData(data);
        platformName.setEnabled(false);

        l = formToolkit.createLabel(sectionClient, "Version");
        Dialog.applyDialogFont(l);

        platformVersion = formToolkit.createText(sectionClient, platformDetails.getVersion(), SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        platformVersion.setLayoutData(data);
        platformVersion.setEnabled(false);

        l = formToolkit.createLabel(sectionClient, "Description");
        Dialog.applyDialogFont(l);

        platformDescription = formToolkit.createText(sectionClient, platformDetails.getDescription(), SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        platformDescription.setLayoutData(data);
        platformDescription.setEnabled(false);
    }

    @Override
    public void dispose() {
        formToolkit.dispose();
        super.dispose();
    }

    @Override
    public void doSave(final IProgressMonitor monitor) {
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);

        @SuppressWarnings("unchecked")
        final EditableObject<KarafPlatformModel> accountEditableObject =
            (EditableObject<KarafPlatformModel>) getEditorInput();

        karafPlatform = accountEditableObject.getObject();

        setPartName(karafPlatform.getRootDirectory().lastSegment());

        platformDetails = (KarafPlatformDetails) karafPlatform.getAdapter(KarafPlatformDetails.class);
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public void setFocus() {
        form.setFocus();
    }
}
