package com.winter.github.distribute.service;

import com.winter.github.distribute.model.AddressModel;

import java.util.List;

public interface AddressService {

    List<AddressModel> queryAddressByIds(List<String> ids);

}
