/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.karaf.eclipse.workbench.properties;

import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.ui.IKarafProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public abstract class KarafPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

    /**
     *
     * @return
     */
    protected final IKarafProject getKarafProject() {
        final IAdaptable project = getElement();
        final IKarafProject karafProject = (IKarafProject) project.getAdapter(IKarafProject.class);

        return karafProject;
    }

    /**
     *
     * @return
     */
    protected final KarafPlatformModel getKarafPlatformModel() {
        final IKarafProject karafProject = getKarafProject();
        final KarafPlatformModel karafPlatformModel = (KarafPlatformModel) karafProject.getAdapter(KarafPlatformModel.class);
        return karafPlatformModel;
    }
}
