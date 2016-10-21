package com.qiyukf.openapi.controller.wxservice;

import com.alibaba.fastjson.JSONObject;
import com.qiyukf.openapi.controller.Constants;
import com.qiyukf.openapi.controller.wxutil.EmojiConverter;
import com.qiyukf.openapi.session.util.HttpClientPool;
import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by zhoujianghua on 2015/10/24.
 */
@Service("wxMsgService")
public class WxMessageService {

    private static final String TAG_TO_USER = "touser";
    private static final String TAG_MSG_TYPE = "msgtype";
    private static final int defaultRetryTimes = 2;
    private static final String sendRetryQueue = "mq_send_retry_queue";

    private static Logger logger = Logger.getLogger(WxMessageService.class);

    private static final int MAX_BYTES_LIMIT = 2000;

    @Autowired
    private WXAuthService wxAuthService;

    @Autowired
    private EmojiConverter emojiConverter;

    public void replyText(String openId, String text) throws IOException {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        sendText(openId, text);
    }

    private void sendText( String openId, String text) {
        JSONObject body = new JSONObject();
        body.put("content", emojiConverter.convertNim(text));

        JSONObject json = new JSONObject();
        json.put(TAG_TO_USER, openId);
        json.put(TAG_MSG_TYPE, "text");
        json.put("text", body);

        String sendStr = json.toJSONString();

        replyMessage(sendStr, "replyText");
    }

    private String msgUrl() {
        return Constants.WX_MSG_URL + "?access_token=" + wxAuthService.queryAccessToken();
    }

    private void replyMessage(String sendStr, String func) {

        String msgUrl = msgUrl();
        try {
            String ret = null;
            for (int i = 0; i <= defaultRetryTimes; i++) {
                try {
                    ret = HttpClientPool.getInstance().post(msgUrl, sendStr);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    logger.warn("post request exception: " + ex);
                }
                if (ret != null) break;
            }

            if (ret == null) {
                logger.warn(String.format("[wx] failed and retry !! sendStr = %s", sendStr));
            }
            logger.debug(String.format("[%s] url=%s, send=%s, ret=%s", func, msgUrl, sendStr, ret));
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.warn("replyMessage error: " + ex.toString());
        }
    }

}
