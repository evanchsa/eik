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
package org.apache.karaf.eik.workbench.provider;

import java.util.EnumSet;
import java.util.EventListener;

public interface RuntimeDataProviderListener extends EventListener {

    /**
     * The type used to indicate how a {@code RuntimeDataProvider} has changed.<br>
     * <br>
     * <ul>
     * <li>ADD - Addition to a {@code RuntimeDataProvider}</li>
     * <li>REMOVE - Removal from a {@code RuntimeDataProvider}</li>
     * <li>CHANGE - General change or update of a {@code RuntimeDataProvider}</li>
     * </ul>
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
