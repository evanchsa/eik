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
package org.apache.karaf.eik.ui;

public final class KarafLaunchConfigurationConstants {

    /**
     * The list of features to use during Karaf boot
     */
    public static final String KARAF_LAUNCH_BOOT_FEATURES = "karaf_boot_features";

    /**
     * The workspace project that contains all of the configuration data for the
     * launch configuration
     */
    public static final String KARAF_LAUNCH_CONFIGURATION_PROJECT = "karaf_configuration_project";

    /**
     * Determines whether or not the Karaf Features system is managed by the
     * Eclipse launch configuration
     */
    public static final String KARAF_LAUNCH_FEATURES_MANAGEMENT = "karaf_features_management";

    /**
     * PDE Launcher constant used for recording classpath entries used as part
     * of the boot classpath for Karaf
     */
    public static final String KARAF_LAUNCH_REQUIRED_BOOT_CLASSPATH = "karaf_required_boot_classpath";

    /**
     * Contains the root directory of the Karaf platform that this launch
     * configuration is configured against
     */
    public static final String KARAF_LAUNCH_SOURCE_RUNTIME = "karaf_source_runtime";

    /**
     * PDE Launcher constant used for determining if the local console should
     * start
     */
    public static final String KARAF_LAUNCH_START_LOCAL_CONSOLE = "karaf_start_local_console";

    /**
     * PDE Launcher constant used for determining if the remote console should
     * start
     */
    public static final String KARAF_LAUNCH_START_REMOTE_CONSOLE = "karaf_start_remote_console";

    /**
     * The password used to authenticate to the remote console
     */
    public static final String KARAF_REMOTE_CONSOLE_PASSWORD = "karaf_remote_console_password";

    /**
     * The username used to authenticate to the remote console
     */
    public static final String KARAF_REMOTE_CONSOLE_USERNAME = "karaf_remote_console_username";

    /**
     * Private constructor to prevent instantiation.
     */
    private KarafLaunchConfigurationConstants() {
        throw new AssertionError(KarafLaunchConfigurationConstants.class.getName() + " cannot be instantiated");
    }

}
