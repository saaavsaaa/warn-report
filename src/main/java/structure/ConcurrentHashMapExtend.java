package structure;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by aaa on 17-7-12.
 */
public class ConcurrentHashMapExtend<K,V> extends ConcurrentHashMap<K,V> {
    public void putKeysList(List<K> keys, V v){
        keys.forEach(k -> {put(k, v);});
    }
    
    public void putKeysList(V v, K... keys){
        for (int i = 0; i < keys.length; i++) {
            put(keys[i], v);
        }
    }
}
