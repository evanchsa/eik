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

import org.apache.karaf.eik.core.KarafCorePluginUtils;
import org.apache.karaf.eik.ui.IKarafProject;
import org.apache.karaf.eik.ui.KarafUIPluginActivator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

public class UserBundles extends AbstractContentModel {

   public UserBundles(final IKarafProject project) {
       super(project);
   }

    @Override
    public Object[] getElements() {
        final List<File> files = new ArrayList<File>();

        KarafCorePluginUtils.getFileList(karafPlatformModel.getUserDeployedDirectory().toFile(), ".jar", files, 50);
        KarafCorePluginUtils.getFileList(karafPlatformModel.getUserDeployedDirectory().toFile(), ".war", files, 50);

        return files.toArray(new Object[0]);
    }

    @Override
    public Image getImage() {
        return KarafUIPluginActivator.getDefault().getImageRegistry().get(KarafUIPluginActivator.BUNDLE_OBJ_IMG);
    }

    @Override
    public String toString() {
        return "User Bundles";
    }

}
