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

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import name.neilbartlett.eclipse.bundlemonitor.editors.config.ConfigurationEditor;
import name.neilbartlett.eclipse.bundlemonitor.editors.config.ConfigurationPidEditorInput;
import name.neilbartlett.eclipse.bundlemonitor.internal.Activator;
import name.neilbartlett.eclipse.bundlemonitor.internal.SWTConcurrencyUtils;
import name.neilbartlett.eclipse.bundlemonitor.internal.ViewerUpdater;
import name.neilbartlett.eclipse.bundlemonitor.views.shared.FilteredViewPart;
import name.neilbartlett.eclipse.bundlemonitor.views.shared.PropertyEntry;

import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PartInitException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class ConfigView extends FilteredViewPart implements ServiceTrackerCustomizer, ConfigurationListener {

	private static final String NO_CONFIG_ADMIN = "Configuration Admin service unavailable";
	
	private final List configs = new LinkedList();
	
	private BundleContext context;
	private ServiceRegistration listenerRegistration;
	protected ServiceTracker tracker;
	private TreeViewer viewer;
	
	private ConfigPidFilter pidFilter;
	private Label status;
	
	private ViewerUpdater updater = new ViewerUpdater() {
		public void updateViewer(Viewer viewer) {
			configs.clear();
			ConfigurationAdmin service = (ConfigurationAdmin) tracker.getService();
			if(service == null) {
				status.setText(NO_CONFIG_ADMIN);
			} else {
				try {
					Configuration[] configArray = service.listConfigurations(new String("(" + Constants.SERVICE_PID + "=*)"));
					if(configArray != null) {
						for (int i = 0; i < configArray.length; i++) {
							configs.add(new ConfigWrapper(configArray[i]));
						}
					}
					status.setText(configs.size() + " configuration(s) available");
				} catch (IOException e) {
					status.setText(e.getMessage());
				} catch (InvalidSyntaxException e) {
					status.setText(e.getMessage());
				}
			} 
			viewer.refresh();
		}
	};

	
	public void createMainControl(Composite parent) {
		context = Activator.getDefault().getBundleContext();
		listenerRegistration = context.registerService(ConfigurationListener.class.getName(), this, null);
		
		tracker = new ServiceTracker(context, ConfigurationAdmin.class.getName(), this);
		
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		parent.setLayout(layout);
		
		Tree tree = new Tree(parent, SWT.BORDER | SWT.FULL_SELECTION);
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		
		TreeColumn col;
		col = new TreeColumn(tree, SWT.NONE);
		col.setWidth(400);
		
		col = new TreeColumn(tree, SWT.NONE);
		col.setWidth(200);
		
		status = new Label(parent, SWT.NONE);
		status.setText(NO_CONFIG_ADMIN);
		
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		status.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		viewer = new TreeViewer(tree);
		viewer.setContentProvider(new ConfigsContentProvider(tracker));
		viewer.setLabelProvider(new ConfigLabelProvider(tracker));
		//viewer.setComparer(new ConfigurationComparer());
		
		pidFilter = new ConfigPidFilter();
		viewer.addFilter(pidFilter);
		
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				ConfigWrapper wrapper;
				
				ISelection selection = event.getSelection();
				Object element = ((IStructuredSelection) selection).getFirstElement();
				if(element instanceof ConfigWrapper) {
					wrapper = (ConfigWrapper) element;
				} else if(element instanceof PropertyEntry) {
					wrapper = (ConfigWrapper) ((PropertyEntry) element).getOwner();
				} else {
					return;
				}
				
				ConfigurationPidEditorInput input = new ConfigurationPidEditorInput(wrapper.getPid());
				try {
					getSite().getPage().openEditor(input, ConfigurationEditor.EDITOR_ID);
				} catch (PartInitException e) {
					ErrorDialog.openError(getSite().getShell(), "Error", null, e.getStatus());
				}
			}
		});
		viewer.setInput(configs);
		
		tracker.open(true);
		
		getSite().setSelectionProvider(viewer);
	}
	
	protected void updatedFilter(String filterString) {
		pidFilter.setServiceName(filterString);
		viewer.refresh();
	}
	
	public void dispose() {
		listenerRegistration.unregister();
		tracker.close();
		super.dispose();
	}

	public Object addingService(final ServiceReference reference) {
		updateUI();
		return context.getService(reference);
	}

	public void modifiedService(ServiceReference reference, Object service) {
	}

	public void removedService(final ServiceReference reference, Object service) {
		context.ungetService(reference);
		updateUI();
	}
	
	public void configurationEvent(final ConfigurationEvent event) {
		SWTConcurrencyUtils.safeAsyncUpdate(viewer, new ViewerUpdater() {
			public void updateViewer(Viewer viewer) {
				AbstractTreeViewer treeViewer = (AbstractTreeViewer) viewer;
				boolean found = false;
				if(event.getType() == ConfigurationEvent.CM_DELETED) {
					for (Iterator iter = configs.iterator(); iter.hasNext();) {
						ConfigWrapper wrapper = (ConfigWrapper) iter.next();
						if(event.getPid().equals(wrapper.getPid())) {
							found = true;
							iter.remove();
							treeViewer.remove(wrapper);
						}
					}
				} else {
					for (Iterator iter = configs.iterator(); iter.hasNext();) {
						ConfigWrapper wrapper = (ConfigWrapper) iter.next();
						if(event.getPid().equals(wrapper.getPid())) {
							found = true;
							treeViewer.refresh(wrapper, true);
						}
					}
				}
				if(!found) {
					ConfigurationAdmin cm = (ConfigurationAdmin) tracker.getService();
					if(cm != null) {
						try {
							Configuration config = cm.getConfiguration(event.getPid());
							ConfigWrapper wrapper = new ConfigWrapper(config);
							configs.add(wrapper);
							treeViewer.add(configs, wrapper);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						updater.updateViewer(treeViewer);
					}
				}
			}
		});
	}
	
	private void updateUI() {
		SWTConcurrencyUtils.safeAsyncUpdate(viewer, updater);
	}

}
