package com.winter.github.distribute.model;

import com.winter.github.distribute.Constants;
import com.winter.github.distribute.annotation.CombineField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 模拟订单对象<br>
 *
 * @author zhangdongdong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @date 2020年03月30日 16:52:06 <br>
 */
@Data
public class OrderModel {

    private String id;

    @CombineField(value = Constants.USER_MODULE, convertField = "userInfoModel")
    private String userId;

    @CombineField(value = Constants.PRODUCT_MODULE, convertField = "productModels")
    private List<String> productIds;

    /**
     * 用户信息
     */
    private UserInfoModel userInfoModel;

    /**
     * 商品信息
     */
    private List<ProductModel> productModels;

    public OrderModel(String id, String userId, List<String> productIds) {
        this.id = id;
        this.userId = userId;
        this.productIds = productIds;
    }

    public OrderModel() {
    }
}
