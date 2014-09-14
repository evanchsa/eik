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
package org.apache.karaf.eik.wtp.ui;

import org.apache.karaf.eik.ui.KarafUIPluginActivator;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

public class KarafRuntimeWizardFragment extends WizardFragment {

    /**
     * The {@link Composite} control that displays this wizard's UI elements.
     */
    private KarafRuntimeComposite karafComposite;

    /**
     * Constructor that does nothing.
     */
    public KarafRuntimeWizardFragment() {
    }

    /**
     * Creates the UI controls for this {@link WizardFragment}.
     */
    @Override
    public Composite createComposite(Composite parent, IWizardHandle handle) {
        karafComposite = new KarafRuntimeComposite(parent, handle);
        return karafComposite;
    }

    /**
     * Establish the {@link IRuntimeWorkingCopy} that will be used to build up
     * the final {@link IRuntime} instance<br>
     *
     */
    @Override
    public void enter() {
        if (karafComposite == null) {
            return;
        }

        final IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel().getObject(
                        TaskModel.TASK_RUNTIME);

        karafComposite.setKarafRuntimeWC(runtime);

    }

    @Override
    public void exit() {
        final IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel().getObject(
                        TaskModel.TASK_RUNTIME);

        if (runtime.validate(null).getSeverity() != IStatus.ERROR) {
            final IPath path = runtime.getLocation();

            // Save the runtime's location in the preferences area for this
            // plugin for easy retrieval
            KarafUIPluginActivator.getDefault().getPluginPreferences().setValue(
                            "location" + runtime.getRuntimeType().getId(), path.toString()); // $NON-NLS-1$
            KarafUIPluginActivator.getDefault().savePluginPreferences();
        }

    }

    /**
     * This {@link WizardFragment} has UI elements to display (a
     * {@link Composite})
     *
     * @return true, UI elements are contributed
     */
    @Override
    public boolean hasComposite() {
        return true;
    }

    /**
     * This wizard is complete when the {@link IRuntimeWorkingCopy} successfully
     * validates. This happens when there is a name that does not conflict with
     * other names and the installation directory points to a valid Karaf
     * installation as determined by the {@code runtimeType} extension.
     */
    @Override
    public boolean isComplete() {
        final IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel().getObject(
                        TaskModel.TASK_RUNTIME);

        if (runtime == null) {
            return false;
        }

        final IStatus status = runtime.validate(null);
        return (status == null || status.getSeverity() != IStatus.ERROR);
    }

}
