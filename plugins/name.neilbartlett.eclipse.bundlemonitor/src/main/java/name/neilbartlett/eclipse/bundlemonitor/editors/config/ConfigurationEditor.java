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
package name.neilbartlett.eclipse.bundlemonitor.editors.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import name.neilbartlett.eclipse.bundlemonitor.internal.Activator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class ConfigurationEditor extends EditorPart {

	public static final String EDITOR_ID = "name.neilbartlett.eclipse.bundlemonitor.configEditor";
	
	private Text editor;
	private boolean dirty;
	private String content;

	public void doSave(IProgressMonitor monitor) {
		ConfigurationAdmin cm = null;
		BundleContext bc = Activator.getDefault().getBundleContext();
		
		ServiceReference ref = bc.getServiceReference(ConfigurationAdmin.class.getName());
		if(ref != null) {
			cm = (ConfigurationAdmin) bc.getService(ref);
		}
		
		if(cm != null) {
			try {
				String pid = ((ConfigurationPidEditorInput) getEditorInput()).getPid();
				Configuration config = cm.getConfiguration(pid, null);
				
				Properties props = new Properties();
				ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes());
				props.load(inputStream);
				
				config.update(props);
				setDirty(false);
			} catch (IOException e) {
				MessageDialog.openError(getSite().getShell(), "Error", e.getLocalizedMessage());
				monitor.setCanceled(true);
			} finally {
				bc.ungetService(ref);
			}
		}
	}

	public void doSaveAs() {
		// Never called
	}

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		
		if(!(input instanceof ConfigurationPidEditorInput)) {
			throw new PartInitException("Invalid input type");
		}
		
		ConfigurationPidEditorInput pidInput = (ConfigurationPidEditorInput) input;
		setPartName(pidInput.getPid());

		ConfigurationAdmin cm = null;
		BundleContext bc = Activator.getDefault().getBundleContext();
		
		ServiceReference ref = bc.getServiceReference(ConfigurationAdmin.class.getName());
		if(ref != null) {
			cm = (ConfigurationAdmin) bc.getService(ref);
		}
		
		if(cm != null) {
			try {
				Configuration config = cm.getConfiguration(pidInput.getPid());
				Dictionary dict = config.getProperties();
				Properties props = new Properties();
				if(dict != null) {
					for(Enumeration keys = dict.keys(); keys.hasMoreElements(); ) {
						Object key = keys.nextElement();
						props.put(key, dict.get(key));
					}
				}

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				props.store(stream, null);
				content = stream.toString();
			} catch (IOException e) {
				throw new PartInitException("Error loading configuration", e);
			} finally {
				bc.ungetService(ref);
			}
		}
	}
	

	public boolean isDirty() {
		return dirty;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}
	
	protected void setDirty(boolean value) {
		dirty = value;
		firePropertyChange(PROP_DIRTY);
	}

	public void createPartControl(Composite parent) {
		editor = new Text(parent, SWT.MULTI);
		editor.setText(content == null ? "" : content);
		editor.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				content = editor.getText();
				setDirty(true);
			}
		});
	}

	public void setFocus() {
		editor.setFocus();
	}
}
