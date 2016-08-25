package com.wondersgroup.healthcloud.api.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jeffrey on 15/11/5.
 */
public class JacksonHelper {

    private ObjectMapper mapper;

    public static JacksonHelper getInstance() {
        return new JacksonHelper();
    }

    public static JacksonHelper getInstance(ObjectMapper objectMapper) {
        return new JacksonHelper(objectMapper);
    }

    private JacksonHelper() {
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private JacksonHelper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public JacksonHelper filter(String filterName, SimpleBeanPropertyFilter propertyFilter) {
        FilterProvider provider = new SimpleFilterProvider().addFilter(filterName, propertyFilter);
        mapper.setFilterProvider(provider);
        annotationIntrospector();
        return this;
    }

    public JacksonHelper filterProvider(FilterProvider provider) {
        mapper.setFilterProvider(provider);
        annotationIntrospector();
        return this;
    }

    public JacksonHelper serializeExclude(Map<Class, String[]> properties) {
        SimpleFilterProvider provider = new SimpleFilterProvider().setFailOnUnknownId(false);
        for (Map.Entry<Class, String[]> entry : properties.entrySet()) {
            provider.addFilter(entry.getKey().getName(), SimpleBeanPropertyFilter.serializeAllExcept(entry.getValue()));
        }
        return filterProvider(provider);
    }

    public JacksonHelper serializeExcludeSet(Map<Class, Set<String>> properties) {
        SimpleFilterProvider provider = new SimpleFilterProvider().setFailOnUnknownId(false);
        for (Map.Entry<Class, Set<String>> entry : properties.entrySet()) {
            provider.addFilter(entry.getKey().getName(), SimpleBeanPropertyFilter.serializeAllExcept(entry.getValue()));
        }
        return filterProvider(provider);
    }

    public JacksonHelper serializeInclude(Map<Class, String[]> properties) {
        SimpleFilterProvider provider = new SimpleFilterProvider().setFailOnUnknownId(false);
        for (Map.Entry<Class, String[]> entry : properties.entrySet()) {
            provider.addFilter(entry.getKey().getName(), SimpleBeanPropertyFilter.filterOutAllExcept(entry.getValue()));
        }
        return filterProvider(provider);
    }

    public JacksonHelper serializeIncludeSet(Map<Class, Set<String>> properties) {
        SimpleFilterProvider provider = new SimpleFilterProvider().setFailOnUnknownId(false);
        for (Map.Entry<Class, Set<String>> entry : properties.entrySet()) {
            provider.addFilter(entry.getKey().getName(), SimpleBeanPropertyFilter.filterOutAllExcept(entry.getValue()));
        }
        return filterProvider(provider);
    }

    public <T> T json2Obj(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("解析json错误");
        }
    }

    public String writeValueAsString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("解析对象错误");
        }
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> json2List(String json) {
        try {
            return mapper.readValue(json, List.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("解析json错误");
        }
    }

    private void annotationIntrospector() {
        mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
            @Override
            public Object findFilterId(Annotated a) {
                Object id = super.findFilterId(a);
                return generateFilter(a, id);
            }
        });
    }

    private static Object generateFilter(Annotated a, Object id) {
        if (null != id) {
            return id;
        }
        String filterName = a.getName();
        if (filterName.contains("java.util.")) {
            return null;
        }
        return !filterName.contains(".") ? null : filterName;
    }
}
