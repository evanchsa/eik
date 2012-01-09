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
package org.apache.karaf.eik.core.equinox;

import java.net.MalformedURLException;
import java.net.URL;

public class BundleEntry {

    /**
     * A builder to help facilitate the construction of {@link BundleEntry}
     * objects.
     */
    public static class Builder {

        private final String bundle;

        private final URL bundleUrl;

        private String startLevel;

        private String autostart;

        /**
         * Builder for a simple {@link BundleEntry}
         *
         * @param bundle
         *            the string location of the bundle
         */
        public Builder(final String bundle) {
            if (bundle == null) {
                throw new NullPointerException("bundle");
            }

            this.bundle = bundle;
            this.bundleUrl = null;
        }

        /**
         * Builder for a absolutely qualified {@link UrlBundleEntry}
         *
         * @param u
         *            the URL to the bundle
         */
        public Builder(final URL u) {
            if (u == null) {
                throw new NullPointerException("u");
            }

            this.bundleUrl = u;
            this.bundle = null;
        }

        public Builder startLevel(final String startLevel) {
            this.startLevel = startLevel;
            return this;
        }

        public Builder autostart(final String autostart) {
            if (autostart == null) {
                throw new NullPointerException("autostart");
            }

            this.autostart = autostart;
            return this;
        }

        /**
         * Construct the appropriate {@link BundleEntry} type
         *
         * @return the {@link BundleEntry}
         */
        public BundleEntry build() {
            final BundleEntry entry;
            if (bundle != null) {
                entry = new BundleEntry(bundle);
            } else {
                entry = new UrlBundleEntry(bundleUrl);
            }

            entry.startLevel = startLevel;
            entry.autostart = autostart;

            return entry;
        }
    }

    /**
     * Parses an entry from an {@code osgi.bundles} property. The format is:<br>
     * <br>
     * <URL | simple bundle location>[@ [<start-level>] [":start"]] <br>
     *
     * @param s
     *            the candidate string
     * @return an instance of {@code EquinoxBundleEntry}
     */
    public static BundleEntry fromString(final String s) {
        if (s == null) {
            throw new NullPointerException("s");
        }

        String candidateBundle;
        String startComponent;

        final int at = s.indexOf('@');
        if (at != -1) {
            candidateBundle = s.substring(0, at);
            startComponent = s.substring(at + 1);
        } else {
            candidateBundle = s;
            startComponent = "";
        }

        URL u = null;
        try {
            u = new URL(candidateBundle);
        } catch (final MalformedURLException e) {
            // Do nothing as this is acceptable for a simple bundle
        }

        final BundleEntry entry;
        if (u == null) {
            entry = new BundleEntry(candidateBundle);
        } else {
            entry = new BundleEntry.UrlBundleEntry(u);
        }

        if (startComponent.length() > 0) {
            final int colon = startComponent.indexOf(':');
            if (colon == -1) {
                entry.startLevel = startComponent;
            } else {
                entry.startLevel = startComponent.substring(0, colon);
                entry.autostart = startComponent.substring(colon + 1);
            }
        }

        return entry;
    }

    /**
     * A simple class to distinguish between simple bundle entries and URL
     * bundle entries.
     *
     */
    private static class UrlBundleEntry extends BundleEntry {

        /**
         * Constructs an entry that is based on a fully qualified URL to the
         * bundle.
         *
         * @param bundleUrl
         *            the bundle in URL form
         */
        public UrlBundleEntry(final URL bundleUrl) {
            super(bundleUrl.toExternalForm());
        }
    };

    private final String bundle;

    private String startLevel;

    private String autostart;

    /**
     * Construct a bundle entry using the specified string as the bundle
     * location.
     *
     * @param bundle
     *            the location of the bundle
     */
    private BundleEntry(final String bundle) {
        this.bundle = bundle;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof BundleEntry == false) {
            return false;
        }

        final BundleEntry rhs = (BundleEntry) obj;
        return toString().equals(rhs.toString());
    }

    public String getAutostart() {
        return autostart;
    }

    public String getBundle() {
        return bundle;
    }

    public String getStartLevel() {
        return startLevel;
    }

    @Override
    public int hashCode() {
        // This should be revisited
        return toString().hashCode();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(bundle);
        if (startLevel != null) {
            sb.append("@");
            sb.append(startLevel);
            if (autostart != null) {
                sb.append(":");
                sb.append(autostart);
            }
        }

        return sb.toString();
    }

}
