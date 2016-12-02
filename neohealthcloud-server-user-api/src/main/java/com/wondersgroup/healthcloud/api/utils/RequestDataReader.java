package com.wondersgroup.healthcloud.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wondersgroup.healthcloud.common.http.exceptions.BadRequestException;
import com.wondersgroup.healthcloud.common.http.exceptions.RequestPostMissingKeyException;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.Map;


public class RequestDataReader {
    private JsonNode node;
    //处理POST请求的参数
    public RequestDataReader(String body) {
        if(StringUtils.isBlank(body))
            throw new BadRequestException();
        ObjectMapper mapper = new ObjectMapper();
        try {
            node = mapper.readTree(body);
        } catch (IOException e) {
            throw new BadRequestException();
        }
    }
    //处理GET请求的参数
    public RequestDataReader(Map<String,Object> body) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json =  mapper.writeValueAsString(body);
            node = mapper.readTree(json);
        } catch (IOException e) {
            throw new BadRequestException();
        }
    }

    public String readString(final String key, final Boolean nullable) {
        if (node.has(key)) {
            String value = node.get(key).asText();
            if (StringUtils.isBlank(value) && !nullable) {
                throw new RequestPostMissingKeyException(key);
            }
            return node.get(key).asText();
        } else if (nullable) {
            return null;
        } else {
            throw new RequestPostMissingKeyException(key);
        }
    }

    public String readDefaultString(final String key, final String defaultValue) {
        return StringUtils.defaultString(readString(key, true), defaultValue);
    }

    public Integer readInteger(final String key, final Boolean nullable) {
        if (node.has(key)) {
            return node.get(key).isInt() ? node.get(key).asInt() : Integer.valueOf(node.get(key).asText());
        } else if (nullable) {
            return null;
        } else {
            throw new RequestPostMissingKeyException(key);
        }
    }

    public Integer readDefaultInteger(final String key, final Integer defaultValue) {
        Integer value = readInteger(key, true);
        return value == null ? defaultValue : value;
    }

    public Boolean readBoolean(final String key, final Boolean nullable) {
        if (node.has(key)) {
            return node.get(key).isBoolean() ? node.get(key).asBoolean() : Boolean.valueOf(node.get(key).asText());
        } else if (nullable) {
            return null;
        } else {
            throw new RequestPostMissingKeyException(key);
        }
    }

    public Boolean readDefaultBoolean(final String key, final Boolean defaultValue) {
        Boolean value = readBoolean(key, true);
        return value == null ? defaultValue : value;
    }

    public <T> T readObject(final String key, final Boolean nullable, final Class<T> type) {
        if (node.has(key)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.treeToValue(node.get(key), type);
            } catch (JsonProcessingException e) {
                throw new BadRequestException();
            }
        } else if (nullable) {
            return null;
        } else {
            throw new RequestPostMissingKeyException(key);
        }
    }

}
