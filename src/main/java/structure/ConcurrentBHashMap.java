package structure;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by aaa on 17-12-18.
 */
public class ConcurrentBHashMap<K, T, V> extends ConcurrentHashMap<K, Map<T, V>> {
//              接受字符返回节点集合
//            最后一个字符决定叶节点
//    private Map<T, V> values = new ConcurrentHashMap<>();
    
    public V getValue(K key, T tag){
        return this.get(key).get(tag);
    }
    
    public Collection<V> getValues(K key){
        Map<T, V> tvs = this.get(key);
        return tvs.values();
    }
    
    public void setValue(K key, T tag, V value){
        Map<T, V> tvs = this.get(key);
        if (tvs == null){
            tvs = new HashMap<>();
        }
        tvs.put(tag, value);
        this.put(key, tvs);
    }
}
