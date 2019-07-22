package com.cn.spring.util;

import static java.util.Collections.singletonList;
import static org.springframework.beans.BeanUtils.getPropertyDescriptor;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;


public class StringUtil {

    public static final String EMPTY_STRING = "";
    public static final String MONGO_SEPERATOR = "-";
    public static final char DEFAULT_SEPERATOR = ',';
    public static final String DEFAULT_SEPERATOR_STR = ",";
    public static final String DEFAULT_LEFT_WRAPPER_STR = "[";
    public static final String DEFAULT_RIGHT_WRAPPER_STR = "]";
    public static final String SQL_WRAPPER_STR = "'";
    public static final String PACKAGE_SEPERATOR_STR = FileUtil.PACKAGE_SEPERATOR_STR;
    public static final String NULL_OBJECT_REPRESENTATION = "null";
    public static final String SPACE_STR = " ";
    public static final Character UNDERSCORE_CHAR = '_';
    public static final char JSON_MAP_IDENTIFIER = '{';
    public static final char JSON_ARRAY_IDENTIFIER = '[';
    public static final String FILE_SEPERATOR = FileUtil.FILE_SEPERATOR;
    public static final String URL_SEPERATOR = FileUtil.URL_SEPERATOR;
    public static final char URL_SEPERATOR_CHAR = FileUtil.URL_SEPERATOR_CHAR;
    public static final String EXTENSION_SEPERATOR = FileUtil.EXTENSION_SEPERATOR;
    public static final String LOG_DECORATOR_TYPE1 = multiply('*', 150);
    public static final String LOG_DECORATOR_TYPE2 = multiply('-', 150);
    public static final String HASH_SEPERATOR = "#";
    private static final Exception _localeExceptionHolder = new Exception("Nothing important!!");
    private static final Map<String, Locale> _performanceLocaleStringCache = localeMap(split("en_US,cs_CZ,de_DE,en_GB,es_ES,es_MX,fr_CA,fr_FR,hu_HU,it_IT,nl_NL,pl_PL,pt_BR,ru_RU,sk_SK,sv_SE"));

    private StringUtil() {
    }

    // Thread Safe Singleton getInstance() Method Implementation
    public static StringUtil getInstance() {
        return _instance.instance;
    }

    public static String strJoinUsingProperty(Collection<?> inputCollection, final String propertyName) {
        return strJoinUsingProperty(inputCollection, propertyName, DEFAULT_SEPERATOR_STR);
    }

    public static String strJoinUsingProperty(Collection<?> inputCollection, final String propertyName, String separator) {
        if (inputCollection == null || inputCollection.isEmpty()) return EMPTY_STRING;
        StringBuilder sb = new StringBuilder();
        for (Object obj : inputCollection)
            try {
                PropertyDescriptor descriptor = getPropertyDescriptor(obj.getClass(), propertyName);
                sb.append(separator).append(descriptor.getReadMethod().invoke(obj));
            } catch (Exception e) {
                return null;
            }
        return sb.substring(1);
    }

    public static String emptyStringAsNull(String input) {
        return isEmpty(input) ? null : input;
    }

    public static boolean isEmpty(String input) {
        return StringUtils.isEmpty(input);
    }

    public static String blankStringAsEmpty(Object input) {
        return input == null ? EMPTY_STRING : blankStringAsEmpty(input.toString());
    }

    public static String blankStringAsEmpty(String input) {
        return isEmpty(input) ? EMPTY_STRING : input;
    }

    public static String defaultString(String input, String... defaults) {
        return (input == null) ? ((defaults.length == 1) ? defaults[0] : EMPTY_STRING) : input;
    }

    public static boolean isNotEmpty(String input) {
        return !isEmpty(input);
    }

    public static boolean isNotBlank(String input) {
        return !isBlank(input);
    }

    public static boolean isBlank(String input) {
        return (input == null || input.length() == 0 || input.trim().length() == 0);
    }

    public static boolean isNotBlank(Object input) {
        return !isBlank(input);
    }

    public static boolean isBlank(Object input) {
        return (input == null || input.toString().length() == 0 || input.toString().trim().length() == 0);
    }

    public static boolean isAnyofThemBlank(String... inputs) {
        for (String input : inputs)
            if (isBlank(input)) return true;
        return false;
    }

    public static List<String> split(String input) {
        return (input == null) ? null : Arrays.asList(input.split(DEFAULT_SEPERATOR_STR));
    }

    public static List<String> split(Object input) {
        return (input == null) ? new ArrayList<>(0) : Arrays.asList(input.toString().split(DEFAULT_SEPERATOR_STR));
    }

    public static List<String> splitBySpace(String input) {
        return (input == null) ? null : Arrays.asList(input.trim().split("([\\t| ])+"));
    }

    public static List<Integer> splitInts(String input) {
        if (isBlank(input)) return new ArrayList<>(0);
        List<Integer> returnList = new ArrayList<>();
        for (String value : input.split(DEFAULT_SEPERATOR_STR)) {
            Integer x = null;
            try {
                x = Integer.parseInt(value.trim());
            } catch (RuntimeException e) {
                continue;
            }
            returnList.add(x);
        }
        return returnList;
    }

    public static String variableNameString(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        StringBuilder buffer = new StringBuilder(strLen);
        buffer.append(Character.toLowerCase(str.charAt(0)));
        boolean whitespace = false;
        for (int i = 1; i < strLen; i++) {
            char ch = str.charAt(i);
            if (Character.isWhitespace(ch) || ch == UNDERSCORE_CHAR) {
                whitespace = true;
            } else if (whitespace) {
                buffer.append(Character.toTitleCase(ch));
                whitespace = false;
            } else {
                buffer.append(Character.toLowerCase(ch));
            }
        }
        return buffer.toString();
    }

    public static String columnNameString(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        StringBuilder buffer = new StringBuilder(strLen);
        buffer.append(Character.toUpperCase(str.charAt(0)));
        for (int i = 1; i < strLen; i++) {
            char ch = str.charAt(i);
            if (Character.isUpperCase(ch)) buffer.append(UNDERSCORE_CHAR);
            else if (ch == UNDERSCORE_CHAR) continue;
            buffer.append(Character.toUpperCase(ch));
        }
        return buffer.toString();
    }

    public static String strJoin(String... strings) {
        return strJoinCollection(Arrays.asList(strings), DEFAULT_SEPERATOR_STR, EMPTY_STRING);
    }

    public static String strJoinCollection(Collection<?> inputCollection, String separator, String Wrapper) {
        return strJoinCollection(inputCollection, separator, Wrapper, Wrapper);
    }

    public static String strJoinCollection(Collection<?> inputCollection, String separator, String leftWrapper, String rightWrapper) {
        if (inputCollection == null || inputCollection.isEmpty()) return EMPTY_STRING;
        return strJoinIterable(inputCollection, separator, leftWrapper, rightWrapper);
    }

    public static String strJoinIterable(Iterable<?> inputCollection, String separator, String leftWrapper, String rightWrapper) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : inputCollection)
            try {
                sb.append(separator).append(leftWrapper).append(obj == null ? NULL_OBJECT_REPRESENTATION : obj.toString()).append(rightWrapper);
            } catch (Exception e) {
                return null;
            }
        return sb.substring(1);
    }

    @SuppressWarnings("unchecked")
    public static String strJoin(Object... objects) {
        if (objects.length == 1)
            return strJoinCollection((Collection<Object>) objects[0], DEFAULT_SEPERATOR_STR, EMPTY_STRING);
        return strJoinCollection(Arrays.asList(objects), DEFAULT_SEPERATOR_STR, EMPTY_STRING);
    }

    @SuppressWarnings("unchecked")
    public static String sqlStrJoin(Object... objects) {
        if (objects.length == 1)
            return strJoinCollection((Collection<Object>) objects[0], DEFAULT_SEPERATOR_STR, SQL_WRAPPER_STR);
        return strJoinCollection(Arrays.asList(objects), DEFAULT_SEPERATOR_STR, SQL_WRAPPER_STR);
    }

    public static String cacheKey(Object... objects) {
        return packageJoin(objects);
    }

    @SuppressWarnings("unchecked")
    public static String packageJoin(Object... objects) {
        if (objects.length == 1)
            return strJoinCollection((Collection<Object>) objects[0], PACKAGE_SEPERATOR_STR, EMPTY_STRING);
        return strJoinCollection(Arrays.asList(objects), PACKAGE_SEPERATOR_STR, EMPTY_STRING);
    }

    public static String strJoinCollection(Collection<?> collection, String separator) {
        return strJoinCollection(collection, separator, EMPTY_STRING, EMPTY_STRING);
    }

    public static String strJoinCollection(Object[] inputArray, String separator, String Wrapper) {
        return strJoinCollection(Arrays.asList(inputArray), separator, Wrapper);
    }

    public static String strJoinIterable(Iterable<?> inputCollection, String separator, String Wrapper) {
        return strJoinIterable(inputCollection, separator, Wrapper, Wrapper);
    }

    public static String stringPair(Object... objects) {
        try {
            if (objects.length == 1) return ConversionUtil.jsonString(objects[0]);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < objects.length; ) {
                Object key = objects[i++];
                Object value = objects[i++];
                String valueString = null;
                if (value == null) valueString = NULL_OBJECT_REPRESENTATION;
                else if (value instanceof Object[])
                    valueString = wrappedMsg(true, DEFAULT_SEPERATOR_STR, (Object[]) value);
                else valueString = value.toString();
                sb.append(",\"").append(key == null ? NULL_OBJECT_REPRESENTATION : key.toString()).append("\":").append(valueString);
            }
            return sb.substring(1);
        } catch (Exception e) {
            return null;
        }
    }

    public static String wrappedMsg(boolean wrapFromStart, String separator, Object... objects) {
        StringBuilder sb = wrapFromStart ? new StringBuilder() : new StringBuilder(objects[0].toString());
        sb.append(DEFAULT_LEFT_WRAPPER_STR);
        for (int i = wrapFromStart ? 0 : 1; i < objects.length; i++)
            sb.append(objects[i] == null ? NULL_OBJECT_REPRESENTATION : objects[i].toString()).append(separator);
        return sb.substring(0, sb.length() - separator.length()) + DEFAULT_RIGHT_WRAPPER_STR;
    }

    public static String wrappedMsg(String separator, Object... objects) {
        return wrappedMsg(false, separator, objects);
    }

    /**
     * wrapper parseLong method which can accept option defaultValue as second argument
     *
     * @param string       inputString
     * @param defaultValue ( optional )
     * @return parsed Long value
     */
    public static Long parseLong(String string, Long... defaultValue) {
        try {
            return Long.parseLong(string);
        } catch (RuntimeException e) {
            if (defaultValue.length > 0) return defaultValue[0];
            throw e;
        }
    }

    public static Long parseLong(Object object, Long... defaultValue) {
        try {
            if (object instanceof Long) return (Long) object;
            return Long.parseLong(object.toString());
        } catch (RuntimeException e) {
            if (defaultValue.length > 0) return defaultValue[0];
            throw e;
        }
    }

    /**
     * wrapper parseInt method which can accept option defaultValue as second argument
     *
     * @param string       inputString
     * @param defaultValue ( optional )
     * @return parsed Integer value
     */
    public static Integer parseInt(String string, Integer... defaultValue) {
        try {
            return Integer.parseInt(string);
        } catch (RuntimeException e) {
            if (defaultValue.length > 0) return defaultValue[0];
            throw e;
        }
    }

    public static Integer parseInt(Object object, Integer... defaultValue) {
        try {
            if (object instanceof Integer) return (Integer) object;
            if (object instanceof Double) return ((Double) object).intValue();
            return Integer.parseInt(object.toString());
        } catch (RuntimeException e) {
            if (defaultValue.length > 0) return defaultValue[0];
            throw e;
        }
    }

    public static <O> boolean parseBoolean(O obj) {
        return parseBoolean(obj, false);
    }

    public static <O> boolean parseBoolean(O obj, boolean defaultValue) {
        if (obj == null) return defaultValue;
        if (obj instanceof Boolean) return (Boolean) obj;
        return ("1".equalsIgnoreCase(obj.toString()) || "Y".equalsIgnoreCase(obj.toString()) || "YES".equalsIgnoreCase(obj.toString()) || "TRUE".equalsIgnoreCase(obj.toString()) || "ENABLED".equalsIgnoreCase(obj.toString()));
    }

    public static <O> String booleanDBString(boolean value) {
        return value ? "1" : "0";
    }

    /**
     * This is equivalent of String.valueOf() in java.lang.String We are using this for static import and more meaningful Name
     *
     * @param input integerInput
     * @return String
     */
    public static String stringValue(int input) {
        return Integer.toString(input, 10);
    }

    public static String stringValue(long input) {
        return Long.toString(input, 10);
    }

    public static String stringValue(boolean input) {
        return Boolean.toString(input);
    }

    public static String multiply(char ch, int count) {
        StringBuilder sb = new StringBuilder(0);
        for (int i = 0; i < count; i++)
            sb.append(ch);
        return sb.toString();
    }

    public static String multiply(String str, int count) {
        StringBuilder sb = new StringBuilder(0);
        for (int i = 0; i < count; i++)
            sb.append(str);
        return sb.toString();
    }

    public static int maxInt(int... nums) {
        int max = 0;
        for (int num : nums)
            if (num > max) max = num;
        return max;
    }

    @SuppressWarnings("unchecked")
    public static String[] prefix(String prefix, Object... input) {
        if (input.length == 0) return new String[0];
        if (input.length == 1) {
            if (input[0] instanceof Collection<?>) return prefix(prefix, (Collection<Object>) input[0]);
            if (input[0].getClass().isArray()) return prefix(prefix, singletonList(input[0]));
        }
        return prefix(prefix, Arrays.asList(input));
    }

    public static String[] prefix(String prefix, Collection<Object> collection) {
        String[] output = new String[collection.size()];
        int i = 0;
        for (Object object : collection) {
            output[i++] = prefix + object.toString();
        }
        return output;
    }

    @SuppressWarnings("unchecked")
    public static String[] suffix(String suffix, Object... input) {
        if (input.length == 0) return new String[0];
        if (input.length == 1) {
            if (input[0] instanceof Collection<?>) return suffix(suffix, (Collection<Object>) input[0]);
            if (input[0].getClass().isArray()) return suffix(suffix, singletonList(input[0]));
        }
        return suffix(suffix, Arrays.asList(input));
    }

    public static String[] suffix(String suffix, Collection<Object> collection) {
        String[] output = new String[collection.size()];
        int i = 0;
        for (Object object : collection) {
            output[i++] = object.toString() + suffix;
        }
        return output;
    }

    public static String[] combinations(String[] array1, String[] array2) {
        if (array1.length == 0 || array2.length == 0) return new String[0];
        String[] combinations = new String[array1.length * array2.length];
        int i = 0;
        for (String str1 : array1) {
            for (String str2 : array2) {
                combinations[i++] = str1 + str2;
            }
        }
        return combinations;
    }

    public static boolean isJsonMap(String input) {
        if (isBlank(input)) return false;
        return input.charAt(0) == JSON_MAP_IDENTIFIER;
    }

    public static boolean isJsonArray(String input) {
        if (isBlank(input)) return false;
        return input.charAt(0) == JSON_ARRAY_IDENTIFIER;
    }

    public static String[] replace(String[] inputStrings, CharSequence target, CharSequence replacement) {
        if (inputStrings == null || inputStrings.length == 0) return null;
        String[] outputStrings = new String[inputStrings.length];
        for (int i = 0; i < inputStrings.length; i++) {
            if (inputStrings[i] == null) continue;
            outputStrings[i] = inputStrings[i].replace(target, replacement);
        }
        return outputStrings;
    }

    public static String[] replace(String sourceString, CharSequence match, String[] replacements) {
        if (replacements == null || replacements.length == 0) return null;
        String[] outputStrings = new String[replacements.length];
        for (int i = 0; i < replacements.length; i++) {
            outputStrings[i] = (replacements[i] == null) ? sourceString : sourceString.replace(match, replacements[i]);
        }
        return outputStrings;
    }

    public static String trim(String input, char trimChar) {
        if (input == null || input.length() == 0) return input;
        String output = (input.indexOf(trimChar) == 0) ? input.substring(1) : input;
        int outputLen = output.length();
        if (outputLen == 0) return output;
        output = (output.indexOf(trimChar) == outputLen - 1) ? output.substring(0, outputLen - 1) : output;
        return output;
    }

    public static String trim(String input, String prefix, String suffix) {
        if (input == null || input.length() == 0) return input;
        String output = input.startsWith(prefix) ? input.substring(prefix.length()) : input;
        int outputLen = output.length();
        if (outputLen == 0) return output;
        output = output.endsWith(suffix) ? output.substring(0, outputLen - suffix.length()) : output;
        return output;
    }

    public static String randomBase64GUID() {
        return "" + ((new Random()).nextInt(10000));
    }

    public static <K, V> String keyValuePairs(Map<K, V> map) {
        if (map == null || map.isEmpty()) return EMPTY_STRING;
        StringBuilder sb = new StringBuilder();
        for (Entry<K, V> entry : map.entrySet()) {
            sb.append(DEFAULT_SEPERATOR_STR);
            sb.append(DEFAULT_LEFT_WRAPPER_STR);
            sb.append(entry.getKey() == null ? NULL_OBJECT_REPRESENTATION : entry.getKey().toString());
            sb.append(" : ");
            sb.append(entry.getValue() == null ? NULL_OBJECT_REPRESENTATION : entry.getValue().toString());
            sb.append(DEFAULT_RIGHT_WRAPPER_STR);
        }
        return sb.substring(1);
    }

    public static Locale parseLocale(String localeString) {
        return parseLocale(localeString, null);
    }

    public static Locale parseLocale(String localeString, Locale defaultLocale) {
        if (isBlank(localeString)) return defaultLocale;
        Locale locale = _performanceLocaleStringCache.get(localeString);
        if (locale != null) return locale;
        try {
            locale = Locale.forLanguageTag(localeString);
            if (locale == null || isBlank(locale.toString())) throw _localeExceptionHolder;
            _performanceLocaleStringCache.put(localeString, locale);
            _performanceLocaleStringCache.put(localeString.replaceAll(MONGO_SEPERATOR, "_"), locale);
            _performanceLocaleStringCache.put(localeString.replaceAll("_", MONGO_SEPERATOR), locale);
        } catch (Exception e) {
            try {
                if (localeString.indexOf('-') > 0) {
                    localeString = localeString.replaceAll(MONGO_SEPERATOR, "_");
                } else {
                    localeString = localeString.replaceAll("_", MONGO_SEPERATOR);
                }
                locale = Locale.forLanguageTag(localeString);
                if (locale == null || isBlank(locale.toString())) throw _localeExceptionHolder;
                _performanceLocaleStringCache.put(localeString, locale);
                _performanceLocaleStringCache.put(localeString.replaceAll(MONGO_SEPERATOR, "_"), locale);
                _performanceLocaleStringCache.put(localeString.replaceAll("_", MONGO_SEPERATOR), locale);
            } catch (Exception ex) {
                locale = defaultLocale;
            }
        }
        return locale;
    }

    public static String localeString(Locale locale) {
        if (locale == null) return null;
        return locale.toString();
    }

    private static Map<String, Locale> localeMap(List<String> localeStrings) {
        Map<String, Locale> localeMap = new HashMap<>();
        for (String localeString : localeStrings) {
            localeString = localeString.trim();
            Locale locale = null;
            try {
                locale = Locale.forLanguageTag(localeString.replaceAll("_", MONGO_SEPERATOR));
            } catch (Exception e) {
                continue;
            }
            if (locale == null) continue;
            localeMap.put(localeString, locale);
            localeMap.put(localeString.replaceAll("_", MONGO_SEPERATOR), locale);
            localeMap.put(localeString.replaceAll(MONGO_SEPERATOR, "_"), locale);
        }
        return localeMap;
    }

    public static String padRight(int str, int length) {
        return StringUtils.rightPad(str + "", length, '0');
    }

    public static String padLeft(int str, int length) {
        return StringUtils.leftPad(str + "", length, '0');
    }

    public static String padLeft(String str, int length, char padChar) {
        return StringUtils.leftPad(str, length, padChar);
    }

    public static int extractInt(String str, int defaultValue) {
        if (str == null) return defaultValue;
        try {
            return Integer.parseInt(str.replaceAll("[^0-9]", "")); // TODO : doesn't take care of negative numbers
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static List<String> startsWith(List<String> collection, String prefix) {
        if (collection == null) return null;
        if (collection.isEmpty()) return collection;
        List<String> returnCollection = new ArrayList<>();
        for (String str : collection) {
            if (!str.startsWith(prefix)) continue;
            returnCollection.add(str);
        }
        return returnCollection;
    }

    public static String getDisplayLabel(Map<String, Object> inputObject, String displayFormat) {
        Pattern pattern = Pattern.compile("\\[(.*?)\\]", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(displayFormat);
        String format = new String(displayFormat);
        while (matcher.find()) {
            final String key = matcher.group(1);
            final String replacement = (String) inputObject.get(key);
            if (replacement == null) break;
            format = format.replace("[" + key + "]", replacement);
        }
        return format;
    }

    // Thread Safe Singleton Method Implementation
    private static class _instance {
        public static final StringUtil instance = new StringUtil();
    }
}
