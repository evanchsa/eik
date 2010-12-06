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
package info.evanchik.eclipse.karaf.workbench;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface MBeanProvider {

    /**
     * A string constant used as a key to distinguish multiple implementations
     * of Karaf workbench services in the OSGi service registry
     */
    public static final String KARAF_WORKBENCH_SERVICES_ID =
        "info.evanchik.eclipse.karaf.jmx.workbench.services";

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
