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
package info.evanchik.eclipse.karaf.ui;

import info.evanchik.eclipse.karaf.core.KarafCorePluginUtils;
import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.KarafPlatformModelRegistry;
import info.evanchik.eclipse.karaf.core.configuration.FeaturesSection;
import info.evanchik.eclipse.karaf.core.features.Bundle;
import info.evanchik.eclipse.karaf.core.features.Feature;
import info.evanchik.eclipse.karaf.core.features.FeatureResolverImpl;
import info.evanchik.eclipse.karaf.core.features.Features;
import info.evanchik.eclipse.karaf.ui.features.FeaturesContentProvider;
import info.evanchik.eclipse.karaf.ui.features.FeaturesLabelProvider;
import info.evanchik.eclipse.karaf.ui.features.FeaturesResolverJob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafConfigurationTab extends AbstractLaunchConfigurationTab {

    public static final String ID = "info.evanchik.eclipse.karaf.ui.karafGeneralLaunchConfigurationTab"; //$NON-NLS-1$

    private Composite control;

    private Button enableFeaturesManagement;

    private FeaturesResolverJob featuresResolverJob;

    private FeaturesSection featuresSection;

    private ContainerCheckedTreeViewer installedFeatures;

    private KarafPlatformModel karafPlatformModel;

    private Button localConsole;

    private Button remoteConsole;

    private Text remoteConsoleUsername;

    private Text remoteConsolePassword;

    private final List<String> selectedFeatures = Collections.synchronizedList(new ArrayList<String>());

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
        try {
            localConsole.setSelection(
                    configuration.getAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_START_LOCAL_CONSOLE, true));
            remoteConsole.setSelection(
                    configuration.getAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_START_REMOTE_CONSOLE, false));
            enableFeaturesManagement.setSelection(
                    configuration.getAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_FEATURES_MANAGEMENT, true));

            initializeKarafPlatformModel();

            final String featuresString = configuration.getAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_BOOT_FEATURES, "");
            final String[] features = featuresString.split(",");
            selectedFeatures.clear();
            selectedFeatures.addAll(Arrays.asList(features));

        } catch (final CoreException e) {
            KarafUIPluginActivator.getLogger().error("Unable to initialize launch configuration tab", e);
            return;
        }

        featuresResolverJob.addJobChangeListener(new JobChangeAdapter() {
           @Override
            public void done(final IJobChangeEvent event) {
               if (event.getResult().isOK()) {
                   featuresResolverJob.removeJobChangeListener(this);

                    try {
                        final String storedBootFeatures =
                            configuration.getAttribute(
                                KarafLaunchConfigurationConstants.KARAF_LAUNCH_BOOT_FEATURES,
                                "");

                        final FeatureResolverImpl fr = new FeatureResolverImpl(featuresResolverJob.getFeaturesRepositories());

                        final List<Object> checkedFeatures = new ArrayList<Object>();
                        for (final String s : storedBootFeatures.split(",")) {
                            final Object[] path = fr.getFeaturePath(s);
                            Collections.addAll(checkedFeatures, path);
                        }

                        Display.getDefault().syncExec(new Runnable() {
                            @Override
                            public void run() {
                                if (!getControl().isDisposed()) {
                                    installedFeatures.setInput(featuresResolverJob.getFeaturesRepositories());
                                    installedFeatures.setCheckedElements(checkedFeatures.toArray());
                                }
                            };
                        });

                    } catch (final CoreException e) {
                        KarafUIPluginActivator.getLogger().error("Unable to set Karaf Boot Features", e);
                    }
               }
            }
        });

        if (featuresResolverJob.getState() == Job.NONE) {
            featuresResolverJob.schedule();
        }
    }

    @Override
    public void performApply(final ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_START_LOCAL_CONSOLE, localConsole.getSelection());
        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_START_REMOTE_CONSOLE, remoteConsole.getSelection());
        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_REMOTE_CONSOLE_PASSWORD, remoteConsolePassword.getText());
        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_REMOTE_CONSOLE_USERNAME, remoteConsoleUsername.getText());
        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_FEATURES_MANAGEMENT, enableFeaturesManagement.getSelection());
        final String featuresString = KarafCorePluginUtils.join(selectedFeatures, ",");
        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_BOOT_FEATURES, featuresString);
    }

    @Override
    public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {

        try {
            initializeKarafPlatformModel();
        } catch (final CoreException e) {
            KarafUIPluginActivator.getLogger().error("Unable to initialize Karaf Platform model", e);
        }

        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_START_LOCAL_CONSOLE, true);
        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_START_REMOTE_CONSOLE, false);
        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_LAUNCH_FEATURES_MANAGEMENT, true);

        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_REMOTE_CONSOLE_USERNAME, "karaf");
        configuration.setAttribute(KarafLaunchConfigurationConstants.KARAF_REMOTE_CONSOLE_PASSWORD, "karaf");

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
        final Font font = parent.getFont();
        final Composite comp = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, true);
        comp.setLayout(layout);
        comp.setFont(font);

        final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        comp.setLayoutData(gd);

        final Group group = new Group(comp, SWT.NONE);
        group.setFont(font);
        layout = new GridLayout(1, false);
        group.setLayout(layout);
        group.setLayoutData(new GridData(GridData.FILL_BOTH));

        group.setText("Console");

        localConsole = createCheckButton(group, "Local console");
        localConsole.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                scheduleUpdateJob();
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
                scheduleUpdateJob();
            }
        });

        final KeyListener keyListener = new KeyListener() {

            @Override
            public void keyReleased(final KeyEvent e) {
                scheduleUpdateJob();
            }

            @Override
            public void keyPressed(final KeyEvent e) {
                scheduleUpdateJob();
            }
        };

        remoteConsole = createCheckButton(group, "Remote console");

        Label l = new Label(group, SWT.NONE);
        l.setText("Username");
        remoteConsoleUsername = new Text(group, SWT.BORDER);
        remoteConsoleUsername.setText("karaf");
        remoteConsoleUsername.setLayoutData(new GridData(175, 20));
        remoteConsoleUsername.addKeyListener(keyListener);

        l = new Label(group, SWT.NONE);
        l.setText("Password");
        remoteConsolePassword = new Text(group, SWT.BORDER|SWT.PASSWORD);
        remoteConsolePassword.setText("karaf");
        remoteConsolePassword.setLayoutData(new GridData(175, 20));
        remoteConsolePassword.addKeyListener(keyListener);

        remoteConsole.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                updateRemoteConsoleControls();
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
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
        final Composite comp = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, true);
        comp.setLayout(layout);
        comp.setFont(font);

        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        comp.setLayoutData(gd);

        final Group group = new Group(comp, SWT.NONE);
        group.setFont(font);
        layout = new GridLayout(1, false);
        group.setLayout(layout);
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        group.setText("Features");

        enableFeaturesManagement = new Button(group, SWT.CHECK);
        enableFeaturesManagement.setText("Enable Karaf Features management");
        enableFeaturesManagement.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(final Event event) {
                if (event.widget == enableFeaturesManagement) {
                    final boolean enabledState = installedFeatures.getControl().getEnabled();
                    installedFeatures.getControl().setEnabled(!enabledState);
                    KarafConfigurationTab.this.updateLaunchConfigurationDialog();
                }
            }
        });

        installedFeatures = new ContainerCheckedTreeViewer(group, SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
        final Tree tree = installedFeatures.getTree();

        final TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
        column1.setText("Feature");
        column1.setWidth(300);

        final TreeColumn column2 = new TreeColumn(tree, SWT.LEFT);
        column2.setText("Version");
        column2.setWidth(175);

        gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 300;
        tree.setLayoutData(gd);
        tree.setHeaderVisible(true);

        installedFeatures.setContentProvider(new FeaturesContentProvider());
        installedFeatures.setLabelProvider(new FeaturesLabelProvider());
        installedFeatures.setInput(null);
        installedFeatures.addCheckStateListener(new ICheckStateListener() {

            @Override
            public void checkStateChanged(final CheckStateChangedEvent event) {
                final Object[] elements = installedFeatures.getCheckedElements();

                for (final Object e : elements) {
                    if (e instanceof Feature && ((Feature)e).getParent() instanceof Features) {
                        final Feature f = (Feature) e;
                        if (!selectedFeatures.contains(f.getName())) {
                            selectedFeatures.add(f.getName());
                        }
                    } else if (e instanceof Bundle) {
                        final Bundle b = (Bundle) e;
                        if (b.getParent() instanceof Feature) {
                            final Feature f = (Feature) b.getParent();
                            if (!selectedFeatures.contains(f)) {
                                selectedFeatures.add(f.getName());
                            }
                        } else {
                            // Intentionally empty
                        }
                    } else {
                        // Intentionally empty
                    }
                }

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

        featuresSection = (FeaturesSection) karafPlatformModel.getAdapter(FeaturesSection.class);
        if (featuresSection == null) {
            throw new CoreException(new Status(IStatus.ERROR, KarafUIPluginActivator.PLUGIN_ID, "Unable to load Features for Karaf Platform Model: " + karafPlatformModel.getRootDirectory()));
        }

        featuresResolverJob = new FeaturesResolverJob(getName(), featuresSection);
        featuresSection.load();
    }

}
