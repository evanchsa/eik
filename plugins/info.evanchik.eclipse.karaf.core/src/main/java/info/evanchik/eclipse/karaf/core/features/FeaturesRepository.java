/**
 * Copyright (c) 2011 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package info.evanchik.eclipse.karaf.core.features;


import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
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
