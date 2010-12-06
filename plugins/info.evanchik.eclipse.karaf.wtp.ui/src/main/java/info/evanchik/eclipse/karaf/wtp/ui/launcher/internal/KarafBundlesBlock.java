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
package info.evanchik.eclipse.karaf.wtp.ui.launcher.internal;

import org.eclipse.pde.internal.ui.launcher.OSGiBundleBlock;
import org.eclipse.pde.ui.launcher.BundlesTab;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
@SuppressWarnings("restriction")
public class KarafBundlesBlock extends OSGiBundleBlock {

    public KarafBundlesBlock(BundlesTab tab) {
        super(tab);
    }

}
