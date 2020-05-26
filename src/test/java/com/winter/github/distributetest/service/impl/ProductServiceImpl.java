package com.winter.github.distributetest.service.impl;

import com.winter.github.distributetest.model.ProductModel;
import com.winter.github.distributetest.service.ProductService;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <br>
 *
 * @author zhangdongdong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @date 2020年05月26日 15:25:57 <br>
 */
@Service
public class ProductServiceImpl implements ProductService {
    @Override public List<ProductModel> queryByProducts(Set<String> ids) {
        return ids.stream().map(id -> new ProductModel(id, "product-" + id, RandomUtils.nextDouble(1, 100))).collect(Collectors.toList());
    }
}
