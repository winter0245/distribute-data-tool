package com.winter.github.distribute.annotation;

import java.lang.annotation.*;

/**
 * 标记组合字段的注解<br>
 * 例如 private String userId 字段需要关联查询出用户信息，可以加此注解
 * @author zhangdongdong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @date 2020年03月30日 15:05:24 <br>
 */
@Repeatable(CombineFields.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD})
public @interface CombineField {
    /**
     * moduleCode ,业务关联模块编码
     *
     * @return
     */
    String value();
    /**
     *  需要转换的目标字段<br>
     *  例如需要根据userId，注入用户信息到 userInfo字段，那么此处填userInfo
     * @author zhangdongdong<br>
     * @version 1.0<br>
     * @taskId <br>
     * @date  2020年03月30日 15:11:11 <br>
     */
    String convertField();
}
