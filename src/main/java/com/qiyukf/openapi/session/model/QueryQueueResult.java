package com.qiyukf.openapi.session.model;

/**
 * Created by zhoujianghua on 2016/10/13.
 */
public class QueryQueueResult {

    /**
     * 错误码，200表示正确
     */
    private int code;

    /**
     * 当前排在查询对象前用户数
     */
    private int count;

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
