/**
 * Slave Server component of a KeyValue store
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

/**
 * This class defines the slave key value servers. Each individual KVServer
 * would be a fully functioning Key-Value server. For Project 3, you would
 * implement this class. For Project 4, you will have a Master Key-Value server
 * and multiple of these slave Key-Value servers, each of them catering to a
 * different part of the key namespace.
 *
 */
public class KVServer implements KeyValueInterface {

    public KVStore dataStore = null;
    public KVCache dataCache = null;

    public static final int MAX_KEY_SIZE = 256;
    public static final int MAX_VAL_SIZE = 256 * 1024;

    /**
     * @param numSets number of sets in the data Cache.
     */
    public KVServer(int numSets, int maxElemsPerSet) {
        dataStore = new KVStore();
        dataCache = new KVCache(numSets, maxElemsPerSet);

        AutoGrader.registerKVServer(dataStore, dataCache);
    }

    @Override
    public void put(String key, String value) throws KVException {
        // Must be called before anything else
        AutoGrader.agKVServerPutStarted(key, value);

        // TODO: implement me from proj3

        // Must be called before returning
        AutoGrader.agKVServerPutFinished(key, value);
    }

    @Override
    public String get(String key) throws KVException {
        // Must be called before anything else
        AutoGrader.agKVServerGetStarted(key);

        // TODO: implement me from proj3

        // Must be called before returning
        AutoGrader.agKVServerGetFinished(key);
        return null;
    }

    @Override
    public void del(String key) throws KVException {
        // Must be called before anything else
        AutoGrader.agKVServerDelStarted(key);

        // TODO: implement me from proj3

        // Must be called before returning
        AutoGrader.agKVServerDelFinished(key);
    }

    /**
     * Check if the server has a given key. This is used for TPC operations
     * that need to check whether or not a transaction can be performed but that
     * don't want to modify the state of the cache by calling get(). You are
     * allowed to call dataStore.get() for this method.
     *
     * @param key key to check for
     */
    public boolean hasKey(String key) throws KVException {
        // TODO: implement me

        return false;
    }
}
