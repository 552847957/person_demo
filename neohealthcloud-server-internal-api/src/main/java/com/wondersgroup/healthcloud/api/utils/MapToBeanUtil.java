package com.wondersgroup.healthcloud.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by jimmy on 16/8/5.
 */
public class MapToBeanUtil<T> {

    public T fromMapToBean(Class<T> type, Map<Object, Object> paraMap) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(type);
            T bean = type.newInstance();
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                String propertyName = propertyDescriptor.getName();
                if (paraMap.containsKey(propertyName)) {
                    Object propertyValue = paraMap.get(propertyName);
                    Object[] args = new Object[1];
                    args[0] = propertyValue;
                    try {
                        propertyDescriptor.getWriteMethod().invoke(bean, args);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
            return bean;
        } catch (IntrospectionException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("map convert to Bean is error", e);
        }
    }

    public static class JsonMapDeSerializer extends KeyDeserializer {

        @Override
        public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
            char[] chars = key.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == '_' && i != chars.length - 1) {
                    chars[i + 1] = Character.isLowerCase(chars[i + 1]) ? Character.toUpperCase(chars[i + 1]) : chars[i + 1];
                }
            }
            return new StringBuffer().append(chars).toString().replace("_", "");
        }
    }
}
