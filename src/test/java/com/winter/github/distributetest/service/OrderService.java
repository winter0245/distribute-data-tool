package com.winter.github.distributetest.service;

import com.winter.github.distributetest.model.OrderModel;

import java.util.List;

public interface OrderService {
    List<OrderModel> queryOrders();
}
