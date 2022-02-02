package io.github.divios.jcommands.util;

import java.util.*;

public class ValueMap {
    
    private final LinkedHashMap<String, Value> valueMap = new LinkedHashMap<>();

    public static ValueMap concat(ValueMap valueMap1, ValueMap valueMap2) {
        valueMap1.valueMap.putAll(valueMap2.valueMap);
        return valueMap1;
    }

    public static ValueMap of(Map<String, Value> valueMap) {
        return new ValueMap(valueMap);
    }

    ValueMap(Map<String, Value> valueMap) {
        this.valueMap.putAll(valueMap);
    }
    
    public boolean isEmpty() {
        return valueMap.isEmpty();
    }
    
    public int size() { return valueMap.size(); }

    public boolean has(String key) {
        return valueMap.containsKey(key);
    }
    
    public Value get(int pos) {
        Value value = null;

        int i = 0;
        for (Value value1 : valueMap.values()) {
            if (i == pos) {
                value = value1;
                break;
            }
            i++;
        }

        return value == null ? Value.EMPTY() : value;
    }

    public Value get(String key) {
        return valueMap.getOrDefault(key, Value.EMPTY());
    }

    public Set<String> keys() {
        return Collections.unmodifiableSet(valueMap.keySet());
    }

    public Collection<Value> values() {
        return Collections.unmodifiableCollection(valueMap.values());
    }
    
}
