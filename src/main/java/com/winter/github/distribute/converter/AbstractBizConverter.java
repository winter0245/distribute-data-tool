package com.winter.github.distribute.converter;

import com.google.common.collect.Sets;
import com.winter.github.distribute.utils.ReflectUtil;
import com.winter.github.distribute.annotation.CombineField;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
        Map.Entry<Field, CombineField> bizEntry = checkBizNeedRender(rows, type);
        if (bizEntry != null) {
            try {
                //提取需要转换的id
                Set<U> ids = resolveFieldValues(rows, bizEntry.getKey());
                if (ReflectUtil.isEmpty(ids)) {
                    log.warn("not find correct [{}] ids ,skip convert", getBizModule());
                    return;
                }
                //根据id，去其他域或者其他存储的地方查询数据
                Map<U, C> resultMap = queryConvertDataByIds(ids);
                if (!ReflectUtil.isEmpty(resultMap)) {
                    rows.forEach(r -> {
                        List<C> result = map(r, bizEntry.getKey(), resultMap);
                        //检查每一行是否匹配到了数据
                        if (!ReflectUtil.isEmpty(result)) {
                            //转换到目标方法
                            convertField(r, bizEntry, result, type);
                        }
                    });
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
     * @return java.util.Map.Entry<java.lang.reflect.Field, com.minivision.planet.annotation.DeviceBiz> <br>
     * @author zhangdongdong <br>
     * @taskId <br>
     */
    <R> Map.Entry<Field, CombineField> checkBizNeedRender(List<R> rows, Class<R> type) {
        if (ReflectUtil.isEmpty(rows)) {
            return null;
        }
        Map<Field, CombineField> annotation = ReflectUtil.getAnnotation(type, CombineField.class);
        List<Map.Entry<Field, CombineField>> fieldAndAnnotations = annotation.entrySet().stream()
                .filter(a -> StringUtils.isNotBlank(a.getValue().convertField()) && a
                        .getValue().value().equals(getBizModule())).collect(
                        Collectors.toList());
        if (ReflectUtil.isEmpty(fieldAndAnnotations)) {
            return null;
        }
        Map.Entry<Field, CombineField> bizEntry = fieldAndAnnotations.get(0);
        String convertField = bizEntry.getValue().convertField();
        PropertyDescriptor field = ReflectUtil.getBeanPropertyMethod(type, convertField);
        if (field == null) {
            log.warn("not find convert field {} for class {} ,skip convert module {}", convertField, type, getBizModule());
            return null;
        }
        return bizEntry;
    }

    /**
     * Description: 从对象中提取需要组装的数据id<br>
     *
     * @param rows
     * @param field
     * @return java.util.Set<U> <br>
     * @author zhangdongdong <br>
     * @taskId <br>
     */
    protected <R> Set<U> resolveFieldValues(List<R> rows, Field field) {
        Class<?> type = field.getType();
        Set<U> result = Sets.newHashSet();
        if (type.isArray()) {
            rows.stream().map(e -> ReflectUtil.getPropertyValue(e, field.getName())).filter(Objects::nonNull).forEach(r ->
                    Arrays.stream(((U[]) r)).filter(Objects::nonNull).forEach(result::add)
            );
        } else if (Collection.class.isAssignableFrom(type)) {
            rows.stream().map(e -> ReflectUtil.getPropertyValue(e, field.getName())).filter(Objects::nonNull).forEach(r ->
                    ((Collection<U>) r).stream().filter(Objects::nonNull).forEach(result::add)
            );
        } else {
            rows.stream().map(e -> ReflectUtil.getPropertyValue(e, field.getName())).filter(Objects::nonNull)
                    .forEach(r -> result.add((U) r));
        }
        return result;
    }

    /**
     * Description: 匹配结果 <br>
     *
     * @param row       行数据
     * @param field     需要转换的属性
     * @param resultMap 查询结果
     * @return java.util.List<C> <br>
     * @author zhangdongdong <br>
     * @taskId <br>
     */
    protected <R> List<C> map(R row, Field field, Map<U, C> resultMap) {
        Class<?> type = field.getType();
        if (type.isArray()) {
            U[] ids = (U[]) ReflectUtil.getPropertyValue(row, field.getName());
            if (ids == null || ids.length == 0) {
                return Collections.emptyList();
            }
            return Arrays.stream(ids).filter(resultMap::containsKey).map(resultMap::get).collect(Collectors.toList());
        } else if (Collection.class.isAssignableFrom(type)) {
            Collection<U> ids = (Collection<U>) ReflectUtil.getPropertyValue(row, field.getName());
            if (ReflectUtil.isEmpty(ids)) {
                return Collections.emptyList();
            }
            return ids.stream().filter(resultMap::containsKey).map(resultMap::get).collect(Collectors.toList());
        } else {
            U id = (U) ReflectUtil.getPropertyValue(row, field.getName());
            if (id == null || !resultMap.containsKey(id)) {
                return Collections.emptyList();
            }
            return Collections.singletonList(resultMap.get(id));
        }
    }

    /**
     * Description: 将匹配到的结果映射到目标属性<br>
     *
     * @param row       行数据
     * @param bizEntry  转换的字段
     * @param matchList 转换的结果
     * @author zhangdongdong <br>
     * @taskId <br>
     */
    protected <R> void convertField(R row, Map.Entry<Field, CombineField> bizEntry, List<C> matchList, Class<R> type) {
        Class<?> fieldType = bizEntry.getKey().getType();
        PropertyDescriptor targetField = ReflectUtil.getBeanPropertyMethod(type, bizEntry.getValue().convertField());
        if (fieldType.isArray() || Collection.class.isAssignableFrom(fieldType) || targetField != null) {
            ReflectUtil.setPropertyValue(row, bizEntry.getValue().convertField(), matchList);
        } else {
            ReflectUtil.setPropertyValue(row, bizEntry.getValue().convertField(), matchList.get(0));
        }
    }
}
