package edu.berkeley.cs162;

import static org.junit.Assert.*;

import org.junit.Test;

public class KVCacheTest {

    @Test
    public void testKVCache() {
        KVCache kvCache = new KVCache(2, 2);
        //KVCache kvCache = new KVCache(3, 3);
        kvCache.put("21", "Tim Duncan");
        kvCache.put("9", "Tony Parker");
        kvCache.put("20", "Manu Ginobili");
        kvCache.put("4", "Danny Green");
        kvCache.put("2", "Kawhi Leonard");
        kvCache.del("4");
        kvCache.put("9", "Raja Rondo");
        kvCache.put("3", "Marco Belinelli");
        kvCache.put("22", "Tiago Splitter");
        
        assertEquals("Raja Rondo", kvCache.get("9"));
        System.out.println(kvCache.toXML());
    }

}
