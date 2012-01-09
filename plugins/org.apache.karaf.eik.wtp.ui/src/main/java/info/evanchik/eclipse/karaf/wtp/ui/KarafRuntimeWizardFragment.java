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
package info.evanchik.eclipse.karaf.wtp.ui;

import org.apache.karaf.eik.ui.KarafUIPluginActivator;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
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
