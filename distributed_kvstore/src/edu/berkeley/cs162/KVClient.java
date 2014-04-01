/**
 * Client component for generating load for the KeyValue store.
 * This is also used by the Master server to reach the slave nodes.
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

import java.io.*;
import java.util.*;
import java.net.Socket;


/**
 * This class is used to communicate with (appropriately marshalling and unmarshalling)
 * objects implementing the {@link KeyValueInterface}.
 *
 */
public class KVClient implements KeyValueInterface {

    public String server = null;
    public int port = 0;

    /**
     * @param server is the DNS reference to the Key-Value server
     * @param port is the port on which the Key-Value server is listening
     */
    public KVClient(String server, int port) {
        this.server = server;
        this.port = port;
    }

    private Socket connectHost() throws KVException {
        // TODO: implement me from proj3
        Socket sock = null;
        try {
            sock = new Socket(server, port);
        } catch (IOException e) {
            throw new KVException(new KVMessage("resp", "Network Error: Could not create socket"));
        }
        return sock;
    }

    private void closeHost(Socket sock) throws KVException {
        // TODO: implement me from proj3
        try {
            sock.close();
        } catch (IOException e) {
            throw new KVException(new KVMessage(KVMessage.RESP, "Network Error: Could not close socket"));
        }    
    }

    public void put(String key, String value) throws KVException {
        // TODO: implement me from proj3
        Socket sock = connectHost();
        KVMessage kvmsg = new KVMessage(KVMessage.PUT);
        kvmsg.setKey(key);
        kvmsg.setValue(value);
        kvmsg.sendMessage(sock);
        closeHost(sock);
    }

    public String get(String key) throws KVException {
        // TODO: implement me from proj3
        Socket sock = connectHost();
        // Send request
        KVMessage msgReq = new KVMessage(KVMessage.GET);
        msgReq.setKey(key);
        msgReq.sendMessage(sock);
        // Receive response
        KVMessage msgRep = new KVMessage(sock);
        String msgStr = msgRep.toXML();
        closeHost(sock);
        return msgStr;
    }

    public void del(String key) throws KVException {
        // TODO: implement me from proj3
        Socket sock = connectHost();
        KVMessage kvmsg = new KVMessage(KVMessage.DEL);
        kvmsg.setKey(key);
        kvmsg.sendMessage(sock);
        closeHost(sock);
    }

    public void ignoreNext() throws KVException {
        // TODO: implement me
        Socket sock = connectHost();
        KVMessage kvmsg = new KVMessage(KVMessage.IGNORENEXT);
        kvmsg.sendMessage(sock);
        closeHost(sock);
    }
}
