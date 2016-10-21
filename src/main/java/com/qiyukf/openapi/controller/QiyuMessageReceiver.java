package com.qiyukf.openapi.controller;

import com.qiyukf.openapi.controller.wxservice.WxMessageService;
import com.qiyukf.openapi.session.ResponseParser;
import com.qiyukf.openapi.session.model.QiyuMessage;
import com.qiyukf.openapi.session.model.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by zhoujianghua on 2016/10/19.
 */
@Component("qiyuMessageReceiver")
public class QiyuMessageReceiver extends ResponseParser {

    @Autowired
    private AsyncTaskManager taskManager;

    @Autowired
    private WxMessageService wxMessageService;

    @Autowired
    private QiyuSessionManager sessionManager;

    public QiyuMessageReceiver() {
        super(Constants.QIYU_APP_SECRET);
    }

    @Override
    protected void onMessage(final QiyuMessage message) {
        taskManager.getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    wxMessageService.replyText(message.getUid(), message.getContent().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onSessionStart(Session session) {
        sessionManager.onSessionStart(session);
    }

    @Override
    protected void onSessionEnd(final Session session) {

        taskManager.getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    sessionManager.onSessionEnd(session);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onValidationError(Long time, String checksum, String eventType, String content) {

    }
}
