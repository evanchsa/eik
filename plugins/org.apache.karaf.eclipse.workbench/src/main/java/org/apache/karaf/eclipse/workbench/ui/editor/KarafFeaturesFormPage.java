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

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.core.configuration.FeaturesSection;
import org.apache.karaf.eclipse.core.features.FeatureResolverImpl;
import org.apache.karaf.eclipse.core.features.FeaturesRepository;
import org.apache.karaf.eclipse.ui.KarafUIPluginActivator;
import org.apache.karaf.eclipse.ui.features.FeatureUtils;
import org.apache.karaf.eclipse.ui.features.FeaturesManagementBlock;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafFeaturesFormPage extends FormPage {

    private final class FeatureRepositoryContentProvider implements IStructuredContentProvider {

        private List<String> repositories;

        @Override
        public void dispose() {
        }

        @Override
        public Object[] getElements(final Object inputElement) {
            return repositories.toArray();
        }

        @SuppressWarnings("unchecked")
        @Override
        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
            if (newInput != null) {
                repositories = (List<String>) newInput;
            }
        }

    }

    public static final String ID = "org.apache.karaf.eclipse.editors.page.Features";

    private static final String TITLE = "Features";

    private Button addRepositoryButton;

    private final KarafPlatformEditorPart editor;

    private TableViewer featureRepositories;

    private FeaturesManagementBlock featuresManagementBlock;

    private final FeaturesSection featuresSection;

    private Text newRepositoryText;

    private Button removeRepositoryButton;

    /**
     *
     * @param editor
     */
    public KarafFeaturesFormPage(final KarafPlatformEditorPart editor) {
        super(editor, ID, TITLE);

        this.editor = editor;

        featuresSection = (FeaturesSection) editor.getKarafPlatform().getAdapter(FeaturesSection.class);
        if (featuresSection != null) {
            featuresSection.load();
        } else {
            KarafUIPluginActivator.getLogger().warn("Unable to load Feature Section for Karaf model: " + editor.getKarafPlatform());
        }
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

        managedForm.getForm().setText("Manage Features");

        final Composite left = managedForm.getToolkit().createComposite(managedForm.getForm().getBody());
        data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan = 2;
        left.setLayout(new GridLayout(1, true));
        left.setLayoutData(data);

        featuresManagementBlock = new FeaturesManagementBlock(left);

        adaptToFormControls(managedForm, featuresManagementBlock.getControl());

        initializeFeaturesManagementBlock();

        final Composite right = managedForm.getToolkit().createComposite(managedForm.getForm().getBody());
        data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan = 2;
        right.setLayout(new GridLayout(1, false));
        right.setLayoutData(data);

        createFeaturesRepositoryControls(right);

        adaptToFormControls(managedForm, right);
    }

    /**
     * Adapts all of the {@link Control}s in the {@link FeaturesManagementBlock}
     * to be styled according to the {@link FormToolkit}
     *
     * @param managedForm
     *            the {@link IManagedForm} that owns the {@code FormToolkit}
     */
    private void adaptToFormControls(final IManagedForm managedForm, final Control rootControl) {
        final Deque<Control> controls = new ArrayDeque<Control>();
        controls.add(rootControl);

        while (!controls.isEmpty()) {
            final Control control = controls.pop();
            managedForm.getToolkit().adapt(control, true, true);

            if (control instanceof Composite) {
                final Composite composite = (Composite) control;
                controls.addAll(Arrays.asList(composite.getChildren()));
            }
        }
    }

    private void createFeaturesRepositoryControls(final Composite parent) {
        featureRepositories = new TableViewer(parent, SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
        final Table tree = featureRepositories.getTable();

        final TableColumn column1 = new TableColumn(tree, SWT.LEFT);
        column1.setText("Repository");
        column1.setWidth(250);

        final TableColumn column2 = new TableColumn(tree, SWT.LEFT);
        column2.setText("Features");
        column2.setWidth(150);

        final GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 200;
        tree.setLayoutData(gd);
        tree.setHeaderVisible(true);

        featureRepositories.setContentProvider(new FeatureRepositoryContentProvider());
        featureRepositories.setLabelProvider(new LabelProvider());
        featureRepositories.setInput(featuresSection.getRepositoryList());

        final RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
        rowLayout.spacing = 8;
        rowLayout.marginHeight = 5;

        final Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(rowLayout);

        final Label label = new Label(container, SWT.NONE);
        label.setText("New Repository URL");

        newRepositoryText = new Text(container, SWT.BORDER);
        newRepositoryText.setLayoutData(new RowData(300, SWT.DEFAULT));

        addRepositoryButton = new Button(container, SWT.PUSH);
        addRepositoryButton.setText("Add Repository");

        removeRepositoryButton = new Button(container, SWT.PUSH);
        removeRepositoryButton.setText("Remove Repository");
    }

    /**
     *
     */
    private void initializeFeaturesManagementBlock() {
        try {
            final KarafPlatformModel karafPlatformModel = editor.getKarafPlatform();

            final List<String> bootFeaturesList = featuresSection.getBootFeatureNames();
            for (final String feature : bootFeaturesList) {
                if (!feature.isEmpty() && !featuresManagementBlock.containsBootFeature(feature)) {
                    featuresManagementBlock.addBootFeature(feature);
                }
            }

            final List<FeaturesRepository> featuresRepositories = FeatureUtils.getDefault().getFeatureRepository(karafPlatformModel);
            for (final FeaturesRepository featuresRepository : featuresRepositories) {
                featuresManagementBlock.addFeaturesRepository(featuresRepository);
            }

            Collections.sort(featuresRepositories, new Comparator<FeaturesRepository>() {
                @Override
                public int compare(final FeaturesRepository o1, final FeaturesRepository o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

            final FeatureResolverImpl fr = new FeatureResolverImpl(featuresRepositories);

            featuresManagementBlock.setFeatureResolver(fr);
            featuresManagementBlock.refresh();
        } catch (final CoreException e) {
            KarafUIPluginActivator.getLogger().warn("Unable to load Feature configuration data", e);
        }
    }
}
