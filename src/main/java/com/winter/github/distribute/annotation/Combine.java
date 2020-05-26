package com.winter.github.distribute.annotation;

import java.lang.annotation.*;

/**
 * 标记组合字段的注解<br>
 * 例如 private String userId 字段需要关联查询出用户信息，可以加此注解
 *
 * @author zhangdongdong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @date 2020年03月30日 15:05:24 <br>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Combine {
    /**
     * 聚合数据类型
     * @return
     */
    Class<?> value();

    /**
     * 是否并行抓取数据
     * @return
     */
    boolean isParallel () default true;
}
