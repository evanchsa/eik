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


import name.neilbartlett.eclipse.bundlemonitor.views.shared.PropertyEntry;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.osgi.service.cm.Configuration;
import org.osgi.util.tracker.ServiceTracker;

public class ConfigLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	private static final String LABEL_NULL = "<null>";
	private final ServiceTracker cmTracker;
	
	public ConfigLabelProvider(ServiceTracker cmTracker) {
		this.cmTracker = cmTracker;
	}
	
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		String text = null;
		
		if(element instanceof ConfigWrapper) {
			ConfigWrapper wrapper = (ConfigWrapper) element;
			Configuration config = wrapper.getConfiguration(cmTracker);
			if(config != null ) {
				if(columnIndex == 0) {
					String pid = config.getPid();
					
					StringBuffer buffer = new StringBuffer();
					buffer.append(pid);
					text = buffer.toString();
				} else {
					text = config.getBundleLocation();
				}
			}
		} else if(element instanceof PropertyEntry) {
			PropertyEntry prop = (PropertyEntry) element;
			
			if(columnIndex == 0) {
				text = prop.getKey();
			} else if(columnIndex == 1) {
				Object value = prop.getValue();
				if(value == null) {
					text = LABEL_NULL;
				} else if(value instanceof Object[]) {
					text = arrayToString((Object[]) value);
				} else {
					text = value.toString();
				}
			}
		}
		
		return text;
	}
	
	protected static String arrayToString(Object[] array) {
		StringBuffer buffer = new StringBuffer();
		
		for(int i=0; i<array.length; i++) {
			if(i > 0) buffer.append(',');
			buffer.append(array[i] == null ? LABEL_NULL : array[i].toString());
		}
		
		return buffer.toString();
	}

}
