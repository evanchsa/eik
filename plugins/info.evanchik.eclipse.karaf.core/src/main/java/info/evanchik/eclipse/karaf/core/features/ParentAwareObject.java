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
package info.evanchik.eclipse.karaf.core.features;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface ParentAwareObject<T> {

    /**
     * Retuns the parent of this object
     *
     * @return the parent of this object
     */
    public T getParent();
}
