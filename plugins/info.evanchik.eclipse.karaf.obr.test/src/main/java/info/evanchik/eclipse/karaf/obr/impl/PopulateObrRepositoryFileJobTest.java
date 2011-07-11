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
package info.evanchik.eclipse.karaf.obr.impl;

import org.junit.Test;

/**
 *
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class PopulateObrRepositoryFileJobTest {

    @Test
    public void executeRepositoryFileJob() throws InterruptedException {
        final PopulateObrFileJob job = new PopulateObrFileJob("Test OBR file creation");
        job.schedule();
        job.join();
        // TODO: Parse the file and verify it
    }
}
