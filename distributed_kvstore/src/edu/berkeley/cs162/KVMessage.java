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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.Socket;
import java.net.SocketException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sun.font.CreatedFontTracker;

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
        createMessage(sock);
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
        try {
            sock.setSoTimeout(timeout);
            createMessage(sock);
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            throw new KVException(new KVMessage("resp", "Network Error: Could not receive data"));
        }
    }
    
    protected void createMessage(Socket sock) throws KVException {
        NoCloseInputStream is;
        try {
            is = new NoCloseInputStream(sock.getInputStream());
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(is);    

            // optinal normalize:
            // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();
          
            // KVMessage
            NodeList kvmsgNodeList = doc.getElementsByTagName("KVMessage");
            if (kvmsgNodeList.getLength() > 0) {
                Node kvmsgNode = kvmsgNodeList.item(0);
                if (kvmsgNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element kvmsgElement = (Element) kvmsgNode;
                    String msgType = kvmsgElement.getAttribute("type");
                    if (msgType == null)
                        throw new KVException(new KVMessage("resp", "Message format incorrect"));
                    this.msgType = msgType;
                    
                    // Key
                    NodeList keyNodeList = kvmsgElement.getElementsByTagName("Key");
                    if (keyNodeList.getLength() > 0) {
                        this.key = keyNodeList.item(0).getTextContent();
                    }
                    // Value
                    NodeList valueNodeList = kvmsgElement.getElementsByTagName("Value");
                    if (valueNodeList.getLength() > 0) {
                        this.value = valueNodeList.item(0).getTextContent();
                    }
                    // TPCOpId
                    NodeList tpcOpIdNodeList = kvmsgElement.getElementsByTagName("TPCOpId");
                    if (tpcOpIdNodeList.getLength() > 0) {
                        this.tpcOpId = tpcOpIdNodeList.item(0).getTextContent();
                    }
                    // Message
                    NodeList msgNodeList = kvmsgElement.getElementsByTagName("Message");
                    if (msgNodeList.getLength() > 0) {
                        this.message = valueNodeList.item(0).getTextContent();
                    }
                  }
             } else {
                 throw new KVException(new KVMessage("resp", "Message format incorrect"));
             }
      } catch (IOException e) {
          throw new KVException(new KVMessage("resp", "Network Error: Could not receive data"));
          //e.printStackTrace();
      } catch (Exception e) {
          throw new KVException(new KVMessage("resp", "XML Error: Received unparseable message"));
      }
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
        // TODO: implement me
        String msgStr = null;
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            
            Document doc = docBuilder.newDocument();
            // root
            Element kvmsg = doc.createElement("KVMessage");
            kvmsg.setAttribute("type", this.msgType);
            doc.appendChild(kvmsg);
            // Key element
            if (this.key != null) {
                Element keyElement = doc.createElement("Key");
                keyElement.appendChild(doc.createTextNode(this.key));
                kvmsg.appendChild(keyElement);
            }
            // Value element
            if (this.value != null) {
                Element valueElement = doc.createElement("Value");
                valueElement.appendChild(doc.createTextNode(this.value));
                kvmsg.appendChild(valueElement);
            }
            // TPCOpId element
            if (this.tpcOpId != null) {
                Element tpcOpIdElement = doc.createElement("TPCOpId");
                tpcOpIdElement.appendChild(doc.createTextNode(this.tpcOpId));
                kvmsg.appendChild(tpcOpIdElement);
            }           
            // Message element
            if (this.message != null) {
                Element msgElement = doc.createElement("Message");
                msgElement.appendChild(doc.createTextNode(this.message));
                kvmsg.appendChild(msgElement);
            }
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            msgStr = writer.getBuffer().toString();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
        return msgStr;
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
        try {
            OutputStream os = sock.getOutputStream();
            PrintWriter out = new PrintWriter(os, true);
            out.println(this.toXML());
            out.flush();
            sock.shutdownOutput();
        } catch (IOException e) {
            throw new KVException(new KVMessage("resp", "Network Error: Could not send data"));
        }
    }

}
