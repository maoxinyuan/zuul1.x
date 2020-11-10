package com.pacific.apigetway.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Lokiy
 * @date 2018/6/14
 * @description
 */
public class JsonUtil {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> T toObject(String string, Class<T> clazz) {
        try {
            return JSON.parseObject(string, clazz);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return null;
    }

    public static <T> String toJson(T t) {
        try {
            return JSON.toJSONString(t);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return null;
    }

    public static <T> T toComplexObject(String s, Type type){
        try {
            return new Gson().fromJson(s, type);
        } catch (JsonSyntaxException e) {
            logger.error("Exception", e);
        }
        return null;
    }

    public static <T> String toGson(T t) {
        return new Gson().toJson(t);
    }

    public static <T> Map<String, T> toMap(String jsonAsString) throws JsonGenerationException {
        try {
            return mapper.readValue(jsonAsString, new  TypeReference<Map<String, T>>() {
            });
        } catch (Exception e) {
            logger.error("parse jsonString error.[" + jsonAsString + "]");
            throw new JsonGenerationException(e);
        }
    }

    public static <T> Map<String, T>[] toMapArray(String jsonAsString) throws JsonGenerationException {
        try {
            return mapper.readValue(jsonAsString, new TypeReference<Map<String, T>[]>() {
            });
        } catch (Exception e) {
            logger.error("parse jsonString error.[" + jsonAsString + "]");
            throw new JsonGenerationException(e);
        }
    }

    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    public static <T> T toList(String string, Class<?> clazzList,Class<?> clazzBean) {
        try {
            JavaType javaType = getCollectionType(clazzList, clazzBean);
            return mapper.readValue(string, javaType);
        } catch (IOException e) {
            logger.error("Exception", e);
        }
        return null;
    }


    /**
     * 将json对象转换成Map
     *
     * @param jsonObject json对象
     * @return Map对象
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jsontoMap(JSONObject jsonObject) {
        Map<String, Object> result = new HashMap<String, Object>(1);
        Iterator<String> iterator = jsonObject.keySet().iterator();
        String key = null;
        String value = null;
        while (iterator.hasNext()) {
            key = iterator.next();
            value = jsonObject.getString(key);
            result.put(key, value);
        }
        return result;
    }
}
