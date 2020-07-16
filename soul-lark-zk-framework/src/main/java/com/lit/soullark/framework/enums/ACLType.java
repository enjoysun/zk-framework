package com.lit.soullark.framework.enums;

import org.apache.zookeeper.data.ACL;

import java.util.List;

public enum ACLType {
    WORLD("world"),
    AUTH("auth"),
    DIGEST("digest"),
    IP("ip");

    private String type;

    ACLType(String code) {
        this.type = code;
    }

    public String getType() {
        return type;
    }
}
