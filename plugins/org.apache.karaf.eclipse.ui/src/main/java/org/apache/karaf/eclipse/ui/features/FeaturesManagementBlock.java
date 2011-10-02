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
package org.apache.karaf.eclipse.ui.features;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;

import org.apache.karaf.eclipse.core.features.Feature;
import org.apache.karaf.eclipse.core.features.FeatureResolverImpl;
import org.apache.karaf.eclipse.core.features.Features;
import org.apache.karaf.eclipse.core.features.FeaturesRepository;
import org.apache.karaf.eclipse.ui.KarafUIPluginActivator;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class FeaturesManagementBlock {

    /**
     *
     * @author Stephen Evanchik (evanchsa@gmail.com)
     *
     */
    private static final class AvailableFeature {

        private final String feature;

        private final String featuresRepository;

        /**
         *
         * @param feature
         * @param featuresRepository
         */
        public AvailableFeature(final Feature feature, final FeaturesRepository featuresRepository) {
            if (feature == null) {
                throw new NullPointerException("feature");
            }

            this.feature = feature.getName();

            if (featuresRepository != null) {
                this.featuresRepository = featuresRepository.getName();
            } else {
                this.featuresRepository = "";
            }
        }

        /**
         *
         * @param feature
         * @param featuresRepository
         */
        public AvailableFeature(final String feature, final String featuresRepository) {
            this.feature = feature;
            this.featuresRepository = featuresRepository;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null) {
                return false;
            }

            if (!(obj instanceof AvailableFeature)) {
                return false;
            }

            final AvailableFeature other = (AvailableFeature) obj;
            if (feature == null) {
                if (other.feature != null) {
                    return false;
                }
            } else if (!feature.equals(other.feature)) {
                return false;
            }

            return true;
        }

        public String getFeatureName() {
            return feature;
        }

        public String getFeatureVersion() {
            return feature;
        }

        public String getRepositoryName() {
            return featuresRepository;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (feature == null ? 0 : feature.hashCode());
            return result;
        }

        @Override
        public String toString() {
            return feature;
        }
    }

    /**
     *
     * @author Stephen Evanchik (evanchsa@gmail.com)
     *
     */
    private final class AvailableFeaturesContentProvider implements IStructuredContentProvider {

        private final List<AvailableFeature> availableFeatures = Collections.synchronizedList(new ArrayList<AvailableFeature>());

        @Override
        public void dispose() {
        }

        @Override
        public Object[] getElements(final Object inputElement) {
            return availableFeatures.toArray();
        }

        @Override
        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
            if (newInput == null) {
                return;
            }

            availableFeatures.clear();

            @SuppressWarnings("unchecked")
            final List<FeaturesRepository> featuresRepositories = (List<FeaturesRepository>) newInput;
            for (final FeaturesRepository repository : featuresRepositories) {
                for (final Feature f : repository.getFeatures().getFeatures()) {
                    availableFeatures.add(new AvailableFeature(f, repository));
                }
            }
        }
    }

    /**
     *
     * @author Stephen Evanchik (evanchsa@gmail.com)
     *
     */
    private final class AvailableFeaturesLabelProvider extends LabelProvider implements ITableLabelProvider {

        @Override
        public Image getColumnImage(final Object element, final int columnIndex) {
            if (columnIndex == 0) {
                return KarafUIPluginActivator.getDefault().getImageRegistry()
                        .get(KarafUIPluginActivator.FEATURE_OBJ_IBM);
            } else {
                return null;
            }
        }

        @Override
        public String getColumnText(final Object element, final int columnIndex) {

            final AvailableFeature feature = (AvailableFeature) element;
            switch (columnIndex) {
            case 0:
                return feature.getFeatureName();
            case 1:
                return feature.getFeatureVersion();
            case 2:
                return feature.getRepositoryName();
            default:
                return null;
            }
        }
    }

    /**
     *
     * @author Stephen Evanchik (evanchsa@gmail.com)
     *
     */
    private final class BootFeaturesContentProvider implements IStructuredContentProvider {

        private List<String> bootFeaturesList;

        @Override
        public void dispose() {
        }

        @Override
        public Object[] getElements(final Object inputElement) {
            return bootFeaturesList.toArray();
        }

        @Override
        @SuppressWarnings("unchecked")
        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
            if (newInput != null) {
                bootFeaturesList = (List<String>) newInput;
            }
        }
    }

    /**
     *
     * @author Stephen Evanchik (evanchsa@gmail.com)
     *
     */
    private final class BootFeaturesLabelProvider extends LabelProvider {

        @Override
        public Image getImage(final Object element) {
            return KarafUIPluginActivator.getDefault().getImageRegistry().get(KarafUIPluginActivator.FEATURE_OBJ_IBM);
        }
    }

    /**
     *
     * @author Stephen Evanchik (evanchsa@gmail.com)
     *
     */
    public interface FeaturesManagementListener extends EventListener {
        public void handleEvent(Object event);
    }

    private CheckboxTableViewer availableFeaturesViewer;

    private Button bootFeatureOrderDecreaseButton;

    private Button bootFeatureOrderIncreaseButton;

    private Button bootFeatureRemoveButton;

    private final List<String> bootFeaturesList = Collections.synchronizedList(new ArrayList<String>());

    private TableViewer bootFeaturesViewer;

    private Composite control;

    private FeaturesManagementListener featuresManagementListener;

    private final List<FeaturesRepository> featuresRepositoryList = Collections.synchronizedList(new ArrayList<FeaturesRepository>());

    private FeatureResolverImpl featureResolver;

    private final Composite parent;

    /**
     * Constructor for a series of UI controls that allow the user to manipulate
     * the features to be installed upon Karaf startup
     *
     * @param parent
     *            the parent {@link Composite} to attach these controls to
     */
    public FeaturesManagementBlock(final Composite parent) {
        this.parent = parent;

        createControl();
    }

    /**
     * Adds the feature to the list of boot features
     *
     * @param bootFeature
     *            the name of the feature that should be installed upon Karaf
     *            startup
     */
    public void addBootFeature(final String bootFeature) {
        bootFeaturesList.add(bootFeature);
    }

    /**
     * Adds a {@link FeaturesRepository} that will have its features displayed
     * to the user
     *
     * @param featuresRepository
     *            a {@code FeaturesRepository} that will have its features
     *            displayed to the user
     */
    public void addFeaturesRepository(final FeaturesRepository featuresRepository) {
        featuresRepositoryList.add(featuresRepository);
    }

    /**
     * Determines if a feature is present in the boot Features list
     *
     * @param bootFeature
     *            the name of the feature
     * @return true if the feature is in the boot features list; false otherwise
     */
    public boolean containsBootFeature(final String bootFeature) {
        return bootFeaturesList.contains(bootFeature);
    }

    /**
     * Getter for a read-only list of boot feature names
     *
     * @return a read-only list of boot feature names
     */
    public List<String> getBootFeatures() {
        return Collections.unmodifiableList(bootFeaturesList);
    }

    /**
     * Getter for the top-level {@link Composite} that all other controls are
     * children
     *
     * @return the top-level {@code Composite} that all other controls are
     *         children
     */
    public Composite getControl() {
        return control;
    }

    /**
     * Determines if the feature management block is enabled
     *
     * @return true if this is enabled; false otherwise
     */
    public boolean getEnabled() {
        return availableFeaturesViewer.getControl().getEnabled();
    }

    /**
     * Refreshes the UI controls with the feature data
     */
    public void refresh() {
        final List<Object> checkedFeatures = new ArrayList<Object>();
        for (final String s : bootFeaturesList) {
            final Feature f = featureResolver.findFeature(s);

            if (f == null) {
                // TODO: Set some sort of warning
                continue;
            }

            final Features featuresContainer = (Features) f.getParent();
            final FeaturesRepository featuresRepository = featuresContainer.getParent();

            checkedFeatures.add(new AvailableFeature(f, featuresRepository));
        }

        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                if (!getControl().isDisposed()) {
                    bootFeaturesViewer.setInput(bootFeaturesList);
                    bootFeaturesViewer.refresh();

                    availableFeaturesViewer.setInput(featuresRepositoryList);
                    availableFeaturesViewer.setCheckedElements(checkedFeatures.toArray());
                }
            };
        });
    }

    /**
     * Setter to manipulate the control enabled state of the UI controls
     *
     * @param enabled
     *            true if the control should be enabled, false otherwise
     */
    public void setEnabled(final boolean enabledState) {
        availableFeaturesViewer.getControl().setEnabled(enabledState);
        bootFeaturesViewer.getControl().setEnabled(enabledState);
        bootFeatureOrderDecreaseButton.setEnabled(enabledState);
        bootFeatureOrderIncreaseButton.setEnabled(enabledState);
        bootFeatureRemoveButton.setEnabled(enabledState);
    }

    /**
     *
     * @param featuresManagementListener
     */
    public void setFeaturesManagementListener(final FeaturesManagementListener featuresManagementListener) {
        this.featuresManagementListener = featuresManagementListener;
    }

    /**
     *
     * @param featureResolver
     */
    public void setFeatureResolver(final FeatureResolverImpl featureResolver) {
        this.featureResolver = featureResolver;
    }

    /**
     *
     */
    private void createAvailableFeaturesControls(final Composite container) {
        GridData gd;
        availableFeaturesViewer = CheckboxTableViewer.newCheckList(container, SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
        final Table tree = availableFeaturesViewer.getTable();

        final TableColumn column1 = new TableColumn(tree, SWT.LEFT);
        column1.setText("Feature");
        column1.setWidth(250);

        final TableColumn column2 = new TableColumn(tree, SWT.LEFT);
        column2.setText("Version");
        column2.setWidth(150);

        final TableColumn column3 = new TableColumn(tree, SWT.LEFT);
        column3.setText("Repository");
        column3.setWidth(150);

        gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 200;
        tree.setLayoutData(gd);
        tree.setHeaderVisible(true);

        availableFeaturesViewer.setContentProvider(new AvailableFeaturesContentProvider());
        availableFeaturesViewer.setLabelProvider(new AvailableFeaturesLabelProvider());
        availableFeaturesViewer.setInput(null);
        availableFeaturesViewer.addCheckStateListener(new ICheckStateListener() {

            @Override
            public void checkStateChanged(final CheckStateChangedEvent event) {
                final AvailableFeature f = (AvailableFeature) event.getElement();
                if (event.getChecked()) {
                    if (!bootFeaturesList.contains(f.getFeatureName())) {
                        bootFeaturesList.add(f.getFeatureName());
                        bootFeaturesViewer.refresh();
                    }
                } else {
                    bootFeaturesList.remove(f.getFeatureName());
                    bootFeaturesViewer.refresh();
                }

                featuresManagementListener.handleEvent(event);
            }
        });
    }

    /**
     *
     */
    private void createBootFeatureManagementControls(final Composite container) {
        final Composite viewerComposite = new Composite(container, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = layout.marginWidth = 0;

        viewerComposite.setLayout(layout);

        GridData data = new GridData(GridData.FILL_BOTH);
        data.heightHint = 200;
        data.widthHint = 200;
        viewerComposite.setLayoutData(data);

        bootFeaturesViewer = new TableViewer(viewerComposite, SWT.BORDER | SWT.MULTI);
        bootFeaturesViewer.setLabelProvider(new BootFeaturesLabelProvider());
        bootFeaturesViewer.setContentProvider(new BootFeaturesContentProvider());
        bootFeaturesViewer.setInput(bootFeaturesList);
        bootFeaturesViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(final SelectionChangedEvent event) {
                handleBootFeatureSelectionChange();
            }
        });

        data = new GridData(GridData.FILL_BOTH);
        data.heightHint = 50;
        data.widthHint = 200;
        bootFeaturesViewer.getTable().setLayoutData(data);

        final Composite buttonComposite = new Composite(viewerComposite, SWT.RIGHT);
        layout = new GridLayout();
        layout.marginHeight = layout.marginWidth = 0;
        buttonComposite.setLayout(layout);

        data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.GRAB_VERTICAL);
        buttonComposite.setLayoutData(data);

        bootFeatureOrderIncreaseButton = new Button(buttonComposite, SWT.PUSH);
        bootFeatureOrderIncreaseButton.setText("Up");
        bootFeatureOrderIncreaseButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        bootFeatureOrderIncreaseButton.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(final Event event) {
                if (event.widget == bootFeatureOrderIncreaseButton) {
                    handleBootFeatureMove(-1);
                    featuresManagementListener.handleEvent(event);
                }
            }
        });

        bootFeatureRemoveButton = new Button(buttonComposite, SWT.PUSH);
        bootFeatureRemoveButton.setText("Remove");
        bootFeatureRemoveButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        bootFeatureRemoveButton.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(final Event event) {
                if (event.widget == bootFeatureRemoveButton) {
                    removeSelectedBootFeatures();
                    featuresManagementListener.handleEvent(event);
                }
            }
        });

        bootFeatureOrderDecreaseButton = new Button(buttonComposite, SWT.PUSH);
        bootFeatureOrderDecreaseButton.setText("Down");
        bootFeatureOrderDecreaseButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        bootFeatureOrderDecreaseButton.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(final Event event) {
                if (event.widget == bootFeatureOrderDecreaseButton) {
                    handleBootFeatureMove(1);
                    featuresManagementListener.handleEvent(event);
                }
            }
        });
    }

    /**
     *
     */
    private void createControl() {
        final GridLayout layout = new GridLayout(2, false);

        control = new Group(parent, SWT.NONE);
        control.setLayout(layout);
        control.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        final Label availableFeaturesLabel = new Label(control, SWT.NONE);
        availableFeaturesLabel.setText("Available Features");
        availableFeaturesLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        final Label bootFeatureRankLabel = new Label(control, SWT.NONE);
        bootFeatureRankLabel.setText("Boot Feature Rank");
        bootFeatureRankLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createAvailableFeaturesControls(control);
        createBootFeatureManagementControls(control);
    }

    /**
     *
     */
    private void handleBootFeatureSelectionChange() {
        final IStructuredSelection selection = (IStructuredSelection) bootFeaturesViewer.getSelection();

        final boolean notEmpty = !selection.isEmpty();

        @SuppressWarnings("unchecked")
        final Iterator<String> selectionElements = selection.iterator();

        boolean first = false;
        boolean last = false;
        final int lastBootFeature = bootFeaturesList.size() - 1;

        while (selectionElements.hasNext()) {
            final Object element = selectionElements.next();
            if (!first && bootFeaturesList.indexOf(element) == 0) {
                first = true;
            }

            if (!last && bootFeaturesList.indexOf(element) == lastBootFeature) {
                last = true;
            }
        }

        bootFeatureOrderIncreaseButton.setEnabled(notEmpty && !first);
        bootFeatureOrderDecreaseButton.setEnabled(notEmpty && !last);
    }

    /**
     *
     * @param direction
     */
    private void handleBootFeatureMove(final int direction) {
        if (direction != -1 && direction != 1) {
            throw new IllegalArgumentException("direction must be -1 or 1. Value: " + direction);
        }

        final IStructuredSelection selection = (IStructuredSelection) bootFeaturesViewer.getSelection();

        @SuppressWarnings("unchecked")
        final List<String> selectionList = selection.toList();
        final String[] movedBootFeatures = new String[bootFeaturesList.size()];

        for (final String config : selectionList) {
            final int i = bootFeaturesList.indexOf(config);
            movedBootFeatures[i + direction] = config;
        }

        bootFeaturesList.removeAll(selectionList);

        for (int j = 0; j < movedBootFeatures.length; j++) {
            final String config = movedBootFeatures[j];
            if (config != null) {
                bootFeaturesList.add(j, config);
            }
        }

        bootFeaturesViewer.refresh();
        handleBootFeatureSelectionChange();
    }

    /**
     *
     */
    private void removeSelectedBootFeatures() {
        final IStructuredSelection selection = (IStructuredSelection) bootFeaturesViewer.getSelection();

        @SuppressWarnings("unchecked")
        final Iterator<String> iter = selection.iterator();
        while (iter.hasNext()) {
            final String config = iter.next();
            bootFeaturesList.remove(config);
            availableFeaturesViewer.setChecked(new AvailableFeature(config, ""), false);
        }

        bootFeaturesViewer.refresh();
    }
}
