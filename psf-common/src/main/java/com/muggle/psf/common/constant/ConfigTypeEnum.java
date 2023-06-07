package com.muggle.psf.common.constant;

/**
 * Description
 * Date 2023/6/7
 * Created by muggle
 */
public enum ConfigTypeEnum {
    GATEWAY((byte) 1, "路由配置");


    private byte code;

    private String name;

    public byte getCode() {
        return code;
    }

    ConfigTypeEnum(final byte code, final String name) {
        this.code = code;
        this.name = name;
    }

    public void setCode(final byte code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
