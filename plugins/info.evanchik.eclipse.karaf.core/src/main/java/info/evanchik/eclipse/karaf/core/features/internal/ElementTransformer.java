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
package info.evanchik.eclipse.karaf.core.features.internal;

import info.evanchik.eclipse.karaf.core.features.Bundle;
import info.evanchik.eclipse.karaf.core.features.Feature;
import info.evanchik.eclipse.karaf.core.features.Features;
import info.evanchik.eclipse.karaf.core.features.Repository;

import org.apache.commons.collections.Transformer;
import org.jdom.Element;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class ElementTransformer implements Transformer {

    @Override
    public Object transform(final Object object) {
        if (!(object instanceof Element)) {
            return null;
        }

        final Element element = (Element) object;

        if (element.getName().equalsIgnoreCase("repository")) {
            return new Repository(element);
        } else if (element.getName().equalsIgnoreCase("feature")) {
            return new Feature(element);
        } else if (element.getName().equalsIgnoreCase("bundle")) {
            return new Bundle(element);
        } else if (element.getName().equalsIgnoreCase("features")) {
            return new Features(element, null);
        } else {
            return object;
        }
    }
}
