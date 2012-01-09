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

import java.util.EventListener;

public interface WorkbenchServiceListener<T> extends EventListener {

    /**
     * Called when a {@code T} has been added to an object
     * that this listener is observing.
     *
     * @param service the {@code T} that was added
     */
    public void serviceAdded(T service);

    /**
     * Called when a {@code T} has been removed from an
     * object that this listener is observing.
     *
     * @param service the {@code T} that was removed
     */
    public void serviceRemoved(T service);

}
