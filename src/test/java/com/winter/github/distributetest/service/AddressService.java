package com.winter.github.distributetest.service;

import com.winter.github.distributetest.model.AddressModel;

import java.util.List;

public interface AddressService {

    List<AddressModel> queryAddressByIds(List<String> ids);

}
