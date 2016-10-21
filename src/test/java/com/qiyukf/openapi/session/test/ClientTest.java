package com.qiyukf.openapi.session.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.qiyukf.openapi.session.SessionClient;
import com.qiyukf.openapi.session.model.ApplyStaffInfo;
import com.qiyukf.openapi.session.model.ApplyStaffResult;
import com.qiyukf.openapi.session.model.CommonResult;
import org.testng.annotations.Test;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by zhoujianghua on 2016/10/11.
 */
public class ClientTest {

    private static final String appKey = "1064deea1c3624c9ee26d1de5ce8481f";
    private static final String foreignId = "aa#";
    private static final String appSecret = "FA5846BDCDE14DB093CF0D6D49FB9BA6";

    private SessionClient client = new SessionClient(appKey, appSecret);

    private long sessionId;

    @Test(priority = 1)
    public void testApplyStaff() throws IOException {
        ApplyStaffInfo info = new ApplyStaffInfo();
        info.setFromPage("http://163.com");
        info.setFromTitle("网易首页");
        info.setUid(foreignId);
        info.setProductId("com.163");
        info.setDeviceType("Web#Windows#10#Chrome");
        info.setStaffType(1);

        ApplyStaffResult result = client.applyStaff(info);
        if (result.getCode() == 200 && result.getSession().getStaffType() == 1) {
            sessionId = result.getSession().getSessionId();
        }

        out("applyStaff", JSON.toJSONString(result));
    }

    @Test(priority = 2)
    public void testSendTextMsg() throws IOException {
        out("send-text-message", client.sendTextMessage(foreignId, "test1"));
        out("text-anti-spam", client.sendTextMessage(foreignId, "习近平"));

        // 超长的消息
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4000; i++) {
            sb.append(i);
        }
        out("text-too-long", client.sendTextMessage(foreignId, sb.toString()));
    }

    @Test(priority = 3)
    public void testSendImageMsg() throws IOException, NoSuchAlgorithmException {
        out("normal image", client.sendImageMessage(foreignId, "e:/yyy.png", 108, 108));
        out("image without size", client.sendImageMessage(foreignId, "e:/zzz.png", 0, 0));
        out("not image", client.sendImageMessage(foreignId, "e:/xx.txt", 0, 0));
    }

    @Test(expectedExceptions = FileNotFoundException.class, priority = 4)
    public void testSendImageNotExist() throws IOException, NoSuchAlgorithmException {
        out("image not exist", client.sendImageMessage(foreignId, "e:/xeex.txt", 0, 0));
    }

    @Test(priority = 5)
    public void testSendAudioMsg() throws UnsupportedAudioFileException, IOException, LineUnavailableException, NoSuchAlgorithmException {
        out("amr audio", client.sendAudioMessage(foreignId, "e:/xxx.amr", 10000));
        out("wam audio", client.sendAudioMessage(foreignId, "e:/yyy.wma", 150000));
    }

    @Test(expectedExceptions = UnsupportedAudioFileException.class, priority = 6)
    public void testSendAudioMsgNotAudio() throws UnsupportedAudioFileException, IOException, LineUnavailableException, NoSuchAlgorithmException {
        out("testSendAudioMsgNotAudio", client.sendAudioMessage(foreignId, "e:/xx.txt", 0));
    }

    @Test(expectedExceptions = FileNotFoundException.class, priority = 7)
    public void testSendAudioNotExist() throws IOException, LineUnavailableException, UnsupportedAudioFileException, NoSuchAlgorithmException {
        out("testSendAudioNotExist", client.sendAudioMessage(foreignId, "e:/xx44.txt", 0));
    }

    @Test(priority = 8)
    public void testEvaluation() throws IOException {
        if (sessionId != 0) {
            out("testEvaluation", client.evaluate(foreignId, sessionId, 50));
        }
    }

    @Test(priority = 9)
    public void testGetQueueStatus() throws IOException {
        out("getQueueStatus", client.getQueueStatus(foreignId));
    }

    @Test(priority = 10)
    public void testCmrInfo() {
        JSONArray crm = JSONArray.parseArray("[{\"value\": \"abc11@163.com\", \"key\": \"email\"}, {\"index\": 5, \"value\": \"test\", \"key\": \"xyz\", \"label\": \"xyz11\"}]");

        try {
            CommonResult result = client.updateCrmInfo(foreignId, crm);
            out("crminfo", result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 11)
    public void testForwardWxMessage() {

    }

    private void out(String tc, Object result) {
        System.out.println("------" + tc + "------");
        System.out.println(JSON.toJSONString(result));
    }
}
