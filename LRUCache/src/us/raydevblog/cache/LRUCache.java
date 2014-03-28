package us.raydevblog.cache;

import java.util.*;

/**
 * LRU Cache:
 * LRU Cache uses a Least Recently Used (LRU) algorithm. It has a capacity
 * associated with it that is used to determine when objects should be
 * purged from the cache.
 * 
 * @author raychen
 *
 */
public class LRUCache<K, V> implements CacheInterface<K, V> {
    private int capacity;
    private int size;
    private final HashMap<K, V> entries;
    private final LinkedList<K> eldestEntries;
    
    /**
     * Create a new LRU cache.
     * 
     * @param capacity  the maximum number of elements this cache can hold
     */
    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity <= 0");
        }
        this.capacity = capacity;
        this.size = 0;
        this.entries = new HashMap<K, V>();
        this.eldestEntries = new LinkedList<K>();
    }
    
    /**
     * Return the number of entries in the cache
     */
    public int size() {
        return this.size;
    }
    
    /**
     * Retrieve an object from the cache using the specified key.
     * This method will make the retrieved object the most recently
     * used object.
     * 
     */
    public final V get(K key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        V value;
        synchronized (this) {
            value = entries.get(key);
            if (value != null) {
                eldestEntries.remove(key);
                eldestEntries.addLast(key);
            }
        }
        return value;
    }
    
    /**
     * Insert an object into the cache
     */
    public final void put(K key, V value) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        
        V previous;
        synchronized (this) {
            previous = entries.put(key, value);
            if (previous == null) {
                size++;
            } else {
                eldestEntries.remove(key);
            }
            eldestEntries.addLast(key);
        }
        
        purge();
    }
    
    /**
     * Remove an object from the cache
     */
    public void remove(Object key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }   
        synchronized (this) {
            entries.remove(key);
            eldestEntries.remove(key);
            size--;
        }
    }
    
    public synchronized void purge() {
        while (true) {
            if (size < 0 || entries.isEmpty()) {
                throw new IllegalStateException(String.format(
                        "Inconsistent cache [size=%d, entries=%d]", size, entries.size()));
            }
            
            if (size <= capacity) {
                break;
            }
            
            K keyEvict = eldestEntries.removeFirst();
            entries.remove(keyEvict);
            size--;
        }
    }
    
    public synchronized void print() {
        for (Map.Entry<K, V> entry : this.entries.entrySet()) {
            System.out.println(entry.getKey() + "=>" + entry.getValue());
        }
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        LRUCache<Integer, String> lruCache = new LRUCache<Integer, String>(3);
        lruCache.put(1, "lei");
        lruCache.put(2, "ray");
        lruCache.put(3, "chen");
        lruCache.put(1, "liu");
        lruCache.put(4, "aileen");
        lruCache.print();
    }

}
