package com.wondersgroup.healthcloud.common.http.utils;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;

/**
 * Created by jimmy on 16/8/5.
 */
public class JsonNameStrategy extends PropertyNamingStrategy {

    @Override
    public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
        return convert(defaultName);
    }

    @Override
    public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
        return convert(defaultName);
    }

    @Override
    public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
        return convert(defaultName);
    }

    public String convert(String defaultName) {

        char[] chars = defaultName.toCharArray();
        if (chars.length != 0) {
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == '_' && i != chars.length - 1) {
                    chars[i + 1] = Character.isLowerCase(chars[i + 1]) ? Character.toUpperCase(chars[i + 1]) : chars[i + 1];
                }
            }
        }
        return new StringBuffer().append(chars).toString().replace("_", "");

    }
}