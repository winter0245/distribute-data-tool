package com.winter.github.distributetest.service;

import com.winter.github.distributetest.model.UserInfoModel;

import java.util.List;

public interface UserService {

    List<UserInfoModel> getUsers();

    List<UserInfoModel> getUserByIds(List<String> ids);

    List<UserInfoModel> getUserByIdsWithAddress(List<String> ids);
}
