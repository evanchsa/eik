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
package org.apache.karaf.eclipse.workbench.ui.editor;

import org.apache.karaf.eclipse.core.KarafPlatformDetails;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface KarafEditor {

    public KarafPlatformEditorInput getKarafEditorInput();

    public KarafPlatformDetails getPlatformDetails();
}
