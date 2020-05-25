package com.winter.github.distribute.model;

import com.winter.github.distribute.Constants;
import com.winter.github.distribute.annotation.CombineField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
