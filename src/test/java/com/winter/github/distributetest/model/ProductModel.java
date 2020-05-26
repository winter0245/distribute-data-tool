package com.winter.github.distributetest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <br>
 *
 * @author zhangdongdong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @date 2020年03月30日 16:57:08 <br>
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductModel {

    private String id;

    private String name;

    private double price;
}
