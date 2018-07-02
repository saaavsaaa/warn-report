package cn.tellwhy.structure;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by aaa on 17-7-12.
 */
public class ConcurrentHashMapExtend<K, T, V> extends ConcurrentHashMap<K,Pair> {
    public void putKeysList(List<K> keys, Pair v){
        keys.forEach(k -> {put(k, v);});
    }
    
    public void putKeysList(Pair v, K... keys){
        for (int i = 0; i < keys.length; i++) {
            put(keys[i], v);
        }
    }
    
    public <T> void putKeysList(V v, Pair<K, T>... keys){
        for (int i = 0; i < keys.length; i++) {
            put(keys[i].getK(), new Pair<T, V>(keys[i].getV(), v));
        }
    }
    
    public T getValueKey(K k){
        Pair pair = get(k);
        return (T) pair.getK();
    }
    
    public V getValueV(K k){
        Pair pair = get(k);
        return (V) pair.getV();
    }
}
