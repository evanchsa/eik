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
package info.evanchik.eclipse.karaf.workbench.provider;

import java.util.Set;

import org.apache.aries.jmx.codec.BundleData;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.graphics.Image;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface RuntimeDataProvider {

    /**
     * Adds a listener for events issued by this {@code RuntimeDataProvider}.<br>
     * <br>
     * Instances can only be added once. Multiple adds of the same listener will
     * be ignored.
     *
     * @param listener
     */
    public void addListener(RuntimeDataProviderListener listener);

    /**
     * Getter for the specific {@link BundleItem} instance
     *
     * @param id
     *            the identifier of the bundle to retrieve
     * @return the {@code BundleItem} if it exists, null otherwise
     */
    public BundleItem getBundle(long id);

    /**
     * Getter for the {@link Set} of {@link BundleData}s in this runtime. The
     * returned {@code Set} cannot be modified.
     *
     * @return the {@code Set} of {@code BundleItem}s in this runtime
     */
    public Set<BundleItem> getBundles();

    /**
     * A 16x16 {@link Image} suitable for using in a view
     *
     * @return an {@link Image} suitable for using in a view
     */
    public Image getIcon();

    /**
     * Getter for the friendly name for this data provider
     *
     * @return the human readable name for this data provider
     */
    public String getName();

    /**
     * Getter for the specific {@link OSGiService} instance
     *
     * @param id
     *            the identifier of the service to retrieve
     * @return the {code OSGiService} if it exists, null otherwise
     */
    public ServiceItem getService(long id);

    /**
     * Getter for the {@link Set} of {@link ServiceItem}s in this runtime. The
     * returned {@code Set} cannot be modified.
     *
     * @return the {@code Set} of {@code ServiceItem}s in this runtime
     */
    public Set<ServiceItem> getServices();

    /**
     * Removes the listener from the {@code RuntimeDataProvider} if it exists.<br>
     * <br>
     * Has no effect if the listener was not added.
     *
     * @param listener
     *            the listener to remove
     */
    public void removeListener(RuntimeDataProviderListener listener);

    /**
     * Starts this data provider. Implementors should return as soon as possible
     * and defer any significant initialization work to a {@link Job}<br>
     * <br>
     * Has no effect if called multiple times. Implementations should take care
     * to handle this.
     */
    public void start();

    /**
     * Stops this data provider.<br>
     * <br>
     * Has no effect if called multiple times. Implementations should take care
     * to handle this.
     */
    public void stop();
}
