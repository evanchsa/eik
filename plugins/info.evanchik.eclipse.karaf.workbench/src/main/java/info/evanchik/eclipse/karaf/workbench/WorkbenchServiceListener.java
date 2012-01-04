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
package info.evanchik.eclipse.karaf.workbench;

import java.util.EventListener;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface WorkbenchServiceListener<T> extends EventListener {

    /**
     * Called when a {@code T} has been added to an object
     * that this listener is observing.
     *
     * @param service the {@code T} that was added
     */
    public void serviceAdded(T service);

    /**
     * Called when a {@code T} has been removed from an
     * object that this listener is observing.
     *
     * @param service the {@code T} that was removed
     */
    public void serviceRemoved(T service);
}
