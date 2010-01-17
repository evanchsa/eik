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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class ConfigurationPidEditorInput implements IEditorInput {
	
	private final String pid;
	
	public ConfigurationPidEditorInput(String pid) {
		this.pid = pid;
	}

	public boolean exists() {
		return true;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return pid;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return "Configuration";
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	public String getPid() {
		return pid;
	}
	
	public boolean equals(Object obj) {
		if(obj.getClass() == this.getClass()) {
			return this.pid.equals(((ConfigurationPidEditorInput) obj).pid);
		}
		return false;
	}
	
	public int hashCode() {
		return pid.hashCode();
	}

}
