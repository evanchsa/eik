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
package org.apache.karaf.eclipse.workbench.properties;

import org.apache.karaf.eclipse.core.KarafPlatformDetails;
import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafPlatformPropertyPage extends KarafPropertyPage {

    private Text installDir;

    private Text platformDescription;

    private Text platformName;

    private Text platformVersion;

	public KarafPlatformPropertyPage() {
		super();
	}

	@Override
    protected Control createContents(final Composite parent) {
	    final KarafPlatformModel karafPlatformModel = getKarafPlatformModel();

	    final KarafPlatformDetails karafPlatformDetails = karafPlatformModel.getPlatformDetails();

        final Composite client = new Composite(parent, SWT.NONE);

        final GridLayout layout = new GridLayout();
        layout.numColumns = 1;

        client.setLayout(layout);

        GridData data = new GridData();
        data.horizontalSpan = 2;

        // The installation directory selection controls
        Label l = new Label(client, SWT.NONE);
        l.setText("Installation directory");
        data = new GridData();
        data.horizontalSpan = 2;
        l.setLayoutData(data);

        Dialog.applyDialogFont(l);

        installDir = new Text(client, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        installDir.setLayoutData(data);
        installDir.setEnabled(false);
        installDir.setText(karafPlatformModel.getRootDirectory().toFile().getAbsolutePath());

        l = new Label(client, SWT.NONE);
        l.setText("Name");
        Dialog.applyDialogFont(l);

        platformName = new Text(client, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        platformName.setLayoutData(data);
        platformName.setEnabled(false);
        platformName.setText(karafPlatformDetails.getName());

        l = new Label(client, SWT.NONE);
        l.setText("Version");
        Dialog.applyDialogFont(l);

        platformVersion = new Text(client, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        platformVersion.setLayoutData(data);
        platformVersion.setEnabled(false);
        platformVersion.setText(karafPlatformDetails.getVersion());

        l = new Label(client, SWT.NONE);
        l.setText("Description");
        Dialog.applyDialogFont(l);

        platformDescription = new Text(client, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        platformDescription.setLayoutData(data);
        platformDescription.setEnabled(false);
        platformDescription.setText(karafPlatformDetails.getDescription());

		return client;
	}
}