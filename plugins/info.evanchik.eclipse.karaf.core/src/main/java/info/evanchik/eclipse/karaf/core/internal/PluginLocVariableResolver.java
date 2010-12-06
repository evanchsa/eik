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
package info.evanchik.eclipse.karaf.core.internal;

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
 *
 * Resolver for ${plugin_loc:<bundle symbolic name>}. The argument is mandatory
 * and should be the plugins's symbolic name.<br>
 * <br>
 * The variable resolves to the absolute path of the plugin specified in the
 * argument.
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class PluginLocVariableResolver implements IDynamicVariableResolver {

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.core.variables.IDynamicVariableResolver#resolveValue(org.
     * eclipse.core.variables.IDynamicVariable, java.lang.String)
     */
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
