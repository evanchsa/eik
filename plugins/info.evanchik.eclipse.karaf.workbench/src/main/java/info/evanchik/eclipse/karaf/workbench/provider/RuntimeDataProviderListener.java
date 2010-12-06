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

import java.util.EnumSet;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface RuntimeDataProviderListener {

    /**
     * The type used to indicate how a {@code RuntimeDataProvider} has changed.<br>
     * <br>
     * <ul>
     * <li>ADD - Addition to a {@code RuntimeDataProvider}</li>
     * <li>REMOVE - Removal from a {@code RuntimeDataProvider}</li>
     * <li>CHANGE - General change or update of a {@code RuntimeDataProvider}</li>
     * </ul>
     *
     * @author Stephen Evanchik (evanchsa@gmail.com)
     *
     */
    public enum EventType {
        ADD, REMOVE, CHANGE,
    }

    /**
     * Invoked when the state of a {@link RuntimeDataProvider} changes.
     *
     * @param source
     *            the {@code RuntimeDataProvider}
     * @param type
     *            the type of change event as indicated by
     *            {@link RuntimeDataProviderListener.EventType}
     */
    public void providerChange(RuntimeDataProvider source, EnumSet<EventType> type);

    /**
     * Invoked when a {@link RuntimeDataProvider} starts execution.
     *
     * @param source
     *            the {@code RuntimeDataProvider}
     */
    public void providerStart(RuntimeDataProvider source);

    /**
     * Invoked when a {@link RuntimeDataProvider} stops execution.
     *
     * @param source
     *            the {@code RuntimeDataProvider}
     */
    public void providerStop(RuntimeDataProvider source);
}
