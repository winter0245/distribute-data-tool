package com.winter.github.distributetest.service.impl;

import com.google.common.collect.Lists;
import com.winter.github.distribute.annotation.Combine;
import com.winter.github.distributetest.model.OrderModel;
import com.winter.github.distributetest.service.OrderService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class OrderServiceImpl implements OrderService {
    @Override
    @Combine(OrderModel.class)
    public List<OrderModel> queryOrders() {
        return Stream.generate(() -> {
            OrderModel orderModel = new OrderModel();
            orderModel.setId(RandomStringUtils.randomAlphanumeric(6));
            orderModel.setProductIds(Lists.newArrayList(RandomStringUtils.randomAlphanumeric(6), RandomStringUtils.randomAlphanumeric(6)));
            orderModel.setUserId(UUID.randomUUID().toString());
            return orderModel;
        }).limit(10).collect(Collectors.toList());
    }
}
