/**
 * Handle TPC connections over a socket interface
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
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Implements NetworkHandler to handle 2PC operation requests from the Master/
 * Coordinator Server
 *
 */
public class TPCMasterHandler implements NetworkHandler {

    public KVServer kvServer = null;
    public ThreadPool threadpool = null;
    public TPCLog tpcLog = null;
    public long slaveID = -1;

    // Used to handle the "ignoreNext" message
    public boolean ignoreNext = false;

    // Stored phase-1 request message from TPCMaster
    public KVMessage originalMessage = null;

    // Whether we sent back an abort decision in phase 1. Used and checked by
    // autograder. Is not used for any other logic.
    public boolean aborted = true;

    public TPCMasterHandler(KVServer keyserver) {
        this(keyserver, 1);
    }

    public TPCMasterHandler(KVServer keyserver, long slaveID) {
        this.kvServer = keyserver;
        this.slaveID = slaveID;
        this.threadpool = new ThreadPool(1);
    }

    public TPCMasterHandler(KVServer kvServer, long slaveID, int connections) {
        this.kvServer = kvServer;
        this.slaveID = slaveID;
        this.threadpool = new ThreadPool(connections);
    }

    /**
     * Set TPCLog after it has been rebuilt.
     * @param tpcLog
     */
    public void setTPCLog(TPCLog tpcLog) {
        this.tpcLog = tpcLog;
    }

    /**
     * Registers the slave server with the master.
     *
     * @param masterHostName
     * @param server SocketServer used by this slave server (contains the hostName and a random port)
     * @throws UnknownHostException
     * @throws IOException
     * @throws KVException
     */
    public void registerWithMaster(String masterHostName, SocketServer server)
            throws UnknownHostException, IOException, KVException {
        AutoGrader.agRegistrationStarted(slaveID);

        Socket master = new Socket(masterHostName, 9090);
        KVMessage regMessage = new KVMessage(
            "register", slaveID + "@" + server.getHostname() + ":" + server.getPort());
        regMessage.sendMessage(master);

        // Receive master response. Response should always be success.
        new KVMessage(master);

        master.close();
        AutoGrader.agRegistrationFinished(slaveID);
    }

    @Override
    public void handle(Socket master) throws IOException {
        AutoGrader.agReceivedTPCRequest(slaveID);
        Runnable r = new MasterHandler(kvServer, master);
        try {
            threadpool.addToQueue(r);
        } catch (InterruptedException e) {
            return; // ignore this error
        }
        AutoGrader.agFinishedTPCRequest(slaveID);
    }

    public class MasterHandler implements Runnable {

        public KVServer keyserver = null;
        public Socket master = null;

        public void closeConn() {
            try {
                master.close();
            } catch (IOException e) {}
        }

        public MasterHandler(KVServer keyserver, Socket master) {
            this.keyserver = keyserver;
            this.master = master;
        }

        @Override
        public void run() {
            KVMessage msg = null;

            // Implement me

            String key = msg.getKey();
            String msgType = msg.getMsgType();

            if (msgType.equals("getreq")) {
                handleGet(msg, key);
            } else if (msgType.equals("putreq")) {
                handlePut(msg, key);
            } else if (msgType.equals("delreq")) {
                handleDel(msg, key);
            } else if (msgType.equals("ignoreNext")) {
                // Set ignoreNext to true.
                // Implement me

                // Send back an acknowledgment
                // Implement me
            } else if (msgType.equals("commit") || msgType.equals("abort")) {
                // Check in TPCLog for the case when SlaveServer is restarted
                // Implement me

                handleMasterResponse(msg, originalMessage, aborted);

                // Reset state
                // Implement me
            }

            // Finally, close the connection
            closeConn();
        }

        /* Handle a get request from the master */
        public void handleGet(KVMessage msg, String key) {
            AutoGrader.agGetStarted(slaveID);

            // Implement me

            AutoGrader.agGetFinished(slaveID);
        }

        /* Handle a phase-1 2PC put request from the master */
        public void handlePut(KVMessage msg, String key) {
            AutoGrader.agTPCPutStarted(slaveID, msg, key);

            // Store for use in the second phase
            originalMessage = new KVMessage(msg);

            // Implement me

            AutoGrader.agTPCPutFinished(slaveID, msg, key);
        }

        /* Handle a phase-1 2PC del request from the master */
        public void handleDel(KVMessage msg, String key) {
            AutoGrader.agTPCDelStarted(slaveID, msg, key);

            // Store for use in the second phase
            originalMessage = new KVMessage(msg);

            // Implement me

            AutoGrader.agTPCDelFinished(slaveID, msg, key);
        }

        /**
         * Second phase of 2PC
         *
         * @param masterResp Global decision taken by the master
         * @param origMsg Phase-1 request coordinator/master).
         * @param origAborted Did this slave server abort it in the first phase
         */
        public void handleMasterResponse(KVMessage masterResp, KVMessage origMsg, boolean origAborted) {
            AutoGrader.agSecondPhaseStarted(slaveID, origMsg, origAborted);

            // Implement me

            AutoGrader.agSecondPhaseFinished(slaveID, origMsg, origAborted);
        }

    }

}
