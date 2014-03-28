package us.raydevblog.cache;

public interface CacheInterface<K, V> {

    public V get(K key);
    
    public void put(K key, V value);
    
    public void remove(Object key);

}
