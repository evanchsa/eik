/*
 * Copyright (c) 2008 Neil Bartlett
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Neil Bartlett - initial implementation
 *     Stephen Evanchik - Updated to use data provider services
 */
package name.neilbartlett.eclipse.bundlemonitor.views.shared;

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