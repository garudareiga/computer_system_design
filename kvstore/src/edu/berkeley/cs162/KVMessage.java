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

import java.io.*;
import java.net.*;

// Add by Ray
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

/**
 * This is the object that is used to generate messages the XML based messages
 * for communication between clients and servers.
 */
public class KVMessage {
    private String msgType = null;
    private String key = null;
    private String value = null;
    private String message = null;

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

    /* Solution from http://weblogs.java.net/blog/kohsuke/archive/2005/07/socket_xml_pitf.html */
    private class NoCloseInputStream extends FilterInputStream {
        public NoCloseInputStream(InputStream in) {
            super(in);
        }

        public void close() {} // ignore close
    }

    /***
     *
     * @param msgType
     * @throws KVException of type "resp" with message "Message format incorrect" if msgType is unknown
     */
    public KVMessage(String msgType) throws KVException {
        // TODO: implement me
    	if (!(msgType.equals("getreq") ||
    		msgType.equals("putreq") ||
    		msgType.equals("delreq") ||
    		msgType.equals("resp")))
    		throw new KVException(new KVMessage("resp", "Message format incorrect"));
    	
    	this.msgType = msgType;
    }

    public KVMessage(String msgType, String message) throws KVException {
        // TODO: implement me
    	if (!(msgType.equals("getreq") ||
        	msgType.equals("putreq") ||
        	msgType.equals("delreq") ||
        	msgType.equals("resp")))
        	throw new KVException(new KVMessage("resp", "Message format incorrect"));
    	
    	this.msgType = msgType;
    	this.message = message;
    }

     /***
     * Parse KVMessage from socket's input stream
     * @param sock Socket to receive from
     * @throws KVException if there is an error in parsing the message. The exception should be of type "resp and message should be :
     * a. "XML Error: Received unparseable message" - if the received message is not valid XML.
     * b. "Network Error: Could not receive data" - if there is a network error causing an incomplete parsing of the message.
     * c. "Message format incorrect" - if there message does not conform to the required specifications. Examples include incorrect message type.
     */
    public KVMessage(Socket sock) throws KVException {
         // TODO: implement me
    	try {
    		NoCloseInputStream is = new NoCloseInputStream(sock.getInputStream());
    	} catch (IOException e) {
    		e.printStackTrace();
    	} catch (Exception e) {
    		throw new KVException(new KVMessage("resp", "XML Error: Received unparseable message"));
    	}
    }

    /**
     * Generate the XML representation for this message.
     * @return the XML String
     * @throws KVException if not enough data is available to generate a valid KV XML message
     */
    public String toXML() throws KVException {
    	// TODO: implement me
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
    		return writer.getBuffer().toString();
    		//String result = new String();
    		//transformer.transform(new DOMSource(doc), new StreamResult(result));
    		//return result;
    	} catch (ParserConfigurationException pce) {
    		pce.printStackTrace();
    	} catch (TransformerException tfe) {
    		tfe.printStackTrace();
    	}
        return null;
    }

    public void sendMessage(Socket sock) throws KVException {
        // TODO: implement me
        try {
            OutputStream os = sock.getOutputStream();
            PrintWriter out = new PrintWriter(os, true);
            out.println(this.toXML());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
