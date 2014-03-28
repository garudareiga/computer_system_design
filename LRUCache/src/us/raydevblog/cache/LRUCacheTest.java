package us.raydevblog.cache;

import static org.junit.Assert.*;

import org.junit.Test;

public class LRUCacheTest {

    @Test
    public void test1() {
        LRUCache<Integer, String> lruCache = new LRUCache<Integer, String>(3);
        lruCache.put(1, "Tony");
        lruCache.put(2, "Tim");
        lruCache.put(3, "Tiago");
        lruCache.put(1, "Danny");
        lruCache.get(2);
        lruCache.put(4, "Patrick");
        assertEquals("Danny", lruCache.get(1));
        assertEquals("Tim", lruCache.get(2));
        lruCache.remove(2);
        assertEquals(null, lruCache.get(2));
    }    
}
