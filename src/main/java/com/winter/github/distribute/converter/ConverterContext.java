package com.winter.github.distribute.converter;

import lombok.Data;

import java.lang.reflect.Method;

@Data
public class ConverterContext {

    private Method readMethod;

    private Method writeMethod;

    private String module;

    public ConverterContext(Method readMethod, Method writeMethod, String module) {
        this.readMethod = readMethod;
        this.writeMethod = writeMethod;
        this.module = module;
    }
}
