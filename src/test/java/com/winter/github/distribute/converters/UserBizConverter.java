package com.winter.github.distribute.converters;

import com.winter.github.distribute.Constants;
import com.winter.github.distribute.annotation.CombineField;
import com.winter.github.distribute.converter.AbstractBizConverter;
import com.winter.github.distribute.model.UserInfoModel;
import com.winter.github.distribute.utils.ReflectUtil;

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
public class UserBizConverter extends AbstractBizConverter<String, UserInfoModel> {
    @Override protected String getBizModule() {
        //指定的业务模块，需要与 @CombineField 注解中的value保持一致
        return Constants.USER_MODULE;
    }

    @Override protected Map<String, UserInfoModel> queryConvertDataByIds(Set<String> ids) {
        //实际场景中此处应该去数据库、nosql 或其他微服务根据id集合查询数据
        return ids.stream().collect(Collectors.toMap(id -> id, id -> new UserInfoModel(id, "user-" + id)));
    }

    @Override protected <R> void convertField(R row, Map.Entry<Field, CombineField> bizEntry, List<UserInfoModel> matchList) {
        //通过反射注入查询的结果到目标字段，此方法由子类实现的原因是某些场景可能不需要注入完整的对象，
        // 例如可能只需要用户的名称，所以需要子类自定义实现
        ReflectUtil.setPropertyValue(row, bizEntry.getValue().convertField(), matchList.get(0));
    }

}
