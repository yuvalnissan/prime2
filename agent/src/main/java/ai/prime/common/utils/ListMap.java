package ai.prime.common.utils;

import java.util.*;

public class ListMap<K, V> {
    private Map<K, List<V>> values;

    public ListMap() {
        this.values = new HashMap<>();
    }

    private void ensureKey(K key) {
        if (!values.containsKey(key)) {
            values.put(key, new LinkedList<>());
        }
    }

    public void add(K key, V value) {
        ensureKey(key);
        values.get(key).add(value);
    }

    public Collection<K> getKeys(){
        return Collections.unmodifiableCollection(values.keySet());
    }

    public List<V> getValues(K key){
        ensureKey(key);

        return List.copyOf(values.get(key));
    }
}
