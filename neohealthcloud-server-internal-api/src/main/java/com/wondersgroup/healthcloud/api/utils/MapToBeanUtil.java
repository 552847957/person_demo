package com.wondersgroup.healthcloud.api.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by jimmy on 16/8/5.
 */
public class MapToBeanUtil<T> {

    public T fromMapToBean(Class<T> type, Map<String, Object> paraMap) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(type);
            T bean = type.newInstance();
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            //特殊处理 "is_available" to ""isAvailable"
            paraMap = convert(paraMap);
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

    public Map<String, Object> convert(Map<String, Object> paraMap) {
        Map<String, Object> resultMap = new HashMap();
        Iterator<Map.Entry<String, Object>> it = paraMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            String paraName = entry.getKey();
            Object value = entry.getValue();
            char[] chars = paraName.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == '_' && i != chars.length - 1) {
                    chars[i + 1] = Character.isLowerCase(chars[i + 1]) ? Character.toUpperCase(chars[i + 1]) : chars[i + 1];
                }
            }
            it.remove();
            paraName = new StringBuffer().append(chars).toString().replace("_", "");
            resultMap.put(paraName, value);
        }
        return resultMap;
    }
}
