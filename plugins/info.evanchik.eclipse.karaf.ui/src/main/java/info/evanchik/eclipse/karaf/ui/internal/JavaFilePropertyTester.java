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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * A {@link PropertyTester} implemenation that us suitable for evaluating
 * several {@link File} properties (file name, directory and, extension).
 * <p>
 * Consult the constants declared in {@link Property} for a description in how
 * the test is performed.
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public final class JavaFilePropertyTester extends PropertyTester {

    public static enum Property {
        /**
         * Matches the name of the file as returned by {@link File#getName()}
         */
        name,

        /**
         * Matches the directory of the file as returned by
         * {@link File#getParent()}. The test does not attempt to match the
         * directory exactly. Instead it considers a match if the file's
         * directory ends with the supplied value.
         */
        directory,

        /**
         * Matches the extension of the file as returned by
         * {@link IPath#getFileExtension()}. The {@code File} is converted to an
         * {@link IPath} using the {@link File#getAbsolutePath()}
         */
        extension
    }

    @Override
    public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue) {
        if (receiver instanceof File) {
            final File f = (File) receiver;
            if (property.equals(Property.name.toString())) {
                return f.getName().equals(expectedValue);
            } else if (property.equals(Property.directory.toString())) {
                return f.getParent().endsWith((String) expectedValue);
            } else if (property.equals(Property.extension.toString())) {
                return new Path(f.getAbsolutePath()).getFileExtension().equals(expectedValue);
            } else {
                return false;
            }
        }

        return false;
    }
}
