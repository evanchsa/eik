/*
 * Copyright (c) 2008 Neil Bartlett
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Neil Bartlett - initial implementation
 */
package name.neilbartlett.eclipse.bundlemonitor.views.config;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class CreateConfigWizardPage extends WizardPage {
	
	private String pid;
	private boolean factory;
	
	private Text txtPid;
	
	public CreateConfigWizardPage() {
		super("Configuration");
		setTitle("Create or Edit Configuration");
	}

	public void createControl(Composite parent) {
		// Create controls
		Composite composite = new Composite(parent, SWT.NONE);
		
		new Label(composite, SWT.NONE).setText("Config PID: ");
		txtPid = new Text(composite, SWT.BORDER);
		
		new Label(composite, SWT.NONE); //Spacer
		final Button chkFactory = new Button(composite, SWT.CHECK);
		chkFactory.setText("Create Factory Configuration");
		
		// Initial values
		txtPid.setText((pid == null) ? "" : pid);
		chkFactory.setSelection(factory);
		
		// Listeners
		txtPid.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				pid = txtPid.getText();
				getContainer().updateButtons();
			}
		});
		chkFactory.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				factory = chkFactory.getSelection();
			}
		});
		
		// LAYOUT
		composite.setLayout(new GridLayout(2, false));
		txtPid.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		chkFactory.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false));
		
		setControl(composite);
	}
	
	public boolean isPageComplete() {
		return pid != null && pid.length() > 0;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public boolean isFactory() {
		return factory;
	}

	public void setFactory(boolean factory) {
		this.factory = factory;
	}
	

}
