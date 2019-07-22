package com.cn.spring.util;

import static com.cn.spring.util.StringUtil.isBlank;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.ClassUtils.isPrimitiveOrWrapper;
import static org.springframework.beans.BeanUtils.copyProperties;
import static org.springframework.beans.BeanUtils.getPropertyDescriptor;
import static org.springframework.beans.BeanUtils.getPropertyDescriptors;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.cn.spring.model.pojo.UtilPojo;


public class ObjectUtil {
    @SuppressWarnings({"rawtypes"})
    private static final Set<Class> _sudoPrimitives = new HashSet<>(Arrays.asList(Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Void.class, String.class));
    @SuppressWarnings("rawtypes")
    private static final Class[] _sudoPrimitivesExtensions = new Class[]{Date.class};
    private static final List<String> validImmutableMethodPrefixes = asList("get", "is", "key", "value", "entry", "iterator", "property", "containskey", "size");

    private ObjectUtil() {
    }

    // Thread Safe Singleton getInstance() Method Implementation
    public static ObjectUtil getInstance() {
        return _instance.instance;
    }

    public static <T, S extends T> T downCast(S srcObject, Class<T> targetClass) throws Exception {
        T targetObject = targetClass.newInstance();
        copyProperties(srcObject, targetObject);
        return targetObject;
    }

    @SuppressWarnings("unchecked")
    public static <O> O cloneResource(O srcObject) throws Exception {
        try {
            if (srcObject == null) return null;
            return (O) srcObject.getClass().getMethod("clone").invoke(srcObject);
        } catch (Exception e) {
            O targetObject = (O) srcObject.getClass().newInstance();
            copyProperties(srcObject, targetObject);
            return targetObject;
        }
    }

    public static <S, T extends S> T cast(S srcObject, Class<T> targetClass, Object... propertyPairs) throws Exception {
        T targetObject = targetClass.newInstance();
        copyProperties(srcObject, targetObject);
        int properyPairLen = propertyPairs.length;
        if (properyPairLen == 0) return targetObject;
        if (properyPairLen % 2 != 0) throw new Exception("Should pass properties as pairs");
        for (int i = 0; i < properyPairLen; ) {
            PropertyDescriptor descriptor = getPropertyDescriptor(targetClass, propertyPairs[i++].toString());
            descriptor.getWriteMethod().invoke(targetObject, propertyPairs[i++]);
        }

        return targetObject;
    }

    @SuppressWarnings("unchecked")
    public static <E, T> Collection<T> cast(Collection<E> inputCollection, Class<T> targetClass) throws Exception {
        if (inputCollection == null) return null;
        Collection<T> outputCollection = inputCollection.getClass().newInstance();
        for (E object : inputCollection) {
            T targetObject = targetClass.newInstance();
            copyProperties(object, targetObject);
            outputCollection.add(targetObject);
        }
        return outputCollection;
    }

    @SuppressWarnings("unchecked")
    public static <T, S> T extend(T object, Object... optionalPropertyPairs) throws Exception {
        int properyPairLen = optionalPropertyPairs.length;
        if (properyPairLen == 0) return object;
        if (properyPairLen % 2 != 0) throw new Exception("Should pass properties as pairs");
        Class<T> targetClass = (Class<T>) object.getClass();
        for (int i = 0; i < properyPairLen; ) {
            PropertyDescriptor descriptor = getPropertyDescriptor(targetClass, optionalPropertyPairs[i++].toString());
            descriptor.getWriteMethod().invoke(object, optionalPropertyPairs[i++]);
        }
        return object;
    }

    public static <T, S> T mergeInto(T targetObject, S srcObject, Object... optionalPropertyPairs) throws Exception {
        return merge(targetObject, srcObject, optionalPropertyPairs);
    }

    @SuppressWarnings("unchecked")
    public static <T, S> T merge(T targetObject, S srcObject, Object... optionalPropertyPairs) throws Exception {
        mergeInstance(targetObject, null, srcObject);
        int properyPairLen = optionalPropertyPairs.length;
        if (properyPairLen == 0) return targetObject;
        if (properyPairLen % 2 != 0) throw new Exception("Should pass properties as pairs");
        Class<T> targetClass = (Class<T>) targetObject.getClass();
        for (int i = 0; i < properyPairLen; ) {
            PropertyDescriptor descriptor = getPropertyDescriptor(targetClass, optionalPropertyPairs[i++].toString());
            descriptor.getWriteMethod().invoke(targetObject, optionalPropertyPairs[i++]);
        }
        return targetObject;
    }

    private static <T, S> T mergeInstance(T targetObject, Class<T> targetClass, S sourceObject) {
        PropertyDescriptor[] descriptors = getPropertyDescriptors(targetClass == null ? targetObject.getClass() : targetClass);
        for (PropertyDescriptor descriptor : descriptors) {
            Method writer = descriptor.getWriteMethod();
            if (writer == null) continue;
            Class<?>[] paramTypes = writer.getParameterTypes();
            if (paramTypes == null || paramTypes.length != 1) continue;
            Class<?> paramType = paramTypes[0];
            String propertyName = descriptor.getName();
            Object value = null;
            try {
                PropertyDescriptor rdescriptor = BeanUtils.getPropertyDescriptor(sourceObject.getClass(), propertyName);
                Method reader = rdescriptor.getReadMethod();
                if (reader != null) {
                    value = reader.invoke(sourceObject);
                }
            } catch (Exception ignored) {
            }
            if (value != null) {
                if (paramType.isAssignableFrom(Timestamp.class) && !(value instanceof Timestamp)) {
                    value = new Timestamp(Long.parseLong(String.valueOf(value)));
                } else if (paramType.isAssignableFrom(Date.class) && !(value instanceof Date)) {
                    value = new Date(Long.parseLong(String.valueOf(value)));
                }
            } else {
                if (paramType.isPrimitive()) continue;
            }
            try {
                writer.invoke(targetObject, value);
            } catch (Exception ignored) {
            }
        }
        return targetObject;
    }

    public static <E> E objectInstance(Class<E> targetClass, Map<String, ?> properties) throws Exception {
        E targetObject = targetClass.newInstance();
        PropertyDescriptor[] descriptors = getPropertyDescriptors(targetClass);
        for (PropertyDescriptor descriptor : descriptors) {
            Method writer = descriptor.getWriteMethod();
            if (writer == null) continue;
            Class<?>[] paramTypes = writer.getParameterTypes();
            if (paramTypes == null || paramTypes.length != 1) continue;
            Class<?> paramType = paramTypes[0];
            String propertyName = descriptor.getName();
            if (!properties.containsKey(propertyName)) continue;
            Object value = properties.get(propertyName);
            if (value != null) {
                if (paramType.isAssignableFrom(Timestamp.class) && !(value instanceof Timestamp)) {
                    value = new Timestamp(Long.parseLong(String.valueOf(value)));
                } else if (paramType.isAssignableFrom(Date.class) && !(value instanceof Date)) {
                    value = new Date(Long.parseLong(String.valueOf(value)));
                } else if (paramType.equals(boolean.class) || paramType.isAssignableFrom(Boolean.class)) {
                    value = StringUtil.parseBoolean(value);
                }
            } else {
                if (paramType.isPrimitive()) continue;
            }
            try {
                writer.invoke(targetObject, value);
            } catch (Exception ignored) {
            }
        }
        return targetObject;
    }

    public static <T, S> T objectInstance(Class<T> targetClass, S sourceObject) throws Exception {
        T targetObject = targetClass.newInstance();
        return mergeInstance(targetObject, targetClass, sourceObject);
    }

    public static <T, S> T primitiveMerge(T targetObject, S sourceObject) throws Exception {
        if (targetObject == null) return null;
        if (sourceObject == null) return targetObject;
        @SuppressWarnings("unchecked")
        Class<S> sourceClass = (Class<S>) sourceObject.getClass();
        for (Method setter : targetObject.getClass().getMethods()) {
            if (!Modifier.isPublic(setter.getModifiers())) continue;
            if (!setter.getName().startsWith("set") || setter.getParameterTypes().length != 1) continue;
            String fieldName = setter.getName().substring(3); // will have field name with caps
            Method getter = sourceClass.getMethod("get" + fieldName);
            getter = sourceClass.getMethod("get" + fieldName);
            if (getter == null) getter = sourceClass.getMethod("is" + fieldName);
            if (getter == null || getter.getParameterTypes().length != 0) continue;
            try {
                setter.invoke(targetObject, getter.invoke(sourceObject));
            } catch (Exception ignored) {

            }
        }
        return targetObject;
    }

    @SuppressWarnings("unchecked")
    public static <K> K defaultValue(Object obj, K defaultValue) {
        try {
            return (obj == null) ? defaultValue : (K) obj;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @SuppressWarnings("rawtypes")
    public static Object getProperty(Object object, String propertyName) {
        if (object == null) return null;
        try {
            if (object instanceof Map) return ((Map) object).get(propertyName);
            if (object instanceof UtilPojo) return object.getClass().getMethod(propertyName).invoke(object);
            PropertyDescriptor descriptor = getPropertyDescriptor(object.getClass(), propertyName);
            return descriptor.getReadMethod().invoke(object);
        } catch (Exception e) {
            return null;
        }
    }

    public static void setProperties(String propertyIdPrefix, Map<Object, Object> propertyConfigMap, Object object) {
        if (propertyConfigMap == null || propertyConfigMap.isEmpty()) return;
        for (Map.Entry<Object, Object> entry : propertyConfigMap.entrySet()) {
            String propertyValue = entry.getValue().toString();
            if (isBlank(propertyValue)) continue;
            String configId = entry.getKey().toString();
            if (!configId.startsWith(propertyIdPrefix)) continue;
            String translatedConfigId = configId.substring(propertyIdPrefix.length());
            try {
                setProperty(object, translatedConfigId, propertyValue);
            } catch (Exception ignored) {
            }
        }
    }

    public static void setProperty(Object object, String propertyName, Object propertyValue) {
        if (object == null || propertyValue == null) return;
        try {
            PropertyDescriptor descriptor = getPropertyDescriptor(object.getClass(), propertyName);
            Method method = descriptor.getWriteMethod();
            Class<?>[] paramTypes = method.getParameterTypes();
            if (paramTypes.length != 1) return;
            if (isPrimitiveOrWrapper(paramTypes[0])) {
                propertyValue = convert(paramTypes[0], propertyValue.toString());
            }
            method.invoke(object, propertyValue);
        } catch (Exception ignored) {
        }
    }

    private static Object convert(Class<?> targetType, String text) {
        PropertyEditor editor = PropertyEditorManager.findEditor(targetType);
        editor.setAsText(text);
        return editor.getValue();
    }

    public static byte[] encode(ByteBuffer buffer, Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(object);
            return bos.toByteArray();
        }
    }

    public static Map<String, Object> objectToValueMap(Object object) {
        return objectToValueMap(object, false);
    }

    public static Map<String, Object> objectToValueMap(Object object, boolean considerOnlyPrimitiveValues) {
        if (object == null) return null;
        Map<String, Object> map = new HashMap<>();
        try {
            Method[] methods = object.getClass().getMethods();
            for (Method m : methods) {
                String methodName = m.getName();
                String propertyName = null;
                if (methodName.startsWith("get")) {
                    propertyName = methodName.substring(3, 4).toLowerCase();
                    if (methodName.length() > 4) propertyName += methodName.substring(4);
                } else if (methodName.startsWith("is")) {
                    propertyName = methodName.substring(2, 3).toLowerCase();
                    if (methodName.length() > 3) propertyName += methodName.substring(3);
                }
                if (propertyName == null) continue;
                Object value = m.invoke(object);
                if (considerOnlyPrimitiveValues && !isPrimitive(value)) continue;
                map.put(propertyName, value);
            }
        } catch (Exception e) {
            return null;
        }
        return map.isEmpty() ? null : map;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean isPrimitive(Object object) {
        if (object == null) return true;
        Class objectClass = object.getClass();
        if (objectClass.isPrimitive()) return true;
        if (_sudoPrimitives.contains(objectClass)) return true;
        for (Class cClass : _sudoPrimitivesExtensions) {
            if (objectClass.isAssignableFrom(cClass)) return true;
        }
        return false;
    }

    public static Map<String, Object> objectToPrimitiveMap(Object object) {
        return objectToValueMap(object, true);
    }

    public static void close(Closeable... objects) {
        if (objects == null || objects.length == 0) return;
        for (Closeable obj : objects) {
            try {
                obj.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static List<String> getObjectAttributeNames(final Object object) {
        List<String> attributeNames = new ArrayList<>();
        Field[] totalFields = object.getClass().getDeclaredFields();
        for (Field field : totalFields) {
            attributeNames.add(field.getName());
        }
        return attributeNames;
    }

    @SuppressWarnings("rawtypes")
    public static String string(Object object) {
        if (object == null) return "null";
        StringBuilder sb = new StringBuilder();
        if (object.getClass().isArray()) {
            for (Object obj : (Object[]) object) {
                sb.append(obj).append(",");
            }
            return sb.toString();
        }
        if (object instanceof Collection) {
            for (Object obj : (Collection) object) {
                sb.append(obj).append(",");
            }
            return sb.toString();
        }
        return String.valueOf(object);
    }

    // Thread Safe Singleton Method Implementation
    private static class _instance {
        public static final ObjectUtil instance = new ObjectUtil();
    }

}
