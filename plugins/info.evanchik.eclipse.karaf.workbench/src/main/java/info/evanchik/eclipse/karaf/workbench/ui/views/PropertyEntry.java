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
package info.evanchik.eclipse.karaf.workbench.ui.views;

/**
 * Encapsulates a property entry in a Map or Dictionary
 */
public class PropertyEntry implements Comparable<PropertyEntry> {
    private final Object owner;
    private final String key;
    private final Object value;

    public PropertyEntry(Object owner, String key, Object value) {
        this.owner = owner;
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public Object getOwner() {
        return owner;
    }

    /**
     * Compares two {@code PropertyEntry} objects to determine their
     * relationship to one another. The owner uses simple {@code equals} to
     * determine if the property entries can be compared at all. If the owner
     * objects are the same then the key is used to compare the two property
     * entries.
     *
     * @see Comparable#compareTo(Object)
     */
    public int compareTo(PropertyEntry o) {
        final PropertyEntry rhs = o;

        if (!owner.equals(rhs.owner)) {
            return 0;
        }

        return key.compareTo(rhs.key);
    }
}