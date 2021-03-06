package com.winter.github.distributetest.model;

import lombok.Data;

@Data
public class AddressModel {

    private String userId;

    private String id;

    private String name;

    public AddressModel(String userId, String id, String name) {
        this.userId = userId;
        this.id = id;
        this.name = name;
    }

    public AddressModel() {
    }
}
