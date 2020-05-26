package com.winter.github.distributetest.service.impl;

import com.winter.github.distribute.annotation.Combine;
import com.winter.github.distributetest.model.UserInfoModel;
import com.winter.github.distributetest.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserServiceImpl implements UserService {
    @Override
    @Combine(value = UserInfoModel.class)
    public List<UserInfoModel> getUsers() {
        AtomicInteger atomicInteger = new AtomicInteger();
        return Stream.generate(() -> new UserInfoModel(atomicInteger.incrementAndGet() + "", UUID.randomUUID().toString())).limit(10).collect(Collectors.toList());
    }

    @Combine(UserInfoModel.class)
    @Override
    public List<UserInfoModel> getUserByIds(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return ids.stream().map(id -> {
            UserInfoModel infoModel = new UserInfoModel(id, UUID.randomUUID().toString());
            infoModel.setAddressId(UUID.randomUUID().toString());
            return infoModel;
        }).collect(Collectors.toList());
    }
}
