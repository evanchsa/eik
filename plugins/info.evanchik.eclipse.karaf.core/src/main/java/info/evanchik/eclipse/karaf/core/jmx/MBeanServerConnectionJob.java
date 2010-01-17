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
package info.evanchik.eclipse.karaf.core.jmx;

import info.evanchik.eclipse.karaf.core.internal.KarafCorePluginActivator;

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
    public static final long DEFAULT_INITIAL_SCHEDULE_DELAY = 20 * 1000;

    /**
     * The JMX connection descriptor
     */
    private final MBeanServerConnectionDescriptor connection;

    /**
     * The maximum number of retries this job will attempt to connect to the JMX
     * server before failing.
     */
    private int maxRetries = DEFAULT_MAX_RETRIES;

    /**
     * The time between connection retries
     */
    private long rescheduleDelay = DEFAULT_RESCHEDULE_DELAY;

    /**
     * The JMX environment used for passing credentials to the JMX connection
     */
    private final Hashtable<String, String[]> environment = new Hashtable<String, String[]>();

    /**
     * The JMX MBeanServer client
     */
    private JMXConnector jmxClient;

    /**
     * The number of connection retries
     */
    private int retries = 0;

    /**
     * Constructs a {@link Job} that will connect to the JMX MBeanServer on a
     * remote (or local) Karaf instance.
     *
     * @param name
     *            the friendly name of this job as displayed to the user
     * @param connection
     *            the {@link MBeanServerConnectionDescriptor} that represents
     *            the JMX MBeanServer this {@link Job} will connect to
     */
    public MBeanServerConnectionJob(String name, MBeanServerConnectionDescriptor connection) {
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

    public final int getMaxRetries() {
        return maxRetries;
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
     * @param maxRetries
     *            the maxRetries to set
     */
    public final void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    /**
     * @param rescheduleDelay
     *            the rescheduleDelay to set
     */
    public final void setRescheduleDelay(long rescheduleDelay) {
        this.rescheduleDelay = rescheduleDelay;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {

        IStatus status;

        try {
            jmxClient = JMXConnectorFactory.connect(connection.getUrl(), environment);

            monitor.worked(5);

            status = new Status(IStatus.OK, KarafCorePluginActivator.PLUGIN_ID, "Successfully connected to JMX MBeanServer for: "
                    + getName());

        } catch (IOException ex) {

            if (retries < maxRetries) {
                retries++;

                monitor.worked(2);

                status = new Status(IStatus.WARNING, KarafCorePluginActivator.PLUGIN_ID, "Retrying connection for for: " + getName(), ex);

                schedule(rescheduleDelay);
            } else {
                status = new Status(IStatus.ERROR, KarafCorePluginActivator.PLUGIN_ID, "Unable to connect to: " + getName());
            }

        }

        return status;
    }

    /**
     * Determines if the {@link MBeanServerConnectionDescriptor} has
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
