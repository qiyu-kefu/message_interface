package com.qiyukf.openapi.session;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiyukf.openapi.session.constant.EventType;
import com.qiyukf.openapi.session.model.QiyuMessage;
import com.qiyukf.openapi.session.model.Session;
import com.qiyukf.openapi.session.util.MD5;
import com.qiyukf.openapi.session.util.QiyuPushCheckSum;

/**
 * 收到七鱼的消息响应处理器。基类封装了校验操作，以及消息事件派发，具体事件处理由具体子类完成。
 */
public abstract class ResponseParser {

    private String appSecret;

    public ResponseParser(String appSecret) {
        this.appSecret = appSecret;
    }

    /**
     * 处理七鱼服务器通过在管理后台配置的消息接口url发送过来的http请求。
     * 由于七鱼要求所有请求必须在10s内返回，因此，在这个响应函数中，不要做耗时操作。
     * 如果涉及到做网络请求等耗时操作，可以放到异步线程或者消息队列中去做
     * @param time 请求的时间，http请求参数直接填入即可
     * @param checksum 请求校验和，http请求参数直接填入即可
     * @param eventType 时间类型，http请求参数直接填入即可
     * @param content 请求的数据内容，一般都是json格式的字符串
     * @return 固定返回为空字符串
     */
    public String onReceive(Long time, String checksum, String eventType, String content) {
        try {
            if (validate(time, checksum, content)) {
                onValidationError(time, checksum, eventType, content);
            }
            JSONObject json = JSONObject.parseObject(content);

            switch (eventType) {
                case EventType.MSG: {
                    QiyuMessage message = JSON.toJavaObject(json, QiyuMessage.class);
                    onMessage(message);
                }
                break;
                case EventType.SESSION_START: {
                    Session session = JSON.toJavaObject(json, Session.class);
                    onSessionStart(session);
                }
                break;
                case EventType.SESSION_END: {
                    Session session = JSON.toJavaObject(json, Session.class);
                    onSessionEnd(session);
                }
                break;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return "";
    }

    protected abstract void onMessage(QiyuMessage message);

    protected abstract void onSessionStart(Session session);

    protected abstract void onSessionEnd(Session session);

    protected abstract void onValidationError(Long time, String checksum, String eventType, String content);

    private boolean validate(Long time, String checksum, String content) {
        String md5 = MD5.md5(content);
        String current = QiyuPushCheckSum.encode(appSecret, md5, time);
        return current.equals(checksum);
    }
}
