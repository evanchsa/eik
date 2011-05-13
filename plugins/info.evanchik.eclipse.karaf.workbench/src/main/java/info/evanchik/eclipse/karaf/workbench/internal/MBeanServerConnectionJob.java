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
package info.evanchik.eclipse.karaf.workbench.internal;

import info.evanchik.eclipse.karaf.workbench.KarafWorkbenchActivator;
import info.evanchik.eclipse.karaf.workbench.jmx.JMXServiceDescriptor;

import java.io.IOException;
import java.util.Hashtable;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class MBeanServerConnectionJob extends Job {

    /**
     * Default maximum number of connection attempts
     */
    public static final int DEFAULT_MAX_RETRIES = 5;

    /**
     * Default number of milliseconds between rescheduling
     */
    public static final long DEFAULT_RESCHEDULE_DELAY = 5 * 1000;

    /**
     * Default number of milliseconds before attempting to connect to the JMX
     * server
     */
    public static final long DEFAULT_INITIAL_SCHEDULE_DELAY = 3 * 1000;

    /**
     * The JMX connection descriptor
     */
    private final JMXServiceDescriptor connection;

    /**
     * The time between connection retries
     */
    private long rescheduleDelay = DEFAULT_RESCHEDULE_DELAY;

    /**
     * The JMX environment used for passing credentials to the JMX connection
     */
    private final Hashtable<String, String[]> environment = new Hashtable<String, String[]>();

    /**
     * Determines if this {@link Job} has been canceled
     */
    private volatile boolean cancel = false;

    /**
     * The JMX MBeanServer client
     */
    private JMXConnector jmxClient;

    /**
     * Constructs a {@link Job} that will connect to the JMX MBeanServer on a
     * remote (or local) Karaf instance.
     *
     * @param name
     *            the friendly name of this job as displayed to the user
     * @param connection
     *            the {@link JMXServiceDescriptor} that represents
     *            the JMX MBeanServer this {@link Job} will connect to
     */
    public MBeanServerConnectionJob(final String name, final JMXServiceDescriptor connection) {
        super(name);

        this.connection = connection;

        String[] credentials;
        if (hasCredentials()) {
            credentials = new String[] { connection.getUsername(), connection.getPassword() };
            environment.put(JMXConnector.CREDENTIALS, credentials);
        }
    }

    public JMXConnector getJmxClient() {
        return jmxClient;
    }

    public JMXServiceDescriptor getMBeanServerConnectionDescriptor() {
        return connection;
    }

    public final long getRescheduleDelay() {
        return rescheduleDelay;
    }

    /**
     * Determines if this job successfully connected to the MBeanServer
     *
     * @return true if connected to the MBeanServer, false otherwise
     */
    public final boolean isConnected() {
        return jmxClient != null;
    }

    /**
     * @param rescheduleDelay
     *            the rescheduleDelay to set
     */
    public final void setRescheduleDelay(final long rescheduleDelay) {
        this.rescheduleDelay = rescheduleDelay;
    }

    @Override
    protected void canceling() {
        cancel = true;
    }

    @Override
    protected IStatus run(final IProgressMonitor monitor) {

	    IStatus status;

	    try {
	        jmxClient = JMXConnectorFactory.connect(connection.getUrl(), environment);

	        monitor.worked(5);

	        status = new Status(
	                IStatus.OK,
	                KarafWorkbenchActivator.PLUGIN_ID,
	                "Successfully connected to JMX MBeanServer for: " + getName());

	    } catch (final IOException ex) {
	        status = new Status(
	        		IStatus.WARNING,
	        		KarafWorkbenchActivator.PLUGIN_ID,
	        		"Retrying connection for for: " + getName(), ex);

		    if (!cancel) {
		    	schedule(rescheduleDelay);
		    }
	    }

	    return status;
    }

    /**
     * Determines if the {@link JMXServiceDescriptor} has
     * authentication information provided
     *
     * @return true if there is authentication information, false otherwise
     */
    private boolean hasCredentials() {
        if (connection.getUsername() != null && connection.getUsername().length() > 0) {
            return true;
        }

        return false;
    }
}
