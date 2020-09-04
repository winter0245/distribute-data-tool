package com.winter.github.distribute.annotation;

import java.lang.annotation.*;
/**
 * @Title: 聚合多个注解
 * @ClassName: com.winter.github.distribute.annotation.CombineFields.java
 * @Description:
 * @author: winter
 * @date:  2020-09-04 10:53
 * @version V1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD})
public @interface CombineFields {
    CombineField [] value();
}
