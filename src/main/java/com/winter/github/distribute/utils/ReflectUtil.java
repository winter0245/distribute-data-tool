package com.winter.github.distribute.utils;

import com.google.common.collect.Maps;
import com.sun.org.apache.regexp.internal.RE;
import com.winter.github.distribute.annotation.CombineField;
import com.winter.github.distribute.annotation.CombineFields;
import com.winter.github.distribute.converter.AbstractBizConverter;
import com.winter.github.distribute.converter.ConverterContext;
import com.winter.github.distribute.exception.ReflectionException;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.core.ReflectUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <br>
 *
 * @author zhangdongdong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @date 2020年03月30日 15:27:50 <br>
 */
@Slf4j
public class ReflectUtil {
    private ReflectUtil() {
        //避免外部实例化
    }

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    /**
     * 类属性map
     */
    private static final Map<Class<?>, Map<String, PropertyDescriptor>> CLASS_PROPERTY_MAP = Maps.newConcurrentMap();

    /**
     * 类注解属性缓存   key-> 类名+注解名称 ,value -> key 属性 ，value 是注解
     */
    private static final Map<String, Map<Field, ? extends Annotation>> CLASS_FIELD_ANNOTATION_CACHE = Maps.newHashMap();

    private static final Map<Class<?>, ClassInfoContext> CLASS_INFO_CONTEXT_CACHE = Maps.newConcurrentMap();

    /**
     * Return {@code true} if the supplied Collection is {@code null} or empty.
     * Otherwise, return {@code false}.
     *
     * @param collection the Collection to check
     * @return whether the given Collection is empty
     */
    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * Return {@code true} if the supplied Map is {@code null} or empty.
     * Otherwise, return {@code false}.
     *
     * @param map the Map to check
     * @return whether the given Map is empty
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    /**
     * Description: 获取类型的属性描述<br>
     *
     * @param type         类型
     * @param propertyName 属性名
     * @return java.beans.PropertyDescriptor <br>
     * @author zhangdongdong <br>
     * @taskId <br>
     */
    public static PropertyDescriptor getBeanPropertyMethod(Class<?> type, String propertyName) {
        return getBeanPropertyMethodMap(type).get(propertyName);
    }

    public static Map<String, PropertyDescriptor> getBeanPropertyMethodMap(Class<?> type) {
        return CLASS_PROPERTY_MAP.computeIfAbsent(type, k -> {
            PropertyDescriptor[] beanProperties = ReflectUtils.getBeanProperties(k);
            return Arrays.stream(beanProperties).collect(Collectors.toMap(PropertyDescriptor::getName, p -> p));
        });
    }

    /**
     * Description: 获取属性值<br>
     *
     * @param obj  实体类对象
     * @param name 属性名
     * @return java.lang.Object <br>
     * @author zhangdongdong <br>
     * @taskId <br>
     */
    public static Object getPropertyValue(Object obj, String name, Class type) {
        try {
            PropertyDescriptor propertyDescriptor = getBeanPropertyMethod(type, name);
            //            if (propertyDescriptor != null) {
            //                return propertyDescriptor.getReadMethod().invoke(obj);
            //            }
            return quickInvoke(type, obj, propertyDescriptor.getReadMethod().getName());
        } catch (Exception e) {
            log.error("get properties {} error", name, e);
        }
        return null;
    }

    /**
     * Description: 设置属性值<br>
     *
     * @param obj   实体对象
     * @param name  属性名称
     * @param value 属性值
     * @author zhangdongdong <br>
     * @taskId <br>
     */
    public static void setPropertyValue(Object obj, String name, Object value) {
        try {
            PropertyDescriptor propertyDescriptor = getBeanPropertyMethod(obj.getClass(), name);
            if (propertyDescriptor != null) {
                propertyDescriptor.getWriteMethod().invoke(obj, value);
            }
        } catch (Exception e) {
            log.error("set properties {} error", name, e);
        }
    }

    /**
     * Description: 并行转换(如果有线程安全问题不要使用此方法)<br>
     *
     * @param converters 所有的转换器
     * @param rows       需要转换的数据
     * @param type       数据类型
     * @author zhangdongdong <br>
     * @taskId <br>
     */
    public static <T> void parallelConvert(List<AbstractBizConverter<?, ?>> converters, List<T> rows, Class<T> type) {
        Optional.ofNullable(converters).ifPresent(list -> list.parallelStream().forEach(c -> c.convertBiz(rows, type)));
    }

    /**
     * Description: 串行转换(如果有线程安全问题不要使用此方法)<br>
     *
     * @param converters 所有的转换器
     * @param rows       需要转换的数据
     * @param type       数据类型
     * @author zhangdongdong <br>
     * @taskId <br>
     */
    public static <T> void linearConvert(List<AbstractBizConverter<?, ?>> converters, List<T> rows, Class<T> type) {
        Optional.ofNullable(converters).ifPresent(list -> list.forEach(c -> c.convertBiz(rows, type)));
    }

    /**
     * Description: 聚合数据<br>
     *
     * @param converters
     * @param rows
     * @param type
     * @param isParallel 是否需要并行 true-并行,false-线性
     * @author zhangdongdong <br>
     * @taskId <br>
     */
    public static <T> void convert(List<AbstractBizConverter<?, ?>> converters, List<T> rows, Class<T> type, boolean isParallel) {
        if (isParallel) {
            parallelConvert(converters, rows, type);
        } else {
            linearConvert(converters, rows, type);
        }
    }


    /**
     * 获取类带指定注解的属性
     *
     * @param type            类
     * @param annotationClass 注解类型
     * @param <T>             注解类型
     * @return key->属性字段,value->注解
     */
    public static <T extends Annotation> Map<Field, T> getAnnotation(Class<?> type, Class<T> annotationClass) {
        String key = type.getName() + "@" + annotationClass.getName();
        return (Map<Field, T>) CLASS_FIELD_ANNOTATION_CACHE.computeIfAbsent(key, k -> {
            Map<Field, T> result = Maps.newHashMap();
            if (!type.getSuperclass().equals(Object.class)) {
                //如果有父类，把父类的递归查出来
                result.putAll(getAnnotation(type.getSuperclass(), annotationClass));
            }
            Field[] fields = type.getDeclaredFields();
            result.putAll(Stream.of(fields).filter(f -> f.getAnnotation(annotationClass) != null)
                    .collect(Collectors.toMap(f -> f, f -> {
                        f.setAccessible(true);
                        return f.getAnnotation(annotationClass);
                    })));
            return result;
        });
    }

    public static Object quickInvoke(Class type, Object target, String methodName, Object... params) {
        ClassInfoContext classInfoContext = CLASS_INFO_CONTEXT_CACHE.computeIfAbsent(type, k -> new ClassInfoContext(type));
        return classInfoContext.invoke(target, methodName, params);
    }


    public static MethodHandle convertMethodToHandle(Method method) throws NoSuchMethodException, IllegalAccessException {
        Class<?> declaringClass = method.getDeclaringClass();
        Class<?> returnType = method.getReturnType();
        MethodType methodType = MethodType.methodType(returnType, method.getParameterTypes());
        return LOOKUP.findVirtual(declaringClass, method.getName(), methodType);
    }


    public static List<ConverterContext> exactCombineContexts(Class<?> type) {
        Map<String, PropertyDescriptor> propertyMethodMap = getBeanPropertyMethodMap(type);
        if (propertyMethodMap == null || propertyMethodMap.isEmpty()) {
            return Collections.emptyList();
        }
        return propertyMethodMap.values().stream().map(descriptor -> {
            Method readMethod = descriptor.getReadMethod();
            if (readMethod == null) {
                return null;
            }
            String fieldName = descriptor.getName();
            Field field = FieldUtils.getField(type, fieldName, true);
            CombineField[] combineFields = null;
            if (field != null) {
                combineFields = field.getAnnotationsByType(CombineField.class);
                if (ArrayUtils.isEmpty(combineFields) && field.getAnnotation(CombineFields.class) != null) {
                    combineFields = field.getAnnotation(CombineFields.class).value();
                }
            }
            if (ArrayUtils.isEmpty(combineFields)) {
                combineFields = readMethod.getAnnotationsByType(CombineField.class);
                if (ArrayUtils.isEmpty(combineFields) && readMethod.getAnnotation(CombineFields.class) != null) {
                    combineFields = readMethod.getAnnotation(CombineFields.class).value();
                }
            }
            if (combineFields != null && !ArrayUtils.isEmpty(combineFields)) {
                return Stream.of(combineFields).map(combineField -> {
                    PropertyDescriptor writerDescriptor = propertyMethodMap.get(combineField.convertField());
                    if (writerDescriptor == null || writerDescriptor.getWriteMethod() == null) {
                        return null;
                    }
                    return new ConverterContext(readMethod, writerDescriptor.getWriteMethod(), combineField.value());
                }).filter(Objects::nonNull);
            } else {
                return null;
            }
        }).filter(Objects::nonNull).flatMap(u -> u).collect(Collectors.toList());
    }


}
