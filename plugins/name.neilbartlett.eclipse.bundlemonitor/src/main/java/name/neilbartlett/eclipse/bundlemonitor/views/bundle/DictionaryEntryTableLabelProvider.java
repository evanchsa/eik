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
package name.neilbartlett.eclipse.bundlemonitor.views.bundle;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class DictionaryEntryTableLabelProvider extends LabelProvider
		implements ITableLabelProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		DictionaryEntry entry = (DictionaryEntry) element;

		Object value;
		switch (columnIndex) {
		case 0:
			value = entry.getKey();
			break;
		case 1:
			value = entry.getValue();
			break;
		default:
			value = null;
		}

		return value != null ? value.toString() : "<null>";
	}

}
