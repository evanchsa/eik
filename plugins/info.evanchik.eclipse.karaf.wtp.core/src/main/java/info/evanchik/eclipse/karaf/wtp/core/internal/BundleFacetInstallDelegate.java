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
package info.evanchik.eclipse.karaf.wtp.core.internal;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.internal.core.natures.PDE;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
@SuppressWarnings("restriction")
public class BundleFacetInstallDelegate implements IDelegate {

    public void execute(IProject project, IProjectFacetVersion version, Object object, IProgressMonitor monitor) throws CoreException {
        addBuilder(PDE.MANIFEST_BUILDER_ID, project, version, object, monitor);
        addBuilder(PDE.SCHEMA_BUILDER_ID, project, version, object, monitor);
    }

    public void addBuilder(String builderId, IProject project, IProjectFacetVersion version, Object object, IProgressMonitor minotor) throws CoreException {

        final IProjectDescription description = project.getDescription();
        final ICommand builderCommand = getBuilderCommand(description, builderId);

        if (builderCommand == null) {
            final ICommand command = description.newCommand();

            command.setBuilderName(builderId);
            setBuilderCommand(project, description, command);
        }
    }

    private ICommand getBuilderCommand(IProjectDescription description, String builderId) throws CoreException {
        final ICommand[] commands = description.getBuildSpec();
        for (int i = 0; i < commands.length; ++i) {
            if (commands[i].getBuilderName().equals(builderId)) {
                return commands[i];
            }
        }
        return null;
    }

    private void setBuilderCommand(IProject project, IProjectDescription description, ICommand newCommand) throws CoreException {

        final ICommand[] oldCommands = description.getBuildSpec();
        final ICommand oldBuilderCommand = getBuilderCommand(description, newCommand.getBuilderName());

        final ICommand[] newCommands;

        if (oldBuilderCommand == null) {
            // Add a build spec after other builders
            newCommands = new ICommand[oldCommands.length + 1];
            System.arraycopy(oldCommands, 0, newCommands, 0, oldCommands.length);
            newCommands[oldCommands.length] = newCommand;
        } else {
            for (int i = 0, max = oldCommands.length; i < max; i++) {
                if (oldCommands[i] == oldBuilderCommand) {
                    oldCommands[i] = newCommand;
                    break;
                }
            }
            newCommands = oldCommands;
        }

        // Commit the spec change into the project
        description.setBuildSpec(newCommands);
        project.setDescription(description, null);
    }

}
