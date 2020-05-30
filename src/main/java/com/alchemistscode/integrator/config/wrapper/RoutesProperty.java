package com.alchemistscode.integrator.config.wrapper;

import java.util.HashMap;

public class RoutesProperty<K,V> extends HashMap<K,V> {
    @Override
    @SuppressWarnings("unchecked")
    public V put(K key, V value){
        if (value instanceof String){
            return super.put(key, (V) ((String) value).replace("$ {","${"));
        }
        else {
            return super.put(key,value);
        }
    }
}
