package com.winter.github.distribute.utils;

import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * <br>
 *
 * @author zhangdongdong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @date 2020年05月26日 16:28:00 <br>
 */
@Data
public class ClassInfoContext {

    private MethodAccess methodAccess;

    private Map<String, Integer> methodMap;

    public ClassInfoContext(Class type) {
        this.methodAccess = MethodAccess.get(type);
        methodMap = new HashMap<>();
        for (String name : this.methodAccess.getMethodNames()) {
            methodMap.put(name, this.methodAccess.getIndex(name));
        }
    }

    public Object invoke(Object target, String name, Object... param) {
        Integer index = methodMap.get(name);
        return this.methodAccess.invoke(target, index, param);
    }
}
