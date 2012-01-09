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
package org.apache.karaf.eik.ui;

import org.apache.karaf.eik.core.KarafCorePluginUtils;
import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.core.KarafPlatformModelRegistry;
import org.apache.karaf.eik.core.configuration.FeaturesSection;
import org.apache.karaf.eik.core.features.Feature;
import org.apache.karaf.eik.core.features.FeatureResolverImpl;
import org.apache.karaf.eik.core.features.Features;
import org.apache.karaf.eik.core.features.FeaturesRepository;
import org.apache.karaf.eik.core.features.XmlFeaturesRepository;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class KarafConfigurationTab extends AbstractLaunchConfigurationTab {

    private static final class AvailableFeature {

        private final String feature;

        private final String featuresRepository;

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

    private final class AvailableFeaturesContentProvider implements IStructuredContentProvider {

        private final List<AvailableFeature> availableFeatures =
            Collections.synchronizedList(new ArrayList<AvailableFeature>());

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
            for (final FeaturesRepository  repository : featuresRepositories) {
                for (final Feature f : repository.getFeatures().getFeatures()) {
                    availableFeatures.add(new AvailableFeature(f, repository));
                }
            }
        }
    }

    private final class AvailableFeaturesLabelProvider extends LabelProvider implements ITableLabelProvider {

        @Override
        public Image getColumnImage(final Object element, final int columnIndex) {
            if (columnIndex == 0) {
                return KarafUIPluginActivator.getDefault().getImageRegistry().get(KarafUIPluginActivator.FEATURE_OBJ_IBM);
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

    private final class BootFeaturesContentProvider implements IStructuredContentProvider {

        @Override
        public void dispose() {
        }

        @Override
        public Object[] getElements(final Object inputElement) {
            return bootFeaturesList.toArray();
        }

        @Override
        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        }
    }

    private final class BootFeaturesLabelProvider extends LabelProvider {

        @Override
        public Image getImage(final Object element) {
            return KarafUIPluginActivator.getDefault().getImageRegistry().get(KarafUIPluginActivator.FEATURE_OBJ_IBM);
        }
    }

    public static final String ID = "org.apache.karaf.eik.ui.karafGeneralLaunchConfigurationTab";

    private CheckboxTableViewer availableFeaturesViewer;

    private Button bootFeatureOrderDecreaseButton;

    private Button bootFeatureOrderIncreaseButton;

    private Button bootFeatureRemoveButton;

    private final List<String> bootFeaturesList = Collections.synchronizedList(new ArrayList<String>());

    private TableViewer bootFeaturesViewer;

    private Composite control;

    private Button enableFeaturesManagement;

    private KarafPlatformModel karafPlatformModel;

    private IKarafProject karafProject;

    private Button localConsole;

    private Button remoteConsole;

    private Text remoteConsolePassword;

    private Text remoteConsoleUsername;

    @Override
    public void createControl(final Composite parent) {
        control = new Composite(parent, SWT.NONE);

        final GridLayout layout = new GridLayout();
        control.setLayout(layout);

        final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        control.setLayoutData(gd);

        createConsoleBlock(control);
        createFeaturesBlock(control);

        setControl(control);
        Dialog.applyDialogFont(control);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(control, ID);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Image getImage() {
        return KarafUIPluginActivator.getDefault().getImageRegistry().get(KarafUIPluginActivator.LOGO_16X16_IMG);
    }

    @Override
    public String getName() {
        return "Karaf";
    }

    @Override
    public void initializeFrom(final ILaunchConfiguration configuration) {

        FileInputStream fin = null;
        try {
            localConsole.setSelection(
                    configuration.getAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_START_LOCAL_CONSOLE, true));
            remoteConsole.setSelection(
                    configuration.getAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_START_REMOTE_CONSOLE, false));
            enableFeaturesManagement.setSelection(
                    configuration.getAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_FEATURES_MANAGEMENT, true));

            initializeKarafPlatformModel();

            final String storedBootFeatures = configuration.getAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_BOOT_FEATURES, "");
            final String[] features = storedBootFeatures.split(",");
            for (final String feature : features) {
                // TODO: Not really efficient
                if (!feature.isEmpty() && !bootFeaturesList.contains(feature)) {
                    bootFeaturesList.add(feature);
                }
            }

            // TODO: This should be factored out and it should be easy to get a List of FeaturesRepository
            final IFolder featuresFolder = karafProject.getFolder("features");
            if (featuresFolder.exists()) {
                final List<FeaturesRepository> featuresRepositories = new ArrayList<FeaturesRepository>();
                final IResource[] resources = featuresFolder.members();
                for (final IResource resource : resources) {
                    if (resource.getFileExtension().equalsIgnoreCase("xml")) {
                        fin = new FileInputStream(resource.getRawLocation().toFile());
                        final XmlFeaturesRepository xmlFeatureRepository = new XmlFeaturesRepository(resource.getName(), fin);
                        featuresRepositories.add(xmlFeatureRepository);
                        fin.close();
                    }
                }

                Collections.sort(featuresRepositories, new Comparator<FeaturesRepository>() {
                    @Override
                    public int compare(final FeaturesRepository o1, final FeaturesRepository o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });

                final FeatureResolverImpl fr = new FeatureResolverImpl(featuresRepositories);

                final List<Object> checkedFeatures = new ArrayList<Object>();
                for (final String s : bootFeaturesList) {
                    final Feature f = fr.findFeature(s);

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

                            availableFeaturesViewer.setInput(featuresRepositories);
                            availableFeaturesViewer.setCheckedElements(checkedFeatures.toArray());
                        }
                    };
                });
            }
        } catch (final IOException e) {
            KarafUIPluginActivator.getLogger().error("Uable to load file", e);
        } catch (final CoreException e) {
            KarafUIPluginActivator.getLogger().error("Unable to initialize launch configuration tab", e);
            return;
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (final IOException e) {
                    // Nothing to do here
                }
            }
        }
    }

    @Override
    public void performApply(final ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_START_LOCAL_CONSOLE, localConsole.getSelection());
        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_START_REMOTE_CONSOLE, remoteConsole.getSelection());
        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_REMOTE_CONSOLE_PASSWORD, remoteConsolePassword.getText());
        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_REMOTE_CONSOLE_USERNAME, remoteConsoleUsername.getText());
        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_FEATURES_MANAGEMENT, enableFeaturesManagement.getSelection());
        final String featuresString = KarafCorePluginUtils.join(bootFeaturesList, ",");
        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_BOOT_FEATURES, featuresString);
    }

    @Override
    public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {

        try {
            initializeKarafPlatformModel();
        } catch (final CoreException e) {
            KarafUIPluginActivator.getLogger().error("Unable to initialize Karaf Platform model", e);
            return;
        }

        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_START_LOCAL_CONSOLE, true);
        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_START_REMOTE_CONSOLE, false);
        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_FEATURES_MANAGEMENT, true);

        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_REMOTE_CONSOLE_USERNAME, "karaf");
        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_REMOTE_CONSOLE_PASSWORD, "karaf");

        final FeaturesSection featuresSection = (FeaturesSection) karafPlatformModel.getAdapter(FeaturesSection.class);
        featuresSection.load();

        final List<String> featuresList = featuresSection.getBootFeatureNames();

        final String features = KarafCorePluginUtils.join(featuresList, ",");
        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_BOOT_FEATURES, features);
    }

    private void createAvailableFeaturesControls(final Group group) {
        GridData gd;
        availableFeaturesViewer = CheckboxTableViewer.newCheckList(group, SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
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
                KarafConfigurationTab.this.updateLaunchConfigurationDialog();
            }
        });
    }

    private void createBootFeatureManagementControls(final Group group) {
        // Boot feature management
        final Composite viewerComposite = new Composite(group, SWT.NONE);
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
                    KarafConfigurationTab.this.updateLaunchConfigurationDialog();
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
                    KarafConfigurationTab.this.updateLaunchConfigurationDialog();
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
                    KarafConfigurationTab.this.updateLaunchConfigurationDialog();
                }
            }
        });
    }

    /**
     * Creates the necessary UI elements that control what kind of console to
     * use (i.e. remote, local or both)
     *
     * @param parent
     */
    private void createConsoleBlock(final Composite parent) {
        final Composite comp = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, true);
        comp.setLayout(layout);
        comp.setFont(parent.getFont());

        final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        comp.setLayoutData(gd);

        final Group group = new Group(comp, SWT.NONE);
        layout = new GridLayout(1, false);
        group.setLayout(layout);
        group.setLayoutData(new GridData(GridData.FILL_BOTH));

        group.setText("Console");

        localConsole = createCheckButton(group, "Local console");
        localConsole.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
                scheduleUpdateJob();
            }

            @Override
            public void widgetSelected(final SelectionEvent e) {
                scheduleUpdateJob();
            }
        });

        final KeyListener keyListener = new KeyListener() {

            @Override
            public void keyPressed(final KeyEvent e) {
                scheduleUpdateJob();
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                scheduleUpdateJob();
            }
        };

        remoteConsole = createCheckButton(group, "Remote console");

        final Composite credentialsBlock = new Composite(group, SWT.NONE);
        credentialsBlock.setLayout(new GridLayout(2, true));

        Label l = new Label(credentialsBlock, SWT.NONE);
        l.setText("Username");
        remoteConsoleUsername = new Text(credentialsBlock, SWT.BORDER);
        remoteConsoleUsername.setText("karaf");
        remoteConsoleUsername.setLayoutData(new GridData(175, 20));
        remoteConsoleUsername.addKeyListener(keyListener);

        l = new Label(credentialsBlock, SWT.NONE);
        l.setText("Password");
        remoteConsolePassword = new Text(credentialsBlock, SWT.BORDER|SWT.PASSWORD);
        remoteConsolePassword.setText("karaf");
        remoteConsolePassword.setLayoutData(new GridData(175, 20));
        remoteConsolePassword.addKeyListener(keyListener);

        remoteConsole.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
                updateRemoteConsoleControls();
            }

            @Override
            public void widgetSelected(final SelectionEvent e) {
                updateRemoteConsoleControls();
            }

            private void updateRemoteConsoleControls() {
                final boolean enable = remoteConsole.getSelection();
                remoteConsoleUsername.setEnabled(enable);
                remoteConsolePassword.setEnabled(enable);

                scheduleUpdateJob();
            }
        });
    }

    /**
     * Creates the necessary UI controls to manipulate the features system.
     *
     * @param parent
     */
    private void createFeaturesBlock(final Composite parent) {

        final Font font = parent.getFont();
        final GridLayout layout = new GridLayout(2, false);

        final Group group = new Group(parent, SWT.NONE);
        group.setFont(font);
        group.setLayout(layout);
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        group.setText("Features");

        enableFeaturesManagement = new Button(group, SWT.CHECK);
        enableFeaturesManagement.setText("Enable Karaf Features management");
        enableFeaturesManagement.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(final Event event) {
                if (event.widget == enableFeaturesManagement) {
                    final boolean enabledState = availableFeaturesViewer.getControl().getEnabled();
                    availableFeaturesViewer.getControl().setEnabled(!enabledState);
                    bootFeaturesViewer.getControl().setEnabled(!enabledState);
                    bootFeatureOrderDecreaseButton.setEnabled(!enabledState);
                    bootFeatureOrderIncreaseButton.setEnabled(!enabledState);
                    bootFeatureRemoveButton.setEnabled(!enabledState);

                    KarafConfigurationTab.this.updateLaunchConfigurationDialog();
                }
            }
        });
        enableFeaturesManagement.setLayoutData(new GridData(SWT.BEGINNING));

        final Label bootFeatureRankLabel = new Label(group, SWT.NONE);
        bootFeatureRankLabel.setText("Boot Feature Rank");
        bootFeatureRankLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createAvailableFeaturesControls(group);
        createBootFeatureManagementControls(group);
    }

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
            if(!first && bootFeaturesList.indexOf(element) == 0) {
                first = true;
            }

            if (!last && bootFeaturesList.indexOf(element) == lastBootFeature) {
                last = true;
            }
        }

        bootFeatureOrderIncreaseButton.setEnabled(notEmpty && !first);
        bootFeatureOrderDecreaseButton.setEnabled(notEmpty && !last);
    }

    private void handleBootFeatureMove(final int direction) {
        if (direction != -1 || direction != 1) {
            throw new IllegalArgumentException("direction must be -1 or 1. Value: " + direction);
        }

        final IStructuredSelection selection = (IStructuredSelection)bootFeaturesViewer.getSelection();

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

    private void initializeKarafPlatformModel() throws CoreException {
        karafPlatformModel = KarafPlatformModelRegistry.findActivePlatformModel();
        if (karafPlatformModel == null) {
            throw new CoreException(new Status(IStatus.ERROR, KarafUIPluginActivator.PLUGIN_ID, "Unable to locate active Karaf Platform Model"));
        }

        karafProject = (IKarafProject) Platform.getAdapterManager().getAdapter(karafPlatformModel, IKarafProject.class);
        if (karafProject== null) {
            throw new CoreException(new Status(IStatus.ERROR, KarafUIPluginActivator.PLUGIN_ID, "Unable to locate Karaf Project in Workspace"));
        }
    }

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
