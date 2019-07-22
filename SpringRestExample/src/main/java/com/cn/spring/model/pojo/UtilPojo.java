package com.cn.spring.model.pojo;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class UtilPojo implements Map<String, Object>, Serializable {
    private static final long serialVersionUID = 1L;
    
    private final LinkedHashMap<String, Object> utilPojoAsMap;

    public UtilPojo() {
        utilPojoAsMap = new LinkedHashMap<String, Object>();
        
    }

    public UtilPojo(final String key, final Object value) {
        utilPojoAsMap = new LinkedHashMap<String, Object>();
        utilPojoAsMap.put(key, value);
    }

    public static boolean isEmpty(Object object) {
        if (object == null) return true;
        if (object instanceof Map<?, ?>) return ((Map<?, ?>) object).isEmpty();
        if (object instanceof Collection<?>) return ((Collection<?>) object).isEmpty();
        return false;
    }

    public static UtilPojo pojo(Object... objects) {
        UtilPojo pojo = new UtilPojo();
        for (int i = 0; i < objects.length;)
            pojo.put(objects[i++].toString(), objects[i++]);
        return pojo;
    }

    public UtilPojo putMany(Object... objects) {
        for (int i = 0; i < objects.length;)
            this.put(objects[i++].toString(), objects[i++]);
        return this;
    }
    
    @Override
    public int size() {
        return utilPojoAsMap.size();
    }

    @Override
    public boolean isEmpty() {
        return utilPojoAsMap.isEmpty();
    }

    @Override
    public boolean containsValue(final Object value) {
        return utilPojoAsMap.containsValue(value);
    }

    @Override
    public boolean containsKey(final Object key) {
        return utilPojoAsMap.containsKey(key);
    }

    @Override
    public Object get(final Object key) {
        return utilPojoAsMap.get(key);
    }

    @Override
    public Object put(final String key, final Object value) {
        return utilPojoAsMap.put(key, value);
    }

    @Override
    public Object remove(final Object key) {
        return utilPojoAsMap.remove(key);
    }

    @Override
    public void putAll(final Map<? extends String, ?> map) {
        utilPojoAsMap.putAll(map);
    }

    @Override
    public void clear() {
        utilPojoAsMap.clear();
    }

    @Override
    public Set<String> keySet() {
        return utilPojoAsMap.keySet();
    }

    @Override
    public Collection<Object> values() {
        return utilPojoAsMap.values();
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return utilPojoAsMap.entrySet();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UtilPojo utilPojo = (UtilPojo) o;

        if (!utilPojoAsMap.equals(utilPojo.utilPojoAsMap)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return utilPojoAsMap.hashCode();
    }

    @Override
    public String toString() {
        return "utilPojo{"
               + utilPojoAsMap
               + '}';
    }

}
