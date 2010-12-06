/**
 * Copyright (c) 2009 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package info.evanchik.eclipse.karaf.hooks;

import org.eclipse.osgi.baseadaptor.HookConfigurator;
import org.eclipse.osgi.baseadaptor.HookRegistry;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class KarafHookConfigurator implements HookConfigurator {

    /**
     * Registers the hooks that provide low level support to running Karaf in
     * Eclipse PDE.
     *
     * @see KarafAdapterHook
     * @see KarafClassloadingHook
     */
    public void addHooks(HookRegistry hookRegistry) {
        hookRegistry.addAdaptorHook(new KarafAdapterHook());
        hookRegistry.addClassLoadingHook(new KarafClassloadingHook());
    }
}
