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
package org.apache.karaf.eik.workbench;

import org.apache.karaf.eik.workbench.jmx.JMXServiceDescriptor;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.eclipse.core.runtime.IAdaptable;

public interface MBeanProvider extends IAdaptable {

    /**
     * A string constant used as a key to distinguish multiple implementations
     * of Karaf workbench services in the OSGi service registry
     */
    public static final String KARAF_WORKBENCH_SERVICES_ID =
        "org.apache.karaf.eik.jmx.workbench.services";

    /**
     * Closes this {@code MBeanProvider}. This will close any connections to a
     * remote {@code MBeanServer} and unregister any OSGi Services from the
     * Service Registry.<br>
     * <br>
     * Once an {@code MBeanProvider} is closed it cannot be re-opened and should
     * be discarded. <br>
     * <br>
     * This method is idempotent from the caller's perspective.
     */
    public void close();

    /**
     * Retrieves the {@link JMXServiceDescriptor} for this {@code MBeanProvider}
     *
     * @return the {@code JMXServiceDescriptor} for this {@code MBeanProvider}
     */
    public JMXServiceDescriptor getJMXServiceDescriptor();

    /**
     * Retrieves an MBean proxy of the given interface class
     *
     * @param <T>
     *            allows the compiler to know that if the {@code interfaceClass}
     *            parameter is {@code MyMBean.class}, for example, then the
     *            return type is {@code MyMBean}.
     * @param objectName
     *            the name of the MBean to forward to on the remote end point
     * @param interfaceClass
     * @return the new proxy instance
     */
    public <T> T getMBean(ObjectName objectName, Class<T> interfaceClass);

    /**
     * Getter for the {@link MBeanServerConnection}
     *
     * @return the {@link MBeanServerConnection}
     */
    public MBeanServerConnection getMBeanServerConnection();

    /**
     * Determines if this {@code MBeanProvider} has been opened.
     *
     * @return true if this {@code MBeanProvider} is open, false otherwise
     */
    public boolean isOpen();

    /**
     * Opens the {@code MBeanProvider} constructing the MBeans and registering
     * them as OSGi services. This {@code MBeanProvider} is also registered as
     * an OSGi service.<br>
     * <br>
     * This method is idempotent from the caller's perspective.
     *
     * @param memento
     *            Must not be null<br>
     *            This object is used to distinguish the services registered
     *            here from other {@code MBeanProvider}S. This memento is
     *            registered under the {@link KARAF_WORKBENCH_SERVICES_ID}
     *            property in the OSGi service registry.
     */
    public void open(Object memento);

}
