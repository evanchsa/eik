/**
 * Copyright (c) 2010 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package info.evanchik.eclipse.karaf.workbench.internal;

import org.eclipse.core.runtime.IAdapterFactory;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class RuntimeDataAdapterFactory implements IAdapterFactory {

    @Override
    public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
        return null;
    }

    @Override
    public Class<?>[] getAdapterList() {
        return null;
    }

}
