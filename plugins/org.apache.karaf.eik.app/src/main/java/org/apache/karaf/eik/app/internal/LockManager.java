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
package org.apache.karaf.eik.app.internal;

import org.apache.karaf.eik.app.Activator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.AccessControlException;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.karaf.main.Lock;
import org.apache.karaf.main.SimpleFileLock;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.startlevel.StartLevel;

public final class LockManager {

    public static LockManager instance = new LockManager();

    public static final String KARAF_SHUTDOWN_PORT = "karaf.shutdown.port";

    public static final String KARAF_SHUTDOWN_HOST = "karaf.shutdown.host";

    public static final String KARAF_SHUTDOWN_PORT_FILE = "karaf.shutdown.port.file";

    public static final String KARAF_SHUTDOWN_COMMAND = "karaf.shutdown.command";

    public static final String KARAF_SHUTDOWN_PID_FILE = "karaf.shutdown.pid.file";

    public static final String DEFAULT_SHUTDOWN_COMMAND = "SHUTDOWN";

    /**
     * If a lock should be used before starting the runtime
     */
    public static final String PROPERTY_USE_LOCK = "karaf.lock";

    /**
     * The lock implementation
     */
    public static final String PROPERTY_LOCK_CLASS = "karaf.lock.class";

    public static final String PROPERTY_LOCK_DELAY = "karaf.lock.delay";

    public static final String PROPERTY_LOCK_LEVEL = "karaf.lock.level";

    public static final String PROPERTY_LOCK_CLASS_DEFAULT = SimpleFileLock.class.getName();

    private static final Logger LOG = Logger.getLogger(LockManager.class.getName());

    private static final long SYSTEM_BUNDLE_ID = 0;

    private static final int LOCKING_START_LEVEL = 100;

    private static final int LOCKING_STOP_LEVEL = 1;

    private final BundleContext bundleContext =
        Activator.getDefault().getBundle().getBundleContext();

    private final AtomicBoolean exiting = new AtomicBoolean(false);

    private final int lockDelay = 1000;

    private Lock lock;

    private Random random = null;

    private ServerSocket shutdownSocket;

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
        t.setName("Apache Felix Karaf :: Lock monitor thread");
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
                           LOG.info("Lock acquired.");
                       }

                       setStartLevel(LOCKING_START_LEVEL);

                       for (;;) {
                           if (!lock.isAlive()) {
                               break;
                           }
                           Thread.sleep(lockDelay);
                       }

                       if (bundleContext.getBundle(SYSTEM_BUNDLE_ID).getState() == Bundle.ACTIVE && !exiting.get()) {
                           LOG.info("Lost the lock, stopping this instance ...");
                           setStartLevel(LOCKING_STOP_LEVEL);
                       }
                       break;
                   } else if (!lockLogged) {
                       LOG.info("Waiting for the lock ...");
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

   protected void setupShutdown(Properties props) {
       try {
           String pidFile = props.getProperty(KARAF_SHUTDOWN_PID_FILE);
           if (pidFile != null) {
               RuntimeMXBean rtb = ManagementFactory.getRuntimeMXBean();
               String processName = rtb.getName();
               Pattern pattern = Pattern.compile("^([0-9]+)@.+$", Pattern.CASE_INSENSITIVE);
               Matcher matcher = pattern.matcher(processName);
               if (matcher.matches()) {
                   int pid = Integer.parseInt(matcher.group(1));
                   Writer w = new OutputStreamWriter(new FileOutputStream(pidFile));
                   w.write(Integer.toString(pid));
                   w.close();
               }
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
       try {
           int port = Integer.parseInt(props.getProperty(KARAF_SHUTDOWN_PORT, "0"));
           String host = props.getProperty(KARAF_SHUTDOWN_HOST, "localhost");
           String portFile = props.getProperty(KARAF_SHUTDOWN_PORT_FILE);
           final String shutdown = props.getProperty(KARAF_SHUTDOWN_COMMAND, DEFAULT_SHUTDOWN_COMMAND);
           if (port >= 0) {
               shutdownSocket = new ServerSocket(port, 1, InetAddress.getByName(host));
               if (port == 0) {
                   port = shutdownSocket.getLocalPort();
               }
               if (portFile != null) {
                   Writer w = new OutputStreamWriter(new FileOutputStream(portFile));
                   w.write(Integer.toString(port));
                   w.close();
               }
               Thread thread = new Thread() {
                   @Override
                public void run() {
                       try {
                           while (true) {
                               // Wait for the next connection
                               Socket socket = null;
                               InputStream stream = null;
                               try {
                                   socket = shutdownSocket.accept();
                                   socket.setSoTimeout(10 * 1000);  // Ten seconds
                                   stream = socket.getInputStream();
                               } catch (AccessControlException ace) {
                                   LOG.log(Level.WARNING, "Karaf shutdown socket: security exception: "
                                                      + ace.getMessage(), ace);
                                   continue;
                               } catch (IOException e) {
                                   LOG.log(Level.SEVERE, "Karaf shutdown socket: accept: ", e);
                                   System.exit(1);
                               }

                               // Read a set of characters from the socket
                               StringBuilder command = new StringBuilder();
                               int expected = 1024; // Cut off to avoid DoS attack
                               while (expected < shutdown.length()) {
                                   if (random == null) {
                                       random = new Random();
                                   }
                                   expected += (random.nextInt() % 1024);
                               }
                               while (expected > 0) {
                                   int ch = -1;
                                   try {
                                       ch = stream.read();
                                   } catch (IOException e) {
                                       LOG.log(Level.WARNING, "Karaf shutdown socket:  read: ", e);
                                       ch = -1;
                                   }
                                   if (ch < 32) {  // Control character or EOF terminates loop
                                       break;
                                   }
                                   command.append((char) ch);
                                   expected--;
                               }

                               // Close the socket now that we are done with it
                               try {
                                   socket.close();
                               } catch (IOException e) {
                                   // Ignore
                               }

                               // Match against our command string
                               boolean match = command.toString().equals(shutdown);
                               if (match) {
                                   bundleContext.getBundle(SYSTEM_BUNDLE_ID).stop();
                                   break;
                               } else {
                                   LOG.log(Level.WARNING, "Karaf shutdown socket:  Invalid command '" +
                                                      command.toString() + "' received");
                               }
                           }
                       } catch (Exception e) {
                           e.printStackTrace();
                       } finally {
                           try {
                               shutdownSocket.close();
                           } catch (IOException e) {
                           }
                       }
                   }
               };
               thread.setDaemon(true);
               thread.start();
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
   }

   private void unlock() throws Exception {
       if (lock != null) {
           lock.release();
       }
   }

}
