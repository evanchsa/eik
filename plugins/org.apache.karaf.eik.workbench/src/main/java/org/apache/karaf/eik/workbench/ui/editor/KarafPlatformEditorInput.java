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
package org.apache.karaf.eik.workbench.ui.editor;

import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.ui.IKarafProject;
import org.apache.karaf.eik.workbench.KarafWorkbenchActivator;

import org.eclipse.jface.resource.ImageDescriptor;

public class KarafPlatformEditorInput extends AbstractEditorInput {

    private final IKarafProject karafProject;

    public KarafPlatformEditorInput(final IKarafProject karafProject) {
        this.karafProject = karafProject;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof KarafPlatformEditorInput)) {
            return false;
        }

        final KarafPlatformEditorInput other = (KarafPlatformEditorInput) obj;
        if (karafProject == null) {
            if (other.karafProject!= null) {
                return false;
            }
        } else if (!karafProject.equals(other.karafProject)) {
            return false;
        }

        return true;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return KarafWorkbenchActivator.getDefault().getImageRegistry().getDescriptor(KarafWorkbenchActivator.LOGO_16X16_IMG);
    }

    public KarafPlatformModel getKarafPlatform() {
        return (KarafPlatformModel) karafProject.getAdapter(KarafPlatformModel.class);
    }

    @Override
    public String getName() {
        return karafProject.getName();
    }

    @Override
    public String getToolTipText() {
        return karafProject.getName() + " located at: " + getKarafPlatform().getRootDirectory().toOSString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (karafProject == null ? 0 : karafProject.hashCode());
        return result;
    }

}
