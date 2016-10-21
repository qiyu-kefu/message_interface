package com.qiyukf.openapi.session.model;

/**
 * Created by zhoujianghua on 2016/10/11.
 */
public class CommonResult {

    /**
     * 错误码，200表示正确
     */
    private int code;

    /**
     * 错误描述信息
     */
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "code: " + code + ", \n" + "message: " + message;
    }
}
