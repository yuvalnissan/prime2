package ai.prime.common.utils;

import java.util.*;

public class SetMap<K, V> {
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

    public void remove(K key, V value) {
        ensureKey(key);
        values.get(key).remove(value);
    }

    public Collection<K> getKeys(){
        return Collections.unmodifiableCollection(values.keySet());
    }

    public Set<V> getValues(K key){
        ensureKey(key);

        return new HashSet<>(values.get(key));
    }

    public SetMap<K, V> cloneDeep() {
        SetMap<K, V> clone = new SetMap<>();
        this.values.forEach((key, set) -> {
            set.forEach(val -> {
                clone.add(key, val);
            });
        });

        return clone;
    }
}
