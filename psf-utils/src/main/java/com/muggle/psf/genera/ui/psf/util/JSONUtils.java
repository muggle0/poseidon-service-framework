package com.muggle.psf.genera.ui.psf.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @Description:
 * @Author: muggle
 * @Date: 2020/8/27
 **/
public class JSONUtils {

    private static final Logger logger = LoggerFactory.getLogger(JSONUtils.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    static {
        MAPPER.setTimeZone(TimeZone.getDefault());
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * 将对象序列化为JSON字符串
     *
     * @param object
     * @return
     */
    public static String toJacksonString(final Object object) {
        if (object == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(object);
        } catch (final JsonProcessingException e) {
            logger.warn("Parse object to json error: ", e);
        }
        return "";
    }

    public static <T> T paserJacsonObject(final String src, final Class<T> clazz) {
        if (StringUtils.isEmpty(src) || clazz == null) {
            return null;
        }
        try {
            return String.class.equals(clazz) ? (T) src : MAPPER.readValue(src, clazz);
        } catch (final Exception e) {
            logger.warn("Parse json to object error: ", e);
        }
        return null;
    }

    public static <T> T paserJacsonObject(final String src, final Class<T> clazz, final SimpleDateFormat dateFormat) {
        if (StringUtils.isEmpty(src) || clazz == null) {
            return null;
        }
        try {
            final ObjectMapper objectMapper = buildJacson(dateFormat);
            return String.class.equals(clazz) ? (T) src : objectMapper.readValue(src, clazz);
        } catch (final Exception e) {
            logger.warn("Parse json to object error: ", e);
        }
        return null;
    }

    private static ObjectMapper buildJacson(final SimpleDateFormat dateFormat) {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setTimeZone(TimeZone.getDefault());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setDateFormat(dateFormat);
        return objectMapper;
    }

}
