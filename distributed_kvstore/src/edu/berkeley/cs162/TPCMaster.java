/**
 * Master for Two-Phase Commits
 *
 * @author Mosharaf Chowdhury (http://www.mosharaf.com)
 * @author Prashanth Mohan (http://www.cs.berkeley.edu/~prmohan)
 *
 * Copyright (c) 2012, University of California at Berkeley
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of University of California, Berkeley nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.berkeley.cs162;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class TPCMaster {

    // Timeout value used during 2PC operations
    public static final int TIMEOUT_MILLISECONDS = 5000;

    // Port on localhost to run registration server on
    private static final int REGISTRATION_PORT = 9090;

    // Cache stored in the Master/Coordinator Server
    public KVCache masterCache = new KVCache(100, 10);

    // Registration server that uses TPCRegistrationHandler
    public SocketServer regServer = null;
    
    // Number of slave servers in the system
    public int numSlaves = -1;

    // ID of the next 2PC operation
    public Long tpcOpId = 0L;
    
    // Slave Servers: ordered by random 64-bit slaveId
    public TreeMap<Long, SlaveInfo> slaveInfos;
    
    /**
     * SlaveInfo TreeMap Comparator
     * @author raychen
     *
     */
    private class UnsignedLongComparator implements Comparator<Long>{
        @Override
        public int compare(Long arg0, Long arg1) {
            if (TPCMaster.this.isLessThanUnsigned(arg0, arg1)) {
                return -1;
            } else if (arg0==arg1) {
                return 0;
            } else {
                return 1;
            }
        }

    }

    /**
     * Creates TPCMaster
     *
     * @param numSlaves number of slave servers expected to register
     * @throws Exception
     */
    public TPCMaster(int numSlaves) {
        this.numSlaves = numSlaves;
        try {
            regServer = new SocketServer(InetAddress.getLocalHost().getHostAddress(),
                                         REGISTRATION_PORT);
            slaveInfos = new TreeMap<Long, SlaveInfo>(new UnsignedLongComparator());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculates tpcOpId to be used for an operation. In this implementation
     * it is a long variable that increases by one for each 2PC operation.
     *
     * @return
     */
    public String getNextTpcOpId() {
        tpcOpId++;
        return tpcOpId.toString();
    }

    /**
     * Start registration server in a separate thread.
     */
    public void run() {
        AutoGrader.agTPCMasterStarted();
        // implement me
        TPCRegistrationHandler regHandler = new TPCRegistrationHandler(this);
        regServer.addHandler(regHandler);
        AutoGrader.agTPCMasterFinished();
    }

    /**
     * Converts Strings to 64-bit longs. Borrowed from http://goo.gl/le1o0W,
     * adapted from String.hashCode().
     *
     * @param string String to hash to 64-bit
     * @return long hashcode
     */
    public long hashTo64bit(String string) {
        long h = 1125899906842597L;
        int len = string.length();

        for (int i = 0; i < len; i++) {
            h = (31 * h) + string.charAt(i);
        }
        return h;
    }
    
    /**
     * Compares two longs as if they were unsigned (Java doesn't have unsigned
     * data types except for char). Borrowed from http://goo.gl/QyuI0V
     *
     * @param n1 First long
     * @param n2 Second long
     * @return is unsigned n1 less than unsigned n2
     */
    public boolean isLessThanUnsigned(long n1, long n2) {
        return (n1 < n2) ^ ((n1 < 0) != (n2 < 0));
    }

    public boolean isLessThanEqualUnsigned(long n1, long n2) {
        return isLessThanUnsigned(n1, n2) || (n1 == n2);
    }

    /**
     * Find primary replica for a given key.
     *
     * @param key
     * @return SlaveInfo of first replica
     */
    public SlaveInfo findFirstReplica(String key) {
        // 64-bit hash of the key
        long hashedKey = hashTo64bit(key.toString());
        // implement me
        Map.Entry<Long, SlaveInfo> entry = slaveInfos.floorEntry(hashedKey);
        return entry.getValue();
        //return null;
    }

    /**
     * Find the successor of firstReplica.
     *
     * @param firstReplica SlaveInfo of primary replica
     * @return SlaveInfo of successor replica
     */
    public SlaveInfo findSuccessor(SlaveInfo firstReplica) {
        // implement me
        Map.Entry<Long, SlaveInfo> entry = slaveInfos.ceilingEntry(firstReplica.slaveID + 1);
        return entry.getValue();
        //return null;
    }

    /**
     * Synchronized method to perform 2PC operations. This method contains the
     * bulk of the two-phase commit logic. It performs phase 1 and phase 2
     * with appropriate timeouts and retries. See the spec for details on the
     * expected behavior.
     *
     * @param msg
     * @param isPutReq boolean to distinguish put and del requests
     * @throws KVException if the operation cannot be carried out
     */
    public synchronized void performTPCOperation(KVMessage msg, boolean isPutReq) throws KVException {
        AutoGrader.agPerformTPCOperationStarted(isPutReq);
        // implement me
        String key = msg.getKey();
        SlaveInfo firstReplica = findFirstReplica(key);
        SlaveInfo secondReplica = findSuccessor(firstReplica);
        
        try {
            // Phase - 1
            Socket firstSock = firstReplica.connectHost();
            msg.sendMessage(firstSock, TIMEOUT_MILLISECONDS);
        
            Socket secondSock = secondReplica.connectHost();
            msg.sendMessage(secondSock, TIMEOUT_MILLISECONDS);
            
            KVMessage firstResponse = new KVMessage(firstSock);
            KVMessage secondResponse = new KVMessage(secondSock);
            
            // Phase -2
            if (firstResponse.getMsgType().equals(KVMessage.READY) &&
                secondResponse.getMsgType().equals(KVMessage.READY)) {
                // Commit
                msg = new KVMessage(KVMessage.COMMIT);
                msg.sendMessage(firstSock);
                msg = new KVMessage(KVMessage.COMMIT);
                msg.sendMessage(secondSock);
            } else {
                // Abort
                msg = new KVMessage(KVMessage.ABORT);
                msg.sendMessage(firstSock);
                msg = new KVMessage(KVMessage.ABORT);
                msg.sendMessage(secondSock);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        
        }
            
        AutoGrader.agPerformTPCOperationFinished(isPutReq);
        return;
    }

    /**
     * Perform GET operation in the following manner:
     * - Try to GET from cache, return immediately if found
     * - Try to GET from first/primary replica
     * - If primary succeeded, return value
     * - If primary failed, try to GET from the other replica
     * - If secondary succeeded, return value
     * - If secondary failed, return KVExceptions from both replicas
     * Please see spec for more details.
     *
     * @param msg Message containing Key to get
     * @return Value corresponding to the Key
     * @throws KVException
     */
    public String handleGet(KVMessage msg) throws KVException {
        AutoGrader.aghandleGetStarted();
        // implement me
        String value = null;
        String key = msg.getKey();
        value = masterCache.get(key);
        if (value == null) {
            SlaveInfo firstReplica = findFirstReplica(key);
            Socket firstSock = firstReplica.connectHost();
            msg.sendMessage(firstSock);
            
            SlaveInfo secondReplica = findSuccessor(firstReplica);
        }
        AutoGrader.aghandleGetFinished();
        return null;
    }


    /**
     * Implements NetworkHandler to handle registration requests from
     * SlaveServers.
     *
     */
    public class TPCRegistrationHandler implements NetworkHandler {
        public TPCMaster master = null;
        public ThreadPool threadpool = null;

        public TPCRegistrationHandler(TPCMaster master) {
            // Call the other constructor
            this(master, 1);
        }

        public TPCRegistrationHandler(TPCMaster master, int connections) {
            this.master = master;
            threadpool = new ThreadPool(connections);
        }

        @Override
        public void handle(Socket client) throws IOException {
            // implement me
            Runnable r = new RegistrationHandler(master, client);
            try {
                threadpool.addToQueue(r);
            } catch (InterruptedException e) {
                return;
            }
        }

        public class RegistrationHandler implements Runnable {
            public TPCMaster master = null;
            public Socket client = null;

            public RegistrationHandler(TPCMaster master, Socket client) {
                this.master = master;
                this.client = client;
            }

            @Override
            public void run() {
                // implement me
                try {
                    KVMessage msg;
                    msg = new KVMessage(client);
                    String msgType = msg.getMsgType();
                    if (msgType.equals("register")) {
                        SlaveInfo newSlaveInfo = new SlaveInfo(msg.getMessage());
                        //int index = Math.abs(newSlaveInfo.hashCode()) % master.numSlaves;
                        master.slaveInfos.add(index, newSlaveInfo);
                        
                        msg = new KVMessage(KVMessage.RESP, "Successfully registered " + msg.getMessage());
                        msg.sendMessage(client);
                    }
                } catch (KVException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Data structure to maintain information about SlaveServers
     *
     */
    public class SlaveInfo {
        // 64-bit globally unique ID of the SlaveServer
        public long slaveID = -1;
        // Name of the host this SlaveServer is running on
        public String hostName = null;
        // Port which SlaveServer is listening to
        public int port = -1;

        /**
         *
         * @param slaveInfo as "SlaveServerID@HostName:Port"
         * @throws KVException
         */
        public SlaveInfo(String slaveInfo) throws KVException {
            // implement me
            String[] firstPart = slaveInfo.split("@");
            String[] secondPart = firstPart[1].split(":");
            this.slaveID = Long.parseLong(firstPart[0]);
            this.hostName = secondPart[0];
            this.port = Integer.parseInt(secondPart[1]);
        }

        public long getSlaveID() {
            return slaveID;
        }

        public Socket connectHost() throws KVException {
            // TODO: implement me
            //return null;
            Socket sock = null;
            try {
                sock = new Socket(hostName, port);
            } catch (IOException e) {
                throw new KVException(new KVMessage("resp", "Network Error: Could not create socket"));
            }
            return sock;
        }

        public void closeHost(Socket sock) throws KVException {
            try {
                sock.close();
            } catch (IOException e) {
                throw new KVException(new KVMessage("resp", "Network Error: Could not close socket"));
            }
        }
    }
}
