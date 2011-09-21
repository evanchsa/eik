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
package org.apache.karaf.eclipse.smk;

import org.apache.karaf.eclipse.core.KarafPlatformModel;
import org.apache.karaf.eclipse.core.KarafPlatformModelFactory;
import org.apache.karaf.eclipse.core.KarafPlatformValidator;
import org.apache.karaf.eclipse.smk.internal.ServiceMixKernelPlatformModel;
import org.apache.karaf.eclipse.smk.internal.ServiceMixKernelPlatformValidator;

import org.eclipse.core.runtime.IPath;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class ServiceMixKernelPlatformModelFactory implements KarafPlatformModelFactory {

    private static final ServiceMixKernelPlatformValidator platformValidator =
        new ServiceMixKernelPlatformValidator();

    @Override
    public KarafPlatformModel getPlatformModel(final IPath rootDirectory) {
        if (!platformValidator.isValid(rootDirectory)) {

        }

        return new ServiceMixKernelPlatformModel(rootDirectory);
    }

    @Override
    public KarafPlatformValidator getPlatformValidator() {
        return platformValidator;
    }

}
