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
package org.apache.karaf.eik.core;

public interface KarafPlatformDetails {

    /**
     * Retrieves the description of this Karaf platform
     *
     * @return the description of the Karaf platform
     */
    public String getDescription();

    /**
     * Retrieves the name of this Karaf platform
     *
     * @return the name of the Karaf platform
     */
    public String getName();

    /**
     * Retrieves the version of this Karaf platform
     *
     * @return the version of the Karaf platform
     */
    public String getVersion();

}
