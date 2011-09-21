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
package org.apache.karaf.eclipse.app.smk.internal;

import org.apache.karaf.eclipse.app.smk.Activator;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.servicemix.kernel.main.Lock;
import org.apache.servicemix.kernel.main.SimpleFileLock;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.startlevel.StartLevel;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public final class LockManager {

    public static LockManager instance = new LockManager();

    /**
     * If a lock should be used before starting the runtime
     */
    public static final String PROPERTY_USE_LOCK = "servicemix.lock";

    /**
     * The lock implementation
     */
    public static final String PROPERTY_LOCK_CLASS = "servicemix.lock.class";

    public static final String PROPERTY_LOCK_DELAY = "servicemix.lock.delay";

    public static final String PROPERTY_LOCK_LEVEL = "servicemix.lock.level";

    public static final String PROPERTY_LOCK_CLASS_DEFAULT = SimpleFileLock.class.getName();

    private static final long SYSTEM_BUNDLE_ID = 0;

    private static final int LOCKING_START_LEVEL = 100;

    private static final int LOCKING_STOP_LEVEL = 1;

    private final BundleContext bundleContext =
        Activator.getDefault().getBundle().getBundleContext();

    private final AtomicBoolean exiting = new AtomicBoolean(false);

    private final int lockDelay = 1000;

    private Lock lock;

    private LockManager() {
    }

    /**
     *
     * @return
     */
    public static LockManager getInstance() {
        return instance;
    }

    /**
     * Start the lock monitor
     */
    public void start() {

        final Thread t = new Thread() {
            @Override
            public void run() {
                lock(new Properties());
            }
        };

        t.setDaemon(true);
        t.setName("Apache ServiceMix Kernel :: Lock monitor thread");
        t.start();
    }

    /**
     *
     * @throws Exception
     */
    public void stop() throws Exception {
        exiting.set(true);
        unlock();
    }

    /**
    *
    * @param props
    */
   private void lock(Properties props) {
       try {
           if (Boolean.parseBoolean(props.getProperty(PROPERTY_USE_LOCK, "true"))) {
               final String clz = props.getProperty(PROPERTY_LOCK_CLASS, PROPERTY_LOCK_CLASS_DEFAULT);
               lock = (Lock) Class.forName(clz).getConstructor(Properties.class).newInstance(props);

               boolean lockLogged = false;

               for (;;) {
                   if (lock.lock()) {
                       if (lockLogged) {
                           System.out.println("Lock acquired.");
                       }

                       setStartLevel(LOCKING_START_LEVEL);

                       for (;;) {
                           if (!lock.isAlive()) {
                               break;
                           }
                           Thread.sleep(lockDelay);
                       }

                       if (bundleContext.getBundle(SYSTEM_BUNDLE_ID).getState() == Bundle.ACTIVE && !exiting.get()) {
                           System.out.println("Lost the lock, stopping this instance ...");
                           setStartLevel(LOCKING_STOP_LEVEL);
                       }
                       break;
                   } else if (!lockLogged) {
                       System.out.println("Waiting for the lock ...");
                       lockLogged = true;
                   }

                   Thread.sleep(lockDelay);
               }
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
   }

    /**
    *
    * @param level
    * @throws Exception
    */
   private void setStartLevel(int level) throws Exception {
       final ServiceReference[] refs =
           bundleContext.getServiceReferences(StartLevel.class.getName(), null);

       final StartLevel sl =
           (StartLevel) bundleContext.getService(refs[0]);

       sl.setStartLevel(level);
   }

   /**
    *
    * @throws Exception
    */
   private void unlock() throws Exception {
       if (lock != null) {
           lock.release();
       }
   }
}
