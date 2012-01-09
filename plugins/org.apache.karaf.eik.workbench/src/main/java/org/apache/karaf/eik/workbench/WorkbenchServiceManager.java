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

import java.util.List;

public interface WorkbenchServiceManager<T> {

    /**
     * Adds a {@link List} of {@link T}s to this manager.
     * This method will notify all {@link WorkbenchServiceListener<T>}s registered.
     *
     * @param services
     *          the {@code List} of {@code T}s to add
     */
    public void add(List<T> services);

    /**
     * Adds a {@link T} to this manager. This method will
     * notify all {@link WorkbenchServiceListener<T>}s registered.
     *
     * @param service the {@code T} to add
     */
    public void add(T service);

    /**
     * Adds a {@link WorkbenchServiceListener<T>} to the list of listeners for this
     * manager. The listener will be notified whenever a
     * {@link T} is added or removed from the manager.<br>
     * <br>
     * Adding the same listener multiple times has no effect.
     *
     * @param listener the {@code WorkbenchServiceListener<T>} to add
     */
    public void addListener(WorkbenchServiceListener<T> listener);

    /**
     * Returns all of the registered {@link T}s. The
     * resulting {@code List} is read-only.
     *
     * @return a read-only {@code List} of {@code T}s
     */
    public List<T> getServices();

    /**
     * Determines if the given {@link T} is registered with
     * the manager.
     *
     * @param service the {@code T} to search for
     * @return true if the {@code T} was found, false otherwise
     */
    public boolean isRegistered(T service);

    /**
     * Removes a {@link List} of {@link T}s from the
     * manager.<br>
     * <br>
     * {@code Ts} present in the remove list but not
     * registered with the manager are ignored.
     *
     * @param services
     *          the {@code List} of {@code T}s to remove
     */
    public void remove(List<T> services);

    /**
     * Removes the given {@link T} from the manager.<br>
     * <br>
     * If the {@code T} is not registered with the manager,
     * this method has no effect.
     *
     * @param service the {@code T} to remove
     */
    public void remove(T service);

    /**
     * Removes the given {@link WorkbenchServiceListener<T>} from the manager.<br>
     * <br>
     * If the {@code WorkbenchServiceListener<T>} is not registered with the manager,
     * this method has no effect.
     *
     * @param listener the {@code WorkbenchServiceListener<T>} to remove
     */
    public void removeListener(WorkbenchServiceListener<T> listener);

}
