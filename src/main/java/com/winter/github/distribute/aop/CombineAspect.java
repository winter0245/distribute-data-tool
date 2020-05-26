package com.winter.github.distribute.aop;

import com.winter.github.distribute.annotation.Combine;
import com.winter.github.distribute.converter.AbstractBizConverter;
import com.winter.github.distribute.utils.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Slf4j
@Order(-1)
@Aspect
public class CombineAspect {

    @Resource
    private List<AbstractBizConverter<?, ?>> converterList;

    @AfterReturning(value = "@annotation(combine)", returning = "retVal")
    public Object combineMethod(JoinPoint joinPoint, Object retVal, Combine combine) {
        if (retVal != null) {
            Class type = combine.value();
            log.info("method {} return {}", joinPoint.getSignature(), retVal);
            if (retVal instanceof List) {
                List rows = (List) retVal;
                ReflectUtil.convert(converterList, rows, type, combine.isParallel());
            } else if (type.isAssignableFrom(retVal.getClass())) {
                ReflectUtil.convert(converterList, Collections.singletonList(retVal), type, combine.isParallel());
            } else {
                log.warn("method {} return type is {} not {}", joinPoint.getSignature(), retVal.getClass(), type);
            }
        }
        return retVal;
    }
}
