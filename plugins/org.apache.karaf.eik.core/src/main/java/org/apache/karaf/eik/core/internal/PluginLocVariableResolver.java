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
package org.apache.karaf.eik.core.internal;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IDynamicVariableResolver;
import org.osgi.framework.Bundle;

/**
 * Resolver for ${plugin_loc:<bundle symbolic name>}. The argument is mandatory
 * and should be the plugins's symbolic name.<br>
 * <br>
 * The variable resolves to the absolute path of the plugin specified in the
 * argument.
 */
public class PluginLocVariableResolver implements IDynamicVariableResolver {

    public String resolveValue(IDynamicVariable variable, String argument) throws CoreException {
        if (argument == null) {
            final String message = "The plugin_loc variable requires the symbolic name of a bundle as an argument";

            throw new CoreException(new Status(IStatus.ERROR, KarafCorePluginActivator.PLUGIN_ID, IStatus.OK,
                            message, null));

        }

        final Bundle b = Platform.getBundle(argument);

        if (b == null) {
            // This does not throw an exception because it is expected that
            // people will put the wrong symbolic name in to the variable's
            // arguments.
            return null;
        }

        // At this point we have a valid bundle so resolve it the absolute file
        // system path
        try {
            final File bundleFile = FileLocator.getBundleFile(b);

            return bundleFile.getAbsolutePath();
        } catch (IOException e) {
            final String message = "Unable to resolve bundle to absolute filesystem path: "
                            + argument;

            throw new CoreException(new Status(IStatus.ERROR,
                            KarafCorePluginActivator.PLUGIN_ID, IStatus.OK, message,
                            null));
        }
    }

}