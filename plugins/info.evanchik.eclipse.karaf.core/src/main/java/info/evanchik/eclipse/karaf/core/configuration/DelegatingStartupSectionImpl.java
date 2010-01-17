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
package info.evanchik.eclipse.karaf.core.configuration;

import info.evanchik.eclipse.karaf.core.KarafPlatformModel;
import info.evanchik.eclipse.karaf.core.configuration.internal.StartupSectionImpl;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class DelegatingStartupSectionImpl extends StartupSectionImpl {

    private final StartupSection delegateModel;

    /**
     * Constructs a {@link StartupSection} that has its configuration stored by
     * one model (the {@code parent}, but delegates all queries for startup
     * state to the {@code delegate}.
     *
     * @param parent
     *            the parent {@link KarafPlatformModel}
     * @param delegateModel
     *            the delegate {@link StartupSection} that answers queries about
     *            startup state
     */
    public DelegatingStartupSectionImpl(KarafPlatformModel parent, StartupSection delegateModel) {
        super(parent);

        this.delegateModel = delegateModel;
    }

    @Override
    public boolean containsPlugin(String bundleSymbolicName) {
        return delegateModel.containsPlugin(bundleSymbolicName);
    }

    @Override
    public String getStartLevel(String bundleSymbolicName) {
        return delegateModel.getStartLevel(bundleSymbolicName);
    }

    /**
     * Delegate {@link StartupSection}S should not try and populate a startup
     * model.
     */
    @Override
    protected void populateStartupStateModel() {
    }
}
