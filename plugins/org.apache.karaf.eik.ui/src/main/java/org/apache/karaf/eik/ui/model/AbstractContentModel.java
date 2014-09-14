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
package org.apache.karaf.eik.ui.model;

import org.apache.karaf.eik.core.KarafPlatformModel;
import org.apache.karaf.eik.ui.IKarafProject;

import org.eclipse.core.runtime.PlatformObject;

public abstract class AbstractContentModel extends PlatformObject implements ContentModel {

    protected final KarafPlatformModel karafPlatformModel;

    protected final IKarafProject project;

    public AbstractContentModel(final IKarafProject project) {
        this.project = project;
        this.karafPlatformModel = (KarafPlatformModel) project.getAdapter(KarafPlatformModel.class);
    }

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") final Class adapter) {
        if (KarafPlatformModel.class.equals(adapter)) {
            return karafPlatformModel;
        } else {
            return super.getAdapter(adapter);
        }
    }

    @Override
    public Object getParent() {
        return project.getProjectHandle();
    }

}
