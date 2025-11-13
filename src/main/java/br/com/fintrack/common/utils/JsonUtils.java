package br.com.fintrack.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    public static <T> T jsonToObject(final String json, final Class<T> clazz) {
        return MAPPER.convertValue(json, clazz);
    }

    public static String objectToJson(final Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }
}
