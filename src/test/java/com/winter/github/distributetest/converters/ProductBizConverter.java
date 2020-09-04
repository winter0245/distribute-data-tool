package com.winter.github.distributetest.converters;

import com.winter.github.distributetest.Constants;
import com.winter.github.distribute.annotation.CombineField;
import com.winter.github.distribute.converter.AbstractBizConverter;
import com.winter.github.distributetest.model.ProductModel;
import com.winter.github.distribute.utils.ReflectUtil;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 模拟商品数据聚合转换<br>
 *
 * @author zhangdongdong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @date 2020年03月30日 17:10:12 <br>
 */
@Component
public class ProductBizConverter extends AbstractBizConverter<String, ProductModel> {
    @Override
    protected String getBizModule() {
        return Constants.PRODUCT_MODULE;
    }

    @Override
    protected Map<String, ProductModel> queryConvertDataByIds(Set<String> ids) {
        return ids.stream().collect(Collectors.toMap(id -> id, id -> new ProductModel(id, "product-" + id, RandomUtils.nextDouble(1, 100))));
    }

}
