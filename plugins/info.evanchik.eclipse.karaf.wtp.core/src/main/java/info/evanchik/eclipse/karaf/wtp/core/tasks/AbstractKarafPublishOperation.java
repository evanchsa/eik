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
package info.evanchik.eclipse.karaf.wtp.core.tasks;

import info.evanchik.eclipse.karaf.wtp.core.server.KarafServerBehavior;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.PublishOperation;
import org.eclipse.wst.server.core.util.PublishHelper;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public abstract class AbstractKarafPublishOperation extends PublishOperation {

    protected KarafServerBehavior server;

    protected IModule[] module;

    protected int publicationType;

    protected int moduleOperationType;

    protected PublishHelper helper;

    public AbstractKarafPublishOperation(KarafServerBehavior server, int publicationType, IModule[] module, int moduleOperationType) {
        super("Publish to server", "Publish module to Karaf server");

        this.server = server;
        this.module = module;
        this.publicationType = publicationType;
        this.moduleOperationType = moduleOperationType;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public int getKind() {
        return REQUIRED;
    }
}
