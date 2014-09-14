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
package org.apache.karaf.eik.ui.project;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class KarafProjectNature implements IProjectNature {

    public static final String ID = "org.apache.karaf.eik.KarafProjectNature";

    private IProject project;

    @Override
    public void configure() throws CoreException {
        addToBuildSpec(KarafProjectBuilder.ID);
    }

    @Override
    public void deconfigure() throws CoreException {
        removeFromBuildSpec(KarafProjectBuilder.ID);
    }

    @Override
    public IProject getProject() {
        return project;
    }

    @Override
    public void setProject(final IProject project) {
        this.project = project;
    }

    private void addToBuildSpec(final String builderID) throws CoreException {

        final IProjectDescription description = this.project.getDescription();
        final int commandIndex = getKarafProjectBuilderCommandIndex(description.getBuildSpec());

        if (commandIndex == -1) {

            final ICommand command = description.newCommand();
            command.setBuilderName(builderID);
            setKarafProjectBuilderCommand(description, command);
        }
    }

    private int getKarafProjectBuilderCommandIndex(final ICommand[] buildSpec) {

        for (int i = 0; i < buildSpec.length; ++i) {
            if (buildSpec[i].getBuilderName().equals(KarafProjectBuilder.ID)) {
                return i;
            }
        }

        return -1;
    }

    protected void removeFromBuildSpec(final String builderID) throws CoreException {

        final IProjectDescription description = this.project.getDescription();
        final ICommand[] commands = description.getBuildSpec();
        for (int i = 0; i < commands.length; ++i) {
            if (commands[i].getBuilderName().equals(builderID)) {
                final ICommand[] newCommands = new ICommand[commands.length - 1];
                System.arraycopy(commands, 0, newCommands, 0, i);
                System.arraycopy(commands, i + 1, newCommands, i, commands.length - i - 1);
                description.setBuildSpec(newCommands);
                this.project.setDescription(description, null);
                return;
            }
        }
    }

    private void setKarafProjectBuilderCommand(
            final IProjectDescription description,
            final ICommand newCommand)
            throws CoreException {

            final ICommand[] oldBuildSpec = description.getBuildSpec();
            final int oldJavaCommandIndex = getKarafProjectBuilderCommandIndex(oldBuildSpec);
            ICommand[] newCommands;

            if (oldJavaCommandIndex == -1) {
                // Add a Java build spec before other builders (1FWJK7I)
                newCommands = new ICommand[oldBuildSpec.length + 1];
                System.arraycopy(oldBuildSpec, 0, newCommands, 1, oldBuildSpec.length);
                newCommands[0] = newCommand;
            } else {
                oldBuildSpec[oldJavaCommandIndex] = newCommand;
                newCommands = oldBuildSpec;
            }

            // Commit the spec change into the project
            description.setBuildSpec(newCommands);
            this.project.setDescription(description, null);
    }

}
