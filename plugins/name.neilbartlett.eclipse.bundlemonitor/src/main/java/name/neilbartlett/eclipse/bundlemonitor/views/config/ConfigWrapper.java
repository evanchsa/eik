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
import java.lang.ref.WeakReference;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.tracker.ServiceTracker;

public class ConfigWrapper {
	
	private final String pid;
	private WeakReference configRef;
	
	public ConfigWrapper(Configuration config) {
		pid = config.getPid();
		configRef = new WeakReference(config);
	}
	
	public String getPid() {
		return pid;
	}
	
	public Configuration getConfiguration(ServiceTracker cmTracker) {
		Configuration config = (Configuration) configRef.get();
		if(config == null) {
			ConfigurationAdmin cm = (ConfigurationAdmin) cmTracker.getService();
			if(cm != null) {
				try {
					config = cm.getConfiguration(pid);
					configRef = new WeakReference(config);
				} catch (IOException e) {
				}
			}
		}
		
		return config;
	}

	public int hashCode() {
		return pid.hashCode();
	}
	
	public boolean equals(Object obj) {
		return (obj.getClass() == ConfigWrapper.class)
		&& pid.equals(((ConfigWrapper) obj).pid);
	}
}
