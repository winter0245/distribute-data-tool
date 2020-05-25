package com.winter.github.distribute.service.impl;

import com.winter.github.distribute.model.AddressModel;
import com.winter.github.distribute.service.AddressService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {
    @Override
    public List<AddressModel> queryAddressByIds(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return ids.stream().map(id -> new AddressModel(RandomStringUtils.randomAlphanumeric(10), id, RandomStringUtils.randomNumeric(10))).collect(Collectors.toList());
    }
}
