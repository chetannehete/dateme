package com.cn.spring.util;

import com.cn.spring.basespring.ApplicationContextProvider;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class ConversionUtil {
    private ObjectMapper jsonObjectMapper;
    private ObjectMapper optimizedMapper;
    private ConversionService conversionService;
    
    @Autowired
    private ApplicationContextProvider appContext;

    private ConversionUtil() {
        try {
            jsonObjectMapper = new ObjectMapper();
            jsonObjectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            optimizedMapper = new ObjectMapper();
            optimizedMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            optimizedMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            SimpleModule module = new SimpleModule();
//            module.addSerializer(Exception.class, new ExceptionSerializer());
//            module.addSerializer(HttpServletRequest.class, new CoreRequestSerializer());
            jsonObjectMapper.registerModule(module);
            SimpleModule optimizedModule = new SimpleModule();
//            optimizedModule.addSerializer(NativeClientMessage.class, new NativeClientMessageSerializer());
            optimizedMapper.registerModule(optimizedModule);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T, I> Collection<T> convert(Collection<I> inputCollection, Class<T> collectAsClass) {
        if (inputCollection == null) return null;
        Collection<T> outputCollection = null;
        try {
            outputCollection = _getNewCollectionInstance(inputCollection, collectAsClass);
            for (I obj : inputCollection) {
                outputCollection.add(convert(obj, collectAsClass));
            }
        } catch (Exception e) {
            return null;
        }
        return outputCollection;
    }

    @SuppressWarnings("unchecked")
    private static <T, I> Collection<T> _getNewCollectionInstance(Collection<I> inputCollection, Class<T> collectAsClass) {
        try {
            return inputCollection.getClass().newInstance();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T, S> T convert(S source, Class<T> targetClassName) {
        if (source == null) return null;
        if (targetClassName.isAssignableFrom(Boolean.class))
            return (T) parseBoolean(source); // performance optimizations
        ConversionService cs = getInstance().getConversionService();
        return cs.convert(source, targetClassName);
    }

    public static <O> Boolean parseBoolean(O obj) {
        return StringUtil.parseBoolean(obj);
    }

    // Thread Safe Singleton getInstance() Method Implementation
    private static ConversionUtil getInstance() {
        return _instance.instance;
    }

    public static <T> String jsonString(T object) {
        return jsonString(object, false);
    }

    public static <T> String jsonString(T object, boolean prettyPrint) {
        if (object == null) return null;
        try {
            ObjectMapper mapper = getInstance().jsonObjectMapper;
            if (prettyPrint) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                ObjectWriter objectWriter = mapper.writerWithDefaultPrettyPrinter();
                objectWriter.writeValue(outStream, object);
                return outStream.toString();
            } else {
                StringWriter sw = new StringWriter();
                mapper.writeValue(sw, object);
                return sw.toString();
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not convert object to json string" + string(object));
        }
    }
    
    public static <T> T mapToObject(Object map,  Class<T> reference) {
        return (T) getInstance().jsonObjectMapper.convertValue(map, reference);
    }

    
    private static String string(Object object) {
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

    public static <T> String jsonString(T object, int trimSize) {
        String jsonString = jsonString(object, false);
        if (jsonString == null) return null;
        if (jsonString.isEmpty()) return jsonString;
        return jsonString.substring(0, Math.min(trimSize, jsonString.length()) - 1);
    }


    @SuppressWarnings("unchecked")
    public static <K> K defaultValue(Object obj, K defaultValue) {
        return (obj == null) ? defaultValue : (K) obj;
    }

    @SuppressWarnings("unchecked")
    public static <K> K readJson(String stringObj, String className) throws Exception {
        return (K) readJson(stringObj, Class.forName(className));
    }

    public static <K> K readJson(String stringObj, Class<K> className) {
        try {
            return getInstance().jsonObjectMapper.readValue(stringObj, className);
        } catch (Exception e) {
            throw new RuntimeException("Could not read json : " + stringObj);
        }
    }
    @SuppressWarnings("unchecked")
    public static <T> T readJson(String stringObj, TypeReference<?> typeReference) {
        try {
            return (T) getInstance().jsonObjectMapper.readValue(stringObj, typeReference);
        } catch (Exception e) {
            throw new RuntimeException("Could not read json : " + stringObj);
        }
    }

    // be very careful about using this api; it cannot convert dates to proper format
    public static <O, T> T javaObject(O object, Class<T> className) {
        if (object == null) return null;
        return JsonIterator.deserialize(JsonStream.serialize(object), className);
    }

    public static <O, K> Map<K, Object> javaMap(O object) {
        if (object == null) return null;
        Map<K, Object> dateObjects = new HashMap<>();
        if (object instanceof Map) {
            Map<Object, Object> inputMap = (Map<Object, Object>) object;
            for (Map.Entry<Object, Object> entry : inputMap.entrySet()) {
                Object value = entry.getValue();
                if (value == null || !value.getClass().isAssignableFrom(Date.class)) continue;
                dateObjects.put((K) entry.getKey(), value);
            }
            inputMap.keySet().removeAll(dateObjects.keySet());
        }
        Map<K, Object> outputMap = JsonIterator.deserialize(JsonStream.serialize(object), Map.class);
        if (!dateObjects.isEmpty()) outputMap.putAll(dateObjects);
        return outputMap;
    }

    public static <O, K> List<Map<K, Object>> javaMaps(List<O> objects) {
        if (objects == null) return null;
        List<Map<K, Object>> returnList = new ArrayList<>();
        for (O object : objects) {
            returnList.add(javaMap(object));
        }
        return returnList;
    }

    
    public ConversionService getConversionService() {
        if (this.conversionService != null) return this.conversionService;
        this.conversionService = appContext.getApplicationContext().getBean(ConversionService.class);
        return this.conversionService;
    }
    public static String jstring(Object object) {
        return JsonStream.serialize(object);
    }
    // Thread Safe Singleton Method Implementation
    private static class _instance {
        public static final ConversionUtil instance = new ConversionUtil();
    }

}
