package com.qiyukf.openapi.session.model;

/**
 * Created by zhoujianghua on 2016/10/11.
 */
public class ApplyStaffResult {

    /**
     * 申请客服的返回码。
     * 200：分配到客服，对应的会话信息在Session中
     * 14007: 没有客服在线
     * 14008: 需要排队，此时count字段有效，表示排在该用户前面的人数
     * 其他：网络错误
     */
    private int code;

    /**
     * 如果分配到客服，此字段是欢迎语，如果没有分配到客服，则是错误提示信息。
     */
    private String message;

    /**
     * 仅返回需要排队时该参数有效，表明排队队列中排在你前面你的人数。如果为0，则表示你排在队列最前面。
     */
    private int count;

    /**
     * 如果分配到客服，此字段是这次会话的资料
     */
    private Session session;

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
