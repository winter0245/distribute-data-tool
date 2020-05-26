package com.winter.github.distributetest.service.impl;

import com.google.common.collect.Lists;
import com.winter.github.distribute.annotation.Combine;
import com.winter.github.distributetest.model.OrderModel;
import com.winter.github.distributetest.model.ProductModel;
import com.winter.github.distributetest.model.UserInfoModel;
import com.winter.github.distributetest.service.OrderService;
import com.winter.github.distributetest.service.ProductService;
import com.winter.github.distributetest.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private UserService userService;

    @Resource
    private ProductService productService;

    @Override
    @Combine(value = OrderModel.class,isParallel = false)
    public List<OrderModel> queryOrders() {
        return Stream.generate(() -> {
            OrderModel orderModel = new OrderModel();
            orderModel.setId(RandomStringUtils.randomAlphanumeric(6));
            orderModel.setProductIds(Lists.newArrayList(RandomStringUtils.randomAlphanumeric(6), RandomStringUtils.randomAlphanumeric(6)));
            orderModel.setUserId(UUID.randomUUID().toString());
            return orderModel;
        }).limit(10).collect(Collectors.toList());
    }

    @Override public List<OrderModel> queryOrders2() {
        List<OrderModel> list = Stream.generate(() -> {
            OrderModel orderModel = new OrderModel();
            orderModel.setId(RandomStringUtils.randomAlphanumeric(6));
            orderModel.setProductIds(Lists.newArrayList(RandomStringUtils.randomAlphanumeric(6), RandomStringUtils.randomAlphanumeric(6)));
            orderModel.setUserId(UUID.randomUUID().toString());
            return orderModel;
        }).limit(10).collect(Collectors.toList());
        Set<String> pIds = new HashSet<>();
        List<String> uIds = new ArrayList<>();
        list.forEach(o -> {
            pIds.addAll(o.getProductIds());
            uIds.add(o.getUserId());
        });
        Map<String, UserInfoModel> userInfoModelMap = userService.getUserByIds(uIds).stream().collect(Collectors.toMap(UserInfoModel::getId, u -> u));
        Map<String, ProductModel> productModelMap = productService.queryByProducts(pIds).stream()
                .collect(Collectors.toMap(ProductModel::getId, p -> p, (p1, p2) -> p2));
        list.forEach(o -> {
            o.setUserInfoModel(userInfoModelMap.get(o.getUserId()));
            o.setProductModels(o.getProductIds().stream().map(productModelMap::get).collect(Collectors.toList()));
        });
        return list;
    }
}
