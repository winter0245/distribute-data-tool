package com.winter.github.distribute.converter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.winter.github.distribute.annotation.CombineField;
import com.winter.github.distribute.utils.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 抽象业务转换类,子类需要实现少量的方法<br>
 * U id类型  <br>
 * C 关联查询出的原子数据类型 </>
 *
 * @author zhangdongdong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @date 2020年03月30日 15:22:41 <br>
 */
@Slf4j
public abstract class AbstractBizConverter<U, C> {

    private final Map<Class<?>, List<ConverterContext>> classModuleContextMap = Maps.newConcurrentMap();

    /**
     * Description: 获取需要抓换的业务关联模块<br>
     *
     * @param
     * @return java.lang.String <br>
     * @author zhangdongdong <br>
     * @taskId <br>
     */
    protected abstract String getBizModule();

    /**
     * Description: 根据id去其他域查询数据<br>
     *
     * @param ids
     * @return java.util.Map<U, C> key为id类型,value 为关联类型 <br>
     * @author zhangdongdong <br>
     * @taskId <br>
     */
    protected abstract Map<U, C> queryConvertDataByIds(Set<U> ids);

    /**
     * Description: 包装转换方法<br>
     *
     * @param rows
     * @param type
     * @author zhangdongdong <br>
     * @taskId <br>
     */
    public <R> void convertBiz(List<R> rows, Class<R> type) {
        long startTime = System.currentTimeMillis();
        List<ConverterContext> bizEntry = checkBizNeedRender(rows, type);
        if (!bizEntry.isEmpty()) {
            try {
                //提取需要转换的id
                Set<U> ids = resolveFieldValues(rows, bizEntry);
                if (ReflectUtil.isEmpty(ids)) {
                    log.warn("not find correct [{}] ids ,skip convert", getBizModule());
                    return;
                }
                //根据id，去其他域或者其他存储的地方查询数据
                Map<U, C> resultMap = queryConvertDataByIds(ids);
                if (!ReflectUtil.isEmpty(resultMap)) {
                    rows.stream().filter(Objects::nonNull).forEach(r ->
                            map(r, bizEntry, resultMap)
                    );
                }
            } catch (Exception e) {
                log.error("convert [{}] to [{}] error", getBizModule(), type, e);
            } finally {
                log.debug("convert [{}] finish ,take time :{}ms", getBizModule(), System.currentTimeMillis() - startTime);
            }
        }

    }

    /**
     * Description: 检查是否可以转换<br>
     *
     * @param rows
     * @param type
     * @return <R> List<ConverterContext> <br>
     * @author zhangdongdong <br>
     * @taskId <br>
     */
    <R> List<ConverterContext> checkBizNeedRender(List<R> rows, Class<R> type) {
        if (ReflectUtil.isEmpty(rows)) {
            return Collections.emptyList();
        }
        return classModuleContextMap.computeIfAbsent(type, k -> ReflectUtil.exactCombineContexts(type).stream()
                .filter(c -> Objects.equals(c.getModule(), getBizModule())).collect(Collectors.toList()));
    }

    /**
     * Description: 从对象中提取需要组装的数据id,同时聚合多个字段<br>
     *
     * @param rows
     * @param contexts
     * @return java.util.Set<U> <br>
     * @author zhangdongdong <br>
     * @taskId <br>
     */
    protected <R> Set<U> resolveFieldValues(List<R> rows, List<ConverterContext> contexts) {
        Set<U> result = Sets.newHashSet();
        contexts.forEach(c -> {
            Method readMethod = c.getReadMethod();
            Class<?> type = readMethod.getReturnType();
            if (type.isArray()) {
                rows.stream().filter(Objects::nonNull).map(e -> ReflectionUtils.invokeMethod(readMethod, e)).filter(Objects::nonNull).forEach(r ->
                        Arrays.stream(((U[]) r)).filter(Objects::nonNull).forEach(result::add));
            } else if (Collection.class.isAssignableFrom(type)) {
                rows.stream().map(e -> ReflectionUtils.invokeMethod(readMethod, e)).filter(Objects::nonNull).forEach(r ->
                        ((Collection<U>) r).stream().filter(Objects::nonNull).forEach(result::add));
            } else {
                rows.stream().map(e -> ReflectionUtils.invokeMethod(readMethod, e)).filter(Objects::nonNull)
                        .forEach(r -> result.add((U) r));
            }
        });
        return result;
    }

    /**
     * Description: 匹配结果 <br>
     *
     * @param row       行数据
     * @param contexts  需要转换的属性集合
     * @param resultMap 查询结果
     * @return java.util.List<C> <br>
     * @author zhangdongdong <br>
     * @taskId <br>
     */
    protected <R> void map(R row, List<ConverterContext> contexts, Map<U, C> resultMap) {
        contexts.forEach(c -> {
            Method readMethod = c.getReadMethod();
            Class<?> type = readMethod.getReturnType();
            List<C> matchList = Collections.emptyList();
            if (type.isArray()) {
                U[] ids = (U[]) ReflectionUtils.invokeMethod(readMethod, row);
                if (ids == null || ids.length == 0) {
                    return;
                }
                matchList = Arrays.stream(ids).filter(resultMap::containsKey).map(resultMap::get).collect(Collectors.toList());
            } else if (Collection.class.isAssignableFrom(type)) {
                Collection<U> ids = (Collection<U>) ReflectionUtils.invokeMethod(readMethod, row);
                if (ReflectUtil.isEmpty(ids)) {
                    return;
                }
                matchList = ids.stream().filter(resultMap::containsKey).map(resultMap::get).collect(Collectors.toList());
            } else {
                U id = (U) ReflectionUtils.invokeMethod(readMethod, row);
                if (id == null || !resultMap.containsKey(id)) {
                    return;
                }
                matchList = Lists.newArrayList(resultMap.get(id));
            }
            if (!matchList.isEmpty()) {
                convertField(row, c, matchList);
            }
        });

    }

    /**
     * Description: 将匹配到的结果映射到目标属性<br>
     *
     * @param row              行数据
     * @param converterContext 转换的字段上下文
     * @param matchList        转换的结果
     * @author zhangdongdong <br>
     * @taskId <br>
     */
    protected <R> void convertField(R row, ConverterContext converterContext, List<C> matchList) {
        Class<?> fieldType = converterContext.getWriteMethod().getParameterTypes()[0];
        if (fieldType.isArray() || Collection.class.isAssignableFrom(fieldType)
        ) {
            ReflectionUtils.invokeMethod(converterContext.getWriteMethod(), row, matchList);
        } else {
            ReflectionUtils.invokeMethod(converterContext.getWriteMethod(), row, matchList.get(0));
        }
    }

}
