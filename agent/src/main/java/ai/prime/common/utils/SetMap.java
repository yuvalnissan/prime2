package ai.prime.common.utils;

import java.util.*;

public class SetMap <K, V> {
    private Map<K, Set<V>> values;

    public SetMap() {
        this.values = new HashMap<>();
    }

    private void ensureKey(K key) {
        if (!values.containsKey(key)) {
            values.put(key, new HashSet<>());
        }
    }

    public void add(K key, V value) {
        ensureKey(key);
        values.get(key).add(value);
    }

    public Collection<K> getKeys(){
        return Collections.unmodifiableCollection(values.keySet());
    }

    public Set<V> getValues(K key){
        ensureKey(key);

        return Set.copyOf(values.get(key));
    }
}
