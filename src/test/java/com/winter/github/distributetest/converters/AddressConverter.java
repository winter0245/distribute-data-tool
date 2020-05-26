package com.winter.github.distributetest.converters;

import com.google.common.collect.Lists;
import com.winter.github.distributetest.Constants;
import com.winter.github.distribute.converter.AbstractBizConverter;
import com.winter.github.distributetest.model.AddressModel;
import com.winter.github.distributetest.service.AddressService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AddressConverter extends AbstractBizConverter<String, AddressModel> {

    @Resource
    private AddressService addressService;

    @Override
    protected String getBizModule() {
        return Constants.ADDRESS_MODULE;
    }

    @Override
    protected Map<String, AddressModel> queryConvertDataByIds(Set<String> ids) {
        return addressService.queryAddressByIds(Lists.newArrayList(ids)).stream().collect(Collectors.toMap(AddressModel::getId, a -> a, (a1, a2) -> a2));
    }
}
