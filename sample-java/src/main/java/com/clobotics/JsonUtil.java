package com.clobotics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String toString(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    public static <T> T toObj(String source, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(source, clazz);
    }

    public static <T> T toObj(String source, TypeReference<T> reference) throws JsonProcessingException {
        return objectMapper.readValue(source, reference);
    }
}