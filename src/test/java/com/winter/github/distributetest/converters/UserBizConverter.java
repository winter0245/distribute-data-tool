package com.winter.github.distributetest.converters;

import com.google.common.collect.Lists;
import com.winter.github.distribute.converter.ConverterContext;
import com.winter.github.distributetest.Constants;
import com.winter.github.distribute.annotation.CombineField;
import com.winter.github.distribute.converter.AbstractBizConverter;
import com.winter.github.distributetest.model.UserInfoModel;
import com.winter.github.distributetest.service.UserService;
import com.winter.github.distribute.utils.ReflectUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 模拟用户数据聚合转换<br>
 *
 * @author zhangdongdong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @date 2020年03月30日 17:03:37 <br>
 */
@Component
public class UserBizConverter extends AbstractBizConverter<String, UserInfoModel> {

    @Resource
    private UserService userService;

    @Override
    protected String getBizModule() {
        //指定的业务模块，需要与 @CombineField 注解中的value保持一致
        return Constants.USER_MODULE;
    }

    @Override
    protected Map<String, UserInfoModel> queryConvertDataByIds(Set<String> ids) {
        //实际场景中此处应该去数据库、nosql 或其他微服务根据id集合查询数据
        return userService.getUserByIds(Lists.newArrayList(ids)).stream().collect(Collectors.toMap(UserInfoModel::getId, u -> u, (u1, u2) -> u2));
    }


    @Override
    protected <R> void convertField(R row, ConverterContext converterContext, List<UserInfoModel> matchList) {
        //通过反射注入查询的结果到目标字段，此方法由子类实现的原因是某些场景可能不需要注入完整的对象，
        ReflectionUtils.invokeMethod(converterContext.getWriteMethod(), row, matchList.get(0));
    }
}
