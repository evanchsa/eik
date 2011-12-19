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
package org.apache.karaf.eclipse.ui;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.karaf.eclipse.core.KarafCorePluginUtils;
import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.core.KarafPlatformModelRegistry;
import org.apache.karaf.eclipse.core.features.FeatureResolverImpl;
import org.apache.karaf.eclipse.core.features.FeaturesRepository;
import org.apache.karaf.eclipse.ui.configuration.FeaturesSection;
import org.apache.karaf.eclipse.ui.features.FeatureUtils;
import org.apache.karaf.eclipse.ui.features.FeaturesManagementBlock;
import org.apache.karaf.eclipse.ui.features.FeaturesManagementBlock.FeaturesManagementListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafConfigurationTab extends AbstractLaunchConfigurationTab {

    public static final String ID = "org.apache.karaf.eclipse.ui.karafGeneralLaunchConfigurationTab"; //$NON-NLS-1$

    private Composite control;

    private Button enableFeaturesManagement;

    private FeaturesManagementBlock featuresManagementBlock;

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

        final FileInputStream fin = null;
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
        final String featuresString = KarafCorePluginUtils.join(featuresManagementBlock.getBootFeatures(), ",");
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
        final GridLayout layout = new GridLayout(1, false);

        final Composite group = new Composite(parent, SWT.NONE);
        group.setFont(font);
        group.setLayout(layout);
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        enableFeaturesManagement = new Button(group, SWT.CHECK);
        enableFeaturesManagement.setText("Enable Karaf Features management");
        enableFeaturesManagement.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(final Event event) {
                if (event.widget == enableFeaturesManagement) {
                    featuresManagementBlock.setEnabled(!featuresManagementBlock.getEnabled());
                    KarafConfigurationTab.this.updateLaunchConfigurationDialog();
                }
            }
        });
        enableFeaturesManagement.setLayoutData(new GridData(SWT.BEGINNING));

        featuresManagementBlock = new FeaturesManagementBlock(group);
        featuresManagementBlock.setFeaturesManagementListener(new FeaturesManagementListener() {

            @Override
            public void handleEvent(final Object event) {
                KarafConfigurationTab.this.updateLaunchConfigurationDialog();
            }
        });
    }

    /**
     * @throws CoreException
     */
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
}
