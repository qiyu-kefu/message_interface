package com.qiyukf.openapi.session.model;

/**
 * 七鱼消息结构描述
 */
public class QiyuMessage {

    public static final String TYPE_TEXT = "TEXT";
    public static final String TYPE_PICTURE = "PICTURE";
    public static final String TYPE_AUDIO = "AUDIO";

    /**
     * 消息访客用户的ID
     */
    private String uid;

    /**
     * 消息类型，取值为：
     * TEXT: 文本
     * PICTURE: 图片
     * AUDIO: 语音
     */
    private String msgType;

    /**
     * 消息内容。
     * 如果是文本消息，为文本内容
     * 如果是多媒体消息，为描述多媒体消息的json
     */
    private Object content;

    /**
     * 发送该消息的客服ID
     */
    private long staffId;

    /**
     * 发送该消息的客服名字
     */
    private String staffName;

    /**
     * 消息发送时间
     */
    private long timeStamp;

    /**
     * 消息ID，用于去重
     */
    private String msgId;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public long getStaffId() {
        return staffId;
    }

    public void setStaffId(long staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
