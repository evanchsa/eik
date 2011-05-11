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
package info.evanchik.eclipse.karaf.workbench.ui.editor;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface EditableObject<T> {

    /**
    *
    * @return
    */
   public T getObject();


   /**
    *
    * @return
    */
   public boolean isNewObject();
}
