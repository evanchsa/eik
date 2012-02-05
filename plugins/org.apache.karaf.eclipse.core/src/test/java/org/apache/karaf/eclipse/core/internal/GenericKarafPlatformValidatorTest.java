/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.karaf.eclipse.core.internal;

import java.io.File;
import java.util.Collection;

import junit.framework.Assert;

import org.apache.karaf.eclipse.core.test.KarafTestCase;
import org.apache.karaf.eclipse.core.test.TestUtils;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class GenericKarafPlatformValidatorTest extends KarafTestCase {

    private final GenericKarafPlatformValidator genericKarafPlatformValidator = new GenericKarafPlatformValidator();

    @Test
    public void testValidDistributions() {
        final Collection<File> distributions = TestUtils.DEFAULT.getKarafDistributionsForTest();

        if (distributions.size() == 0) {
            Assert.fail("Missing Karaf distributions");
        }

        for (final File karaf : distributions) {
            Assert.assertTrue(karaf.getAbsolutePath(), genericKarafPlatformValidator.isValid(new Path(karaf.getAbsolutePath())));
        }

    }

    @Test
    public void testInvalidDistributions() {
        final File file = new File(".");

        Assert.assertFalse(file.getAbsolutePath(), genericKarafPlatformValidator.isValid(new Path(file.getAbsolutePath())));

    }
}
