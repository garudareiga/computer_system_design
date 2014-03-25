/**
 * Persistent Key-Value storage layer. Current implementation is transient,
 * but assume to be backed on disk when you do your project.
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.Map;

//Add by Ray
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

/**
 * This is a dummy KeyValue Store. Ideally this would go to disk,
 * or some other backing store. For this project, we simulate the disk like
 * system using a manual delay.
 *
 *
 *
 */
public class KVStore implements KeyValueInterface {
    private Map<String, String> store     = null;

    public KVStore() {
        resetStore();
    }

    private void resetStore() {
        store = new HashMap<String, String>();
    }

    public void put(String key, String value) throws KVException {
        AutoGrader.agStorePutStarted(key, value);

        try {
            putDelay();
            store.put(key, value);
        } finally {
            AutoGrader.agStorePutFinished(key, value);
        }
    }

    public String get(String key) throws KVException {
        AutoGrader.agStoreGetStarted(key);

        try {
            getDelay();
            String retVal = this.store.get(key);
            if (retVal == null) {
                KVMessage msg = new KVMessage("resp", "key \"" + key + "\" does not exist in store");
                throw new KVException(msg);
            }
            return retVal;
        } finally {
            AutoGrader.agStoreGetFinished(key);
        }
    }

    public void del(String key) throws KVException {
        AutoGrader.agStoreDelStarted(key);

        try {
            delDelay();
            if(key != null)
                this.store.remove(key);
        } finally {
            AutoGrader.agStoreDelFinished(key);
        }
    }

    private void getDelay() {
        AutoGrader.agStoreDelay();
    }

    private void putDelay() {
        AutoGrader.agStoreDelay();
    }

    private void delDelay() {
        AutoGrader.agStoreDelay();
    }

    public String toXML() {
        // TODO: implement me
        //return null;
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            
            // KVStore
            Element kvstore = doc.createElement("KVStore");
            doc.appendChild(kvstore);
            // KVPair
            for (Map.Entry<String, String> entry : store.entrySet()) {
                Element kvpElement = doc.createElement("KVPair");
                kvstore.appendChild(kvpElement);
                
                Element keyElement = doc.createElement("Key");
                keyElement.appendChild(doc.createTextNode(entry.getKey()));
                
                Element valueElement = doc.createElement("Value");
                valueElement.appendChild(doc.createTextNode(entry.getValue()));
                
                kvpElement.appendChild(keyElement);
                kvpElement.appendChild(valueElement);
            }
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            // Prettify the XML output
            transformerFactory.setAttribute("indent-number", 2);
            Transformer transformer = transformerFactory.newTransformer();
            // Prettify the XML output
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            
            return writer.getBuffer().toString();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
        return null;
    }

    public void dumpToFile(String fileName) {
        // TODO: implement me
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(this.toXML());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Replaces the contents of the store with the contents of a file
     * written by dumpToFile; the previous contents of the store are lost.
     * @param fileName the file to be read.
     */
    public void restoreFromFile(String fileName) throws FileNotFoundException {
        // TODO: implement me
        File file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException(fileName);
        }
        
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(file);
            
            doc.getDocumentElement().normalize();
            
            NodeList nodeList = doc.getElementsByTagName("KVPair");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) node;
                    String key = elem.getElementsByTagName("Key").item(0).getTextContent();
                    String value = elem.getElementsByTagName("Value").item(0).getTextContent();
                    this.store.put(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
