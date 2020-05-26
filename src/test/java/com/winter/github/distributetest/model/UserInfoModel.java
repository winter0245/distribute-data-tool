package com.winter.github.distributetest.model;

import com.winter.github.distributetest.Constants;
import com.winter.github.distribute.annotation.CombineField;
import lombok.Data;

import java.util.List;

/**
 * <br>
 *
 * @author zhangdongdong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @date 2020年03月30日 16:00:11 <br>
 */
@Data
public class UserInfoModel {

    private String id;

    private String name;


    @CombineField(value = Constants.ADDRESS_MODULE,convertField = "addressModels")
    private String addressId;

    private List<AddressModel> addressModels;

    public UserInfoModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public UserInfoModel() {
    }
}
