package com.cn.spring.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.function.Function;

import com.cn.spring.model.pojo.UtilPojo;

import static com.cn.spring.util.ConversionUtil.convert;
import static com.cn.spring.util.ObjectUtil.getProperty;
import static com.cn.spring.util.StringUtil.DEFAULT_SEPERATOR_STR;
import static org.springframework.beans.BeanUtils.copyProperties;


public class CollectionUtil {
    public static final Function<Object, Object> EMPTY_TRANSFORMER = o -> o;

    private CollectionUtil() {
    }

    // Thread Safe Singleton getInstance() Method Implementation
    public static CollectionUtil getInstance() {
        return _instance.instance;
    }

    @SafeVarargs
    public static <O> Map<?, ?> map(O... objects) {
        return populateMap(new HashMap<>(), (Object[]) objects);
    }

    public static Map<?, ?> populateMap(Map<Object, Object> map, Object... objects) {
        for (int i = 0; i < objects.length; )
            map.put(objects[i++], objects[i++]);
        return map;
    }


    @SafeVarargs
    public static <K, V> Map<K, V> mergeMaps(Map<K, V>... maps) {
        if (maps == null || maps.length == 0) return null;
        if (maps.length == 1) return maps[0];
        Map<K, V> mainMap = maps[0];
        for (int i = 1; i < maps.length; i++)
            if (maps[i] != null) mainMap.putAll(maps[i]);
        return mainMap;
    }


    public static <E, V, K, M extends Map> Map<K, V> transformToHashMap(Class<M> mapClass, Collection<E> inputCollection, Function<Object, ?> keyTransformer, Function<Object, ?> valueTransformer) {
        Map<K, V> targetMap = null;
        try {
            targetMap = mapClass == null ? new HashMap<>() : (Map<K, V>) mapClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
        transformIntoTargetMap(inputCollection, targetMap, keyTransformer, valueTransformer);
        return targetMap;
    }

    @SuppressWarnings("unchecked")
    public static <E, V, K> void transformIntoTargetMap(Collection<E> inputCollection, Map<K, V> targetMap, Function<Object, ?> keyTransformer, Function<Object, ?> valueTransformer) {
        for (E obj : inputCollection)
            targetMap.put((K) keyTransformer.apply(obj), (V) valueTransformer.apply(obj));
    }

    public static Properties properties(Object... objects) {
        return (Properties) populateMap(new Properties(), objects);
    }

    public static boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }

    public static boolean isEmpty(Object object) {
        if (object == null) return true;
        if (object instanceof Map<?, ?>) return ((Map<?, ?>) object).isEmpty();
        if (object instanceof Collection<?>) return ((Collection<?>) object).isEmpty();
        return false;
    }

    public static <O> O firstNonNullValue(Map<?, O> map) {
        return firstNotNullValue(map);
    }

    public static <O> O firstNotNullValue(Map<?, O> map) {
        for (O obj : map.values()) {
            if (obj != null) return obj;
        }
        return null;
    }

    public static <O> O firstNonNullValue(Collection<O> collection) {
        return firstNotNullValue(collection);
    }

    public static <O> O firstNotNullValue(Collection<O> collection) {
        for (O obj : collection) {
            if (obj != null) return obj;
        }
        return null;
    }

    public static <E> List<E> sort(List<E> inputList, final Object propertyObject) {
        return sort(inputList, propertyObject, true);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <E> List<E> sort(List<E> inputList, final Object propertyName, final boolean ascending) {
        inputList.sort((o1, o2) -> {
            try {
                Comparable val1, val2;
                if (o1 instanceof UtilPojo) {
                    val1 = (Comparable) o1.getClass().getMethod(propertyName.toString()).invoke(o1);
                    val2 = (Comparable) o2.getClass().getMethod(propertyName.toString()).invoke(o2);
                } else if (o1 instanceof Map) {
                    val1 = (Comparable) ((Map) o1).get(propertyName);
                    val2 = (Comparable) ((Map) o2).get(propertyName);
                } else {
                    val1 = (Comparable) getProperty(o1, propertyName.toString());
                    val2 = (Comparable) getProperty(o2, propertyName.toString());
                }
                if (val1 == null && val2 == null) return 0;
                if (val1 == null) return ascending ? 1 : -1;
                if (val2 == null) return ascending ? -1 : 1;
                return (ascending ? 1 : -1) * val1.compareTo(val2);
            } catch (Exception e) {
                return 0;
            }
        });
        return inputList;
    }

    public static <E> List<E> sort(List<E> inputList, final String propertyName) {
        return sort(inputList, propertyName, true);
    }

    public static <E> List<E> sortDescending(List<E> inputList, final Object propertyObject) {
        return sort(inputList, propertyObject, false);
    }

    public static <E> List<E> sortDescending(List<E> inputList, final String propertyName) {
        return sort(inputList, propertyName, false);
    }

    public static <E> List<E> sortByMethod(List<E> inputList, final String methodName) {
        return sortByMethod(inputList, methodName, true);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <E> List<E> sortByMethod(List<E> inputList, final String methodName, final boolean ascending) {
        inputList.sort((o1, o2) -> {
            try {
                Comparable val1, val2;
                val1 = (Comparable) o1.getClass().getMethod(methodName).invoke(o1);
                val2 = (Comparable) o2.getClass().getMethod(methodName).invoke(o2);
                return ((ascending) ? 1 : -1) * val1.compareTo(val2);
            } catch (Exception e) {
                return 0;
            }
        });
        return inputList;
    }

    public static <E> List<E> sortByMethodDescending(List<E> inputList, final String methodName) {
        return sortByMethod(inputList, methodName, false);
    }

    public static <E> Collection<E> addAll(Collection<E> base, Iterable<E> anotherCollection) {
        if (base == null) return null;
        for (E obj : anotherCollection)
            base.add(obj);
        return base;
    }

    public static <E> Map<?, List<E>> getHashMapFromListGroupedByProperty(Collection<E> inputCollection, final String propertyName) {
        if (inputCollection == null) return null;
        Map<Object, List<E>> groupedMap = new HashMap<>();

        for (E obj : inputCollection)
            try {
                @SuppressWarnings("rawtypes")
                Object key = (obj instanceof Map) ? ((Map) obj).get(propertyName) : getProperty(obj, propertyName);
                List<E> value = groupedMap.computeIfAbsent(key, k -> new ArrayList<>());
                value.add(obj);
            } catch (Exception e) {
                return null;
            }
        return groupedMap;
    }

    public static <E> Map<?, List<E>> getLinkedHashMapFromListGroupedByProperty(Collection<E> inputCollection, final String propertyName) {// TODO need to see if this can be clubbed with HashMap method
        if (inputCollection == null) return null;
        Map<Object, List<E>> groupedMap = new LinkedHashMap<>();
        for (E obj : inputCollection)
            try {
                @SuppressWarnings("rawtypes")
                Object key = (obj instanceof Map) ? ((Map) obj).get(propertyName) : getProperty(obj, propertyName);
                List<E> value = groupedMap.computeIfAbsent(key, k -> new ArrayList<>());
                value.add(obj);
            } catch (Exception e) {
                return null;
            }
        return groupedMap;
    }

    public static <K, V> Map<?, ?> transform(Map<K, V> inputMap, Function<Object, ?> keyTransformer, Function<Object, ?> valueTransformer) {

        if (inputMap == null) return null;
        Map<Object, Object> returnMap = new HashMap<>();
        for (Entry<K, V> entry : inputMap.entrySet()) {
            returnMap.put(keyTransformer == null ? entry.getKey() : keyTransformer.apply(entry.getKey()), valueTransformer == null ? entry.getValue() : valueTransformer.apply(entry.getValue()));
        }
        return returnMap;
    }

    public static <K, V> Map<?, ?> transformValues(Map<K, V> inputMap, Function<Object, ?> valueTransformer) {
        return transform(inputMap, null, valueTransformer);
    }

    public static <E, L extends Collection<E>> Collection<?> collect(L inputCollection, String propertyName) {
        return collect(inputCollection, propertyName, null);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <E, R> Collection<?> collect(Collection<E> inputCollection, String propertyName, Class<R> collectAsClass) {
        if (inputCollection == null) return null;
        Collection<Object> outputCollection = null;
        try {
            outputCollection = inputCollection.getClass().newInstance();
            for (Object obj : inputCollection) {
                Object propertyValue = (obj instanceof Map) ? ((Map) obj).get(propertyName) : getProperty(obj, propertyName);
                if (collectAsClass == null) outputCollection.add(propertyValue);
                else outputCollection.add(convert(propertyValue, collectAsClass));
            }
        } catch (Exception e) {
            return null;
        }
        return outputCollection;
    }

    @SuppressWarnings("unchecked")
    public static <E> Collection<E> downcastCollection(Collection<? extends E> inputCollection, Class<E> targetClass) {
        if (inputCollection == null) return null;
        Collection<E> outputCollection = null;
        try {
            outputCollection = inputCollection.getClass().newInstance();
            Constructor<E> constructor = targetClass.getConstructor();
            for (Object orig : inputCollection) {
                E dest = constructor.newInstance();
                copyProperties(orig, dest);
                outputCollection.add(dest);
            }
        } catch (Exception e) {
            return null;
        }
        return outputCollection;
    }

    public static <K, V> Map<K, V> filteredMap(Map<K, V> inputMap, Collection<K> keys) {
        if (isEmpty(keys)) return new HashMap<>(1);
        Map<K, V> filteredMap = new HashMap<>(keys.size());
        for (K key : keys) {
            V value = inputMap.get(key);
            if (value != null) filteredMap.put(key, value);
        }
        return filteredMap;
    }

    public static <E> Collection<E> filterCollectionByProperty(Collection<E> inputCollection, final String propertyName, final Collection<?> values) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        List<E> returnCollection = new ArrayList<>();
        for (E e : inputCollection)
            if (values.contains(getProperty(e, propertyName))) returnCollection.add(e);

        return returnCollection;
    }

    public static List<String> castObjectToList(Object obj) {
        if (isEmpty(obj))
            return null;
        if (obj instanceof String[])
            return Arrays.asList((String[]) obj);
        return (List<String>) obj;
    }

    // Thread Safe Singleton Method Implementation
    private static class _instance {
        public static final CollectionUtil instance = new CollectionUtil();
    }

}
