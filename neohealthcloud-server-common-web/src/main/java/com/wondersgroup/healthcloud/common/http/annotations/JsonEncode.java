package com.wondersgroup.healthcloud.common.http.annotations;

import java.lang.annotation.*;

/**
 * Created by nick on 2017/6/26.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonEncode {

    boolean encode() default false;
}
