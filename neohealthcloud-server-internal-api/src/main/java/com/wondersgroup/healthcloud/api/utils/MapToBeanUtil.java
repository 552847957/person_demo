package com.wondersgroup.healthcloud.api.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;


/**
 * Created by jimmy on 16/8/5.
 */
public class MapToBeanUtil<T> {

    public T fromMapToBean(Class<T> type, Map map) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(type);
            T bean = type.newInstance();
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                String propertyName = propertyDescriptor.getName();
                if (map.containsKey(propertyName)) {
                    Object propertyValue = map.get(propertyName);
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
}
