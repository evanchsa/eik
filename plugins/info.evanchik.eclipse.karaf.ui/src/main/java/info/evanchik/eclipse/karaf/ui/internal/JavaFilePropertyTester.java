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
package info.evanchik.eclipse.karaf.ui.internal;

import java.io.File;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.Path;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public final class JavaFilePropertyTester extends PropertyTester {

    @Override
    public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue) {
        if (receiver instanceof File) {
            final File f = (File) receiver;
            if (property.equals("name")) {
                return f.getName().equals(expectedValue);
            } else if (property.equals("directory")) {
                return f.getParent().endsWith((String) expectedValue);
            } else if (property.equals("extension")) {
                return new Path(f.getAbsolutePath()).getFileExtension().equals(expectedValue);
            } else {
                return false;
            }
        }

        return false;
    }

}
