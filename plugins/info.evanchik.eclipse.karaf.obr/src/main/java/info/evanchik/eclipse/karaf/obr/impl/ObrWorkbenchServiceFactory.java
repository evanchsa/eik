/**
 * Copyright (c) 2011 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package info.evanchik.eclipse.karaf.obr.impl;

import info.evanchik.eclipse.karaf.ui.workbench.KarafWorkbenchService;
import info.evanchik.eclipse.karaf.ui.workbench.KarafWorkbenchServiceFactory;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public final class ObrWorkbenchServiceFactory implements KarafWorkbenchServiceFactory {

    @Override
    public KarafWorkbenchService getWorkbenchService() {
        return new ObrWorkbenchService();
    }
}
