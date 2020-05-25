package com.winter.github.distribute.service;

import com.winter.github.distribute.model.OrderModel;

import java.util.List;

public interface OrderService {
    List<OrderModel> queryOrders();
}
