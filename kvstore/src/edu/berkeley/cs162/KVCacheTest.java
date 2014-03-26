package edu.berkeley.cs162;

import static org.junit.Assert.*;

import org.junit.Test;

public class KVCacheTest {

    @Test
    public void testKVCache() {
        KVCache kvCache = new KVCache(100, 10);
        kvCache.put("21", "Tim Duncan");
        kvCache.put("9", "Tony Parker");
        kvCache.put("20", "Manu Ginobili");
        kvCache.put("4", "Danny Green");
        kvCache.put("2", "Kawhi Leonard");
    }

}
