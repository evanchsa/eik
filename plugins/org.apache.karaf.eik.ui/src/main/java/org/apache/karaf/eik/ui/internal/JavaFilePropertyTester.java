/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.karaf.eik.ui.internal;

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
