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
package org.apache.karaf.eik.core.features;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface FeaturesRepository {

    /**
     * Returns the {@link Feature}s found in this {@code FeaturesRepository}
     *
     * @return the {@code Feature}s found in this repository
     */
    public Features getFeatures();

    /**
     * Returns the name of the {@code FeaturesRepository}; this can be null
     *
     * @return the name of the {@code FeaturesRepository} or null if this is not
     *         available
     */
    public String getName();

    /**
     * Returns the list of referenced {@link Repository}s by this
     * {@code FeaturesRespository}
     *
     * @return the list of referenced {@code Repository}s; this list could be
     *         empty
     */
    public List<Repository> getRepositories();

    /**
     * Writes the contents of the {@code FeaturesRepository} to the specified
     * {@link OutputStream}
     *
     * @param out
     *            the {@code OutputStream}
     * @throws IOException
     *             if there is a problem writing the contents of the
     *             {@code FeaturesRepository}
     */
    public void write(OutputStream out) throws IOException;

}
