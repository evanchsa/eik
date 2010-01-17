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

import java.util.Dictionary;
import java.util.Enumeration;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class DictionaryContentProvider implements IStructuredContentProvider {

    public void dispose() {
    }

    public Object[] getElements(Object inputElement) {
        final Dictionary<?, ?> dict = (Dictionary<?, ?>) inputElement;
        final int size = dict.size();

        final Object[] result = new Object[size];
        int i = 0;
        for (Enumeration<?> e = dict.keys(); e.hasMoreElements(); i++) {
            final Object key = e.nextElement();
            final Object value = dict.get(key);
            result[i] = new DictionaryEntry(key, value);
        }

        return result;
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
}
