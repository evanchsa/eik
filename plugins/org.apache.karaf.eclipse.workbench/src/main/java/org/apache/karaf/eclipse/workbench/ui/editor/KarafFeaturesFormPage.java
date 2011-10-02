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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.core.configuration.FeaturesSection;
import org.apache.karaf.eclipse.core.features.FeatureResolverImpl;
import org.apache.karaf.eclipse.core.features.FeaturesRepository;
import org.apache.karaf.eclipse.ui.features.FeatureUtils;
import org.apache.karaf.eclipse.ui.features.FeaturesManagementBlock;
import org.eclipse.core.runtime.CoreException;
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

    public static final String ID = "org.apache.karaf.eclipse.editors.page.Features";

    private static final String TITLE = "Features";

    private FeaturesManagementBlock featuresManagementBlock;

    private final KarafPlatformEditorPart editor;

    /**
     *
     * @param editor
     */
    public KarafFeaturesFormPage(final KarafPlatformEditorPart editor) {
        super(editor, ID, TITLE);

        this.editor = editor;
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
        data.horizontalSpan = 2;
        left.setLayout(new GridLayout(1, true));
        left.setLayoutData(data);

        featuresManagementBlock = new FeaturesManagementBlock(left);
        initializeFeaturesManagementBlock();

        final Composite right = managedForm.getToolkit().createComposite(managedForm.getForm().getBody());
        data = new GridData(GridData.FILL_BOTH);
        right.setLayout(new GridLayout(1, false));
        right.setLayoutData(data);
    }

    /**
     *
     */
    private void initializeFeaturesManagementBlock() {
        try {
            final KarafPlatformModel karafPlatformModel = editor.getKarafPlatform();

            final FeaturesSection featuresSection = (FeaturesSection) karafPlatformModel.getAdapter(FeaturesSection.class);
            featuresSection.load();

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

        }
    }
}
