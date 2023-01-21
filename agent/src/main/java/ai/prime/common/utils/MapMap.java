package ai.prime.common.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MapMap<K1, K2, V> {
    private final Map<K1, Map<K2, V>> values;

    public MapMap() {
        values = new HashMap<>();
    }

    private void ensureKey(K1 key1) {
        if (!values.containsKey(key1)) {
            values.put(key1, new HashMap<>());
        }
    }

    public void put(K1 key1, K2 key2, V value) {
        ensureKey(key1);
        values.get(key1).put(key2, value);
    }

    public Collection<K1> getKeys(){
        return Collections.unmodifiableCollection(values.keySet());
    }

    public V get(K1 key1, K2 key2) {
        ensureKey(key1);

        return values.get(key1).get(key2);
    }
}
