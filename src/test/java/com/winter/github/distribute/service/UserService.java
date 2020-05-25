package com.winter.github.distribute.service;

import com.winter.github.distribute.model.UserInfoModel;

import java.util.List;

public interface UserService {

    List<UserInfoModel> getUsers();

    List<UserInfoModel> getUserByIds(List<String> ids);
}
