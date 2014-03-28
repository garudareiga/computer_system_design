/**
 * XML Parsing library for the key-value store
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

import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * This is the object that is used to generate messages the XML based messages
 * for communication between clients and servers.
 */
public class KVMessage implements Serializable {

    public static final long serialVersionUID = 6473128480951955693L;

    public String msgType = null;
    public String key = null;
    public String value = null;
    public String message = null;
    public String tpcOpId = null;

    /*
     * HINT: You may need to use this for constructors dealing with sockets:
     * http://weblogs.java.net/blog/kohsuke/archive/2005/07/socket_xml_pitf.html
     */
    private class NoCloseInputStream extends FilterInputStream {

        public NoCloseInputStream(InputStream in) {
            super(in);
        }

        public void close() {} // do nothing on close
    }

    public KVMessage(KVMessage kvm) {
        msgType = kvm.msgType;
        key = kvm.key;
        value = kvm.value;
        message = kvm.message;
        tpcOpId = kvm.tpcOpId;
    }

    public KVMessage(String msgType) throws KVException {
        // TODO: implement me
        if (msgType == null) {
            throw new NullPointerException("msgType == 0");
        }
        this.msgType = msgType;
    }

    public KVMessage(String msgType, String message) throws KVException {
        // TODO: implement me
        if (msgType == null) {
            throw new NullPointerException("msgType == 0");
        }
        if (message == null) {
            throw new NullPointerException("message == 0");
        }
        this.msgType = msgType;
        this.message = message;
    }

    /**
     * Creates a KVMessage from a socket.
     *
     * @param sock Socket to receive from
     * @throws KVException if there is an error in parsing the message.
     */
    public KVMessage(Socket sock) throws KVException {
        // TODO: implement me
    }

    /**
     * Creates a KVMessage from a socket within a certain timeout.
     *
     * @param sock Socket to receive from
     * @param timeout millseconds after which you give up
     * @throws KVException if there is an error in parsing the message.
     */
    public KVMessage(Socket sock, int timeout) throws KVException {
        // TODO: implement me
    }


    public final String getKey() {
        return key;
    }

    public final void setKey(String key) {
        this.key = key;
    }

    public final String getValue() {
        return value;
    }

    public final void setValue(String value) {
        this.value = value;
    }

    public final String getMessage() {
        return message;
    }

    public final void setMessage(String message) {
        this.message = message;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getTpcOpId() {
        return tpcOpId;
    }

    public void setTpcOpId(String tpcOpId) {
        this.tpcOpId = tpcOpId;
    }

    /**
     * Generate the serialized XML representation for this message.
     *
     * @return the XML String
     * @throws KVException
     */
    public String toXML() throws KVException {
        return null;
        // TODO: implement me
    }

    /**
     * Send this message to another host via socket. You will need to
     * flush the stream by calling sock.shutdownOutput()
     *
     * @param sock Socket with which to send this message
     * @throws KVException
     */
    public void sendMessage(Socket sock) throws KVException {
        // TODO: implement me from proj3
    }

}
