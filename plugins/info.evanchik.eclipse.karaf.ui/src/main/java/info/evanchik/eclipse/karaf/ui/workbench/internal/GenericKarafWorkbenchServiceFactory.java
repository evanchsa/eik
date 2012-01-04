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
package info.evanchik.eclipse.karaf.ui.workbench.internal;

import info.evanchik.eclipse.karaf.ui.workbench.KarafWorkbenchService;
import info.evanchik.eclipse.karaf.ui.workbench.KarafWorkbenchServiceFactory;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class GenericKarafWorkbenchServiceFactory implements KarafWorkbenchServiceFactory {

    private static final GenericKarafWorkbenchService workbenchService =
        new GenericKarafWorkbenchService();

    public KarafWorkbenchService getWorkbenchService() {
        return workbenchService;
    }
}
