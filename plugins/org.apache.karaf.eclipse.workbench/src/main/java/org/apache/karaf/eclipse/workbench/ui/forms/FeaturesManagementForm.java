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
package org.apache.karaf.eclipse.workbench.ui.forms;

import java.util.ArrayDeque;
import java.util.ArrayList;
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
import org.apache.karaf.eclipse.ui.features.FeaturesManagementBlock.FeaturesManagementListener;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class FeaturesManagementForm extends SectionPart {

    private final class FeatureRepositoryContentProvider implements IStructuredContentProvider {

        @Override
        public void dispose() {
        }

        @Override
        public Object[] getElements(final Object inputElement) {
            final List<String> elements = new ArrayList<String>();
            elements.addAll(featureRepositories);
            elements.addAll(newFeaturesRepositories);
            return elements.toArray();
        }

        @Override
        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        }
    }

    private final class FeaturesResourceChangeListener implements IResourceChangeListener {
        @Override
        public void resourceChanged(final IResourceChangeEvent event) {
        }
    }

    private Button addRepositoryButton;

    private final List<String> featureRepositories = new ArrayList<String>();

    private TableViewer featureRepositoriesViewer;

    private FeaturesManagementBlock featuresManagementBlock;

    private final IResourceChangeListener featuresResourceChangeListener;

    private FeaturesSection featuresSection;

    private final FormToolkit formToolkit;

    private final List<String> initialBootFeatures = new ArrayList<String>();

    private final KarafPlatformModel karafPlatformModel;

    private final List<String> newFeaturesRepositories = new ArrayList<String>();

    private Text newRepositoryText;

    private Button removeRepositoryButton;

    private Composite sectionClient;

    /**
     * @param karafPlatformModel
     * @param parent
     * @param formToolkit
     */
    public FeaturesManagementForm(final KarafPlatformModel karafPlatformModel, final Composite parent, final FormToolkit formToolkit) {
        super(parent, formToolkit, Section.EXPANDED | Section.TITLE_BAR);

        this.karafPlatformModel = karafPlatformModel;
        this.formToolkit = formToolkit;
        this.featuresResourceChangeListener = new FeaturesResourceChangeListener();
    }

    @Override
    public void commit(final boolean onSave) {
        super.commit(onSave);

        if (onSave) {
            featuresSection.setBootFeatureNames(featuresManagementBlock.getBootFeatures());
            featuresSection.save();
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        ResourcesPlugin.getWorkspace().removeResourceChangeListener(featuresResourceChangeListener);
    }

    @Override
    public final void initialize(final IManagedForm managedForm) {
        super.initialize(managedForm);

        getSection().setLayout(new GridLayout(1, true));
        getSection().setLayoutData(new GridData(GridData.FILL_BOTH));

        sectionClient = formToolkit.createComposite(getSection());
        sectionClient.setLayout(new GridLayout(2, true));

        getSection().setText("Manage Features");
        getSection().setClient(sectionClient);

        GridData data = new GridData(GridData.FILL_BOTH);

        final Composite left = managedForm.getToolkit().createComposite(sectionClient);
        data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan = 2;
        left.setLayout(new GridLayout(1, true));
        left.setLayoutData(data);

        featuresManagementBlock = new FeaturesManagementBlock(left);

        adaptToFormControls(featuresManagementBlock.getControl());

        initializeFeaturesManagementBlock();

        final Composite right = managedForm.getToolkit().createComposite(sectionClient);
        data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan = 2;
        right.setLayout(new GridLayout(1, false));
        right.setLayoutData(data);

        createFeaturesRepositoryControls(right);

        adaptToFormControls(right);

        ResourcesPlugin.getWorkspace().addResourceChangeListener(featuresResourceChangeListener);
    }

    @Override
    public final void refresh() {
        super.refresh();

        featuresManagementBlock.refresh();
    }

    /**
     * Adapts all of the {@link Control}s in the {@link FeaturesManagementBlock}
     * to be styled according to the {@link FormToolkit}
     *
     * @param rootControl
     */
    private void adaptToFormControls(final Control rootControl) {
        final Deque<Control> controls = new ArrayDeque<Control>();
        controls.add(rootControl);

        while (!controls.isEmpty()) {
            final Control control = controls.pop();
            formToolkit.adapt(control, true, true);

            if (control instanceof Composite) {
                final Composite composite = (Composite) control;
                controls.addAll(Arrays.asList(composite.getChildren()));
            }
        }
    }

    private void createFeaturesRepositoryControls(final Composite parent) {
        featureRepositoriesViewer = new TableViewer(parent, SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
        final Table tree = featureRepositoriesViewer.getTable();

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

        featureRepositoriesViewer.setContentProvider(new FeatureRepositoryContentProvider());
        featureRepositoriesViewer.setLabelProvider(new LabelProvider());
        featureRepositoriesViewer.setInput(featuresSection.getRepositoryList());

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
        addRepositoryButton.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(final Event event) {
                if (event.widget == addRepositoryButton) {
                    newFeaturesRepositories.add(newRepositoryText.getText());
                    featureRepositoriesViewer.add(newRepositoryText.getText());

                    markDirty();
                }
            }
        });

        removeRepositoryButton = new Button(container, SWT.PUSH);
        removeRepositoryButton.setEnabled(false);
        removeRepositoryButton.setText("Remove Repository");
        removeRepositoryButton.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(final Event event) {
                if (event.widget == removeRepositoryButton) {

                    final IStructuredSelection structuredSelection =
                        (IStructuredSelection) featureRepositoriesViewer.getSelection();

                    final Object selection = structuredSelection.getFirstElement();

                    newFeaturesRepositories.remove(selection);
                    featureRepositoriesViewer.remove(selection);
                    featureRepositories.remove(selection);

                    markDirty();
                }
            }
        });

        featureRepositoriesViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(final SelectionChangedEvent event) {
                if (event.getSelection().isEmpty()) {
                    removeRepositoryButton.setEnabled(false);
                } else {
                    removeRepositoryButton.setEnabled(true);
                }
            }
        });
    }

    /**
     *
     */
    private void initializeFeaturesManagementBlock() {
        try {
            featuresSection = (FeaturesSection) karafPlatformModel.getAdapter(FeaturesSection.class);
            if (featuresSection != null) {
                featuresSection.load();
            } else {
                KarafUIPluginActivator.getLogger().warn("Unable to load Feature Section for Karaf model: " + karafPlatformModel);
            }

            featureRepositories.addAll(featuresSection.getRepositoryList());

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

            initialBootFeatures.addAll(featuresManagementBlock.getBootFeatures());

            final FeatureResolverImpl fr = new FeatureResolverImpl(featuresRepositories);

            featuresManagementBlock.setFeaturesManagementListener(new FeaturesManagementListener() {

                @Override
                public void handleEvent(final Object event) {

                    markDirty();
                }
            });

            featuresManagementBlock.setFeatureResolver(fr);
            featuresManagementBlock.refresh();
        } catch (final CoreException e) {
            KarafUIPluginActivator.getLogger().warn("Unable to load Feature configuration data", e);
        }
    }
}
