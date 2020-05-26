package com.winter.github.distributetest.service;

import com.winter.github.distributetest.model.ProductModel;

import java.util.List;
import java.util.Set;

/**
 * <br>
 *
 * @author zhangdongdong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @date 2020年05月26日 15:25:10 <br>
 */
public interface ProductService {
    List<ProductModel> queryByProducts(Set<String> ids);
}
