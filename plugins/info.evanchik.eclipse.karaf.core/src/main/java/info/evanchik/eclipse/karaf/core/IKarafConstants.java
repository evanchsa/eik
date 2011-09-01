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
package info.evanchik.eclipse.karaf.core;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface IKarafConstants {

    /**
     * Karaf base directory, used to reference core Karaf system files; takes
     * precedence over Karaf home directory
     */
    public static final String KARAF_BASE_PROP = "karaf.base"; //$NON-NLS-1$

    public static final String KARAF_BUNDLE_LOCATIONS_PROP = "bundle.locations"; //$NON-NLS-1$

    public static final String KARAF_DATA_PROP = "karaf.data"; //$NON-NLS-1$

    public static final String KARAF_DEFAULT_BUNDLE_START_LEVEL = "60"; //$NON-NLS-1$

    public static final String KARAF_DEFAULT_CONFIG_PROPERTIES_FILE = "config.properties"; //$NON-NLS-1$

    public static final String KARAF_DEFAULT_PLATFORM_PROVIDER_SYMBOLIC_NAME = "info.evanchik.eclipse.karaf.target"; //$NON-NLS-1$

    public static final String KARAF_DEFAULT_STARTUP_PROPERTIES_FILE = "startup.properties"; //$NON-NLS-1$

    public static final String KARAF_DEFAULT_SYSTEM_PROPERTIES_FILE = "system.properties"; //$NON-NLS-1$

    /**
     * Karaf home directory, used to reference installation specific files
     */
    public static final String KARAF_HOME_PROP = "karaf.home"; //$NON-NLS-1$

    public static final String KARAF_INSTANCES_PROP = "karaf.instances"; //$NON-NLS-1$

    public static final String KARAF_JAAS_BOOT_BUNDLE_SYMBOLIC_NAME = "org.apache.felix.karaf.jaas.boot";

    public static final String KARAF_MAIN_BUNDLE_SYMBOLIC_NAME = "org.apache.felix.karaf.main";

    public static final String KARAF_OBR_REPOSITORY_PROP = "obr.repository.url";

    public static final String KARAF_OSGI_FRAMEWORK_ID = "info.evanchik.eclipse.karaf.Framework"; //$NON-NLS-1$

    public static final String ORG_APACHE_FELIX_KARAF_MANAGEMENT_CFG_FILENAME = "org.apache.felix.karaf.management.cfg"; //$NON-NLS-1$

    public static final String ORG_APACHE_KARAF_MANAGEMENT_CFG_FILENAME = "org.apache.karaf.management.cfg"; //$NON-NLS-1$

    public static final String ORG_APACHE_SERVICEMIX_FEATURES_CFG_FILENAME = "org.apache.servicemix.features.cfg"; //$NON-NLS-1$

    public static final String ORG_APACHE_SERVICEMIX_MANAGEMENT_CFG_FILENAME = "org.apache.servicemix.management.cfg"; //$NON-NLS-1$

    /**
     * System property that points to an Apache ServiceMix Kernel base
     * directory.
     *
     * @see IKarafConstants#KARAF_BASE_PROP
     */
    public static final String SERVICEMIX_BASE_PROP = "servicemix.base"; //$NON-NLS-1$

    /**
     * System property that points to an Apache ServiceMix Kernel home
     * directory.
     *
     * @see IKarafConstants#KARAF_HOME_PROP
     */
    public static final String SERVICEMIX_HOME_PROP = "servicemix.home"; //$NON-NLS-1$
}
