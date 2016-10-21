package com.qiyukf.openapi.controller.wxservice;

import com.alibaba.fastjson.JSONObject;
import com.qiyukf.openapi.controller.Constants;
import com.qiyukf.openapi.session.util.HttpClientPool;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhoujianghua on 2015/10/10.
 *
 * 微信第三方开发平台授权登录流程：
 * <p>
 * 1.
 */
@Service("wxAuthService")
public class WXAuthService {

    private static Logger logger = Logger.getLogger(WXAuthService.class);

    private String accessToken;

    private long expireTime;

    private AtomicBoolean fetching = new AtomicBoolean(false);

    /**
     * 获取第三方公众号的access token
     * @return 第三方公众号的access token
     */
    public String queryAccessToken() {
        if (expireTime < System.currentTimeMillis()) {
            updateAccessTokenFromWx();
        }
        return accessToken;
    }

    private void updateAccessTokenFromWx() {
        if (!fetching.compareAndSet(false, true)) {
            return;
        }
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
        url = String.format(url, Constants.WX_APP_ID, Constants.WX_APP_SECRET);
        try {
            String ret = HttpClientPool.getInstance().get(url);
            JSONObject json = JSONObject.parseObject(ret);
            accessToken = json.getString("access_token");
            expireTime = System.currentTimeMillis() + json.getIntValue("expires_in");
        } catch (IOException e) {
            logger.debug("query accessToken error: " + e);
        } finally {
            fetching.set(false);
        }
    }
}
