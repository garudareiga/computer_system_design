/**
 * Implementation of a set-associative cache.
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
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * A set-associate cache which has a fixed maximum number of sets (numSets).
 * Each set has a maximum number of elements (MAX_ELEMS_PER_SET).
 * If a set is full and another entry is added, an entry is dropped based on the eviction policy.
 */
public class KVCache implements KeyValueInterface {
    private int numSets = 100;
    private int maxElemsPerSet = 10;

    private KVCacheSet[] kvCacheSet;
    
    private class KVCacheEntry {
    	private String key;
    	private String value;
    	private boolean isReferenced;
    	
    	KVCacheEntry(String key, String value) {
    		this.key = key;
    		this.value = value;
    		this.isReferenced = false;
    	}
    	
    	public boolean isReferenced() {
    	    return isReferenced;
    	}
    }
    
    private class KVCacheSet {
    	private int maxElems = 10;
    	private LinkedList<KVCacheEntry> entries = new LinkedList<KVCacheEntry>();
    	private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    	private final WriteLock writeLock = readWriteLock.writeLock();
    	
    	KVCacheSet(int maxElems) {
    		this.maxElems = maxElems;
    	}
    	
    	boolean isFull() {
    		return (maxElems == entries.size());
    	}
    }
    
    /**
     * Creates a new LRU cache.
     * Add by Ray 03/24/2014
     * Creates a new set-associate Cache (http://en.wikipedia.org/wiki/CPU_cache#Two-way_set_associative_cache)
     * @param cacheSize    the maximum number of entries that will be kept in this cache.
     */
    public KVCache(int numSets, int maxElemsPerSet) {
        this.numSets = numSets;
        this.maxElemsPerSet = maxElemsPerSet;
        // TODO: Implement Me!
        this.kvCacheSet = new KVCacheSet[this.numSets];
        for (int i = 0; i < this.kvCacheSet.length; i++) {
        	this.kvCacheSet[i] = new KVCacheSet(this.maxElemsPerSet);
        }
    }

    /**
     * Retrieves an entry from the cache.
     * Assumes the corresponding set has already been locked for writing.
     * @param key the key whose associated value is to be returned.
     * @return the value associated to this key, or null if no value with this key exists in the cache.
     */
    public String get(String key) {
        // Must be called before anything else
        AutoGrader.agCacheGetStarted(key);
        AutoGrader.agCacheGetDelay();

        // TODO: Implement Me!
        if (key == null) {
        	throw new NullPointerException("key == null");
        }
        
        KVCacheSet kvCacheSet = this.kvCacheSet[getSetId(key)];
		for (KVCacheEntry entry : kvCacheSet.entries) {
			if (key.equals(entry.key)) {
				entry.isReferenced = true;
				return entry.value;
			}
		}

        // Must be called before returning
        AutoGrader.agCacheGetFinished(key);
        return null;
    }

    /**
     * Adds an entry to this cache.
     * If an entry with the specified key already exists in the cache, it is replaced by the new entry.
     * If the cache is full, an entry is removed from the cache based on the eviction policy
     * Assumes the corresponding set has already been locked for writing.
     * @param key    the key with which the specified value is to be associated.
     * @param value    a value to be associated with the specified key.
     * @return true is something has been overwritten
     */
    public void put(String key, String value) {
        // Must be called before anything else
        AutoGrader.agCachePutStarted(key, value);
        AutoGrader.agCachePutDelay();

        // TODO: Implement Me!
        // Second-Eviction Policy: 
        // http://goanna.cs.rmit.edu.au/~ronvs/WSWT/Online-Folder/chapter5/cs843_6.1.2.1.html
        if (key == null) {
        	throw new NullPointerException("key == null");
        }
        if (value == null) {
        	throw new NullPointerException("value == null");
        }
        
        KVCacheSet kvCacheSet = this.kvCacheSet[getSetId(key)];
        for (KVCacheEntry entry : kvCacheSet.entries) {
        	if (key.equals(entry.key)) {
        		entry.value = value;
        		return;
        	}
        }
        if (kvCacheSet.isFull()) {
            int entryIndex = 0;
            KVCacheEntry entry = null;
            for (; entryIndex < kvCacheSet.entries.size(); entryIndex++) {
                entry = kvCacheSet.entries.get(entryIndex);
                if (entry.isReferenced() == false) {
                    break;
                }
            }
            
        	for (KVCacheEntry tmpEntry : kvCacheSet.entries) {
        		if (tmpEntry.isReferenced == true) {
        			tmpEntry.isReferenced = false;
        		}
        	}
        	
        	if (entryIndex == kvCacheSet.entries.size()) {
        	    entry = kvCacheSet.entries.getFirst();
        	} else {
        	    entry = kvCacheSet.entries.get(entryIndex);
        	}
        	entry.key = key;
        	entry.value = value;
        	entry.isReferenced = true;
        } else {
        	KVCacheEntry newEntry = new KVCacheEntry(key, value);
        	newEntry.isReferenced = true;
        	kvCacheSet.entries.addLast(newEntry);
        }
        
        // Must be called before returning
        AutoGrader.agCachePutFinished(key, value);
    }

    /**
     * Removes an entry from this cache.
     * Assumes the corresponding set has already been locked for writing.
     * @param key    the key with which the specified value is to be associated.
     */
    public void del (String key) {
        // Must be called before anything else
        AutoGrader.agCacheDelStarted(key);
        AutoGrader.agCacheDelDelay();

        // TODO: Implement Me!
        KVCacheSet kvCacheSet = this.kvCacheSet[getSetId(key)];
        for (KVCacheEntry entry : kvCacheSet.entries) {
        	if (key.equals(entry.key)) {
        		kvCacheSet.entries.remove(entry);
        		return;
        	}
        }

        // Must be called before returning
        AutoGrader.agCacheDelFinished(key);
    }

    /**
     * @param key
     * @return    the write lock of the set that contains key.
     */
    public WriteLock getWriteLock(String key) {
        // TODO: Implement Me!
        //return null;
    	return kvCacheSet[getSetId(key)].writeLock;
    }

    /**
     *
     * @param key
     * @return    set of the key
     */
    private int getSetId(String key) {
        return Math.abs(key.hashCode()) % numSets;
    }

    public String toXML() {
        // TODO: Implement Me!
        //return null;
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            
            // KVCache
            Element kvcache = doc.createElement("KVCache");
            doc.appendChild(kvcache);
            // KVPair
            for (int setId = 0; setId < kvCacheSet.length; setId++) {
                KVCacheSet cacheSet = kvCacheSet[setId];
                
                Element setElement = doc.createElement("Set");
                setElement.setAttribute("Id", Integer.toString(setId));
                kvcache.appendChild(setElement);
                
                for (KVCacheEntry entry : cacheSet.entries) {
                    Element entryElement = doc.createElement("CacheEntry");
                    if (entry.isReferenced()) {
                        entryElement.setAttribute("isReferenced", "true");
                    } else {
                        entryElement.setAttribute("isReferenced", "false");
                    }
                    entryElement.setAttribute("isValid", "true");
                    setElement.appendChild(entryElement);
                
                    Element keyElement = doc.createElement("Key");
                    keyElement.appendChild(doc.createTextNode(entry.key));
                    
                    Element valueElement = doc.createElement("Value");
                    valueElement.appendChild(doc.createTextNode(entry.value));
                    
                    entryElement.appendChild(keyElement);
                    entryElement.appendChild(valueElement);
                }
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
}
