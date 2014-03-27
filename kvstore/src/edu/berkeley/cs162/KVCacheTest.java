package edu.berkeley.cs162;

import static org.junit.Assert.*;

import org.junit.Test;

public class KVCacheTest {

    @Test
    public void testKVCache() {
        KVCache kvCache = new KVCache(2, 2);
        kvCache.put("21", "Tim Duncan");
        kvCache.put("9", "Tony Parker");
        kvCache.put("20", "Manu Ginobili");
        kvCache.put("4", "Danny Green");
        kvCache.put("2", "Kawhi Leonard");
        assertEquals("Tony Parker", kvCache.get("9"));
        kvCache.del("4");
        assertEquals(null, kvCache.get("4"));
        kvCache.put("9", "Raja Rondo");
        assertEquals("Raja Rondo", kvCache.get("9"));
        kvCache.put("3", "Marco Belinelli");
        kvCache.put("22", "Tiago Splitter");
        
        System.out.println(kvCache.toXML());
    }

}
