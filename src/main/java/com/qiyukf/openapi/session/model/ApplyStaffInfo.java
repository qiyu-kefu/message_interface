package com.qiyukf.openapi.session.model;

import java.io.Serializable;

/**
 * 用户请求分配客服时所需的信息，包括用户的信息和要请求的客服类型信息。
 * 用户的信息用于展示给客服，供客服使用
 */
public class ApplyStaffInfo implements Serializable {

    /**
     * 请求分配客服的用户ID
     */
    private String uid;

    /**
     * 咨询页面的uri，可以是url，也可以任意能标识来源的字符串
     */
    private String fromPage;

    /**
     * 咨询页面的title
     */
    private String fromTitle;

    /**
     * 用户的IP地址
     */
    private String fromIp;

    /**
     * 用户的设备信息, 形如"Android#xiaomi#5.0"
     */
    private String deviceType;

    /**
     * 产品的Id，例如包名，如com.qiyukf.openapi
     */
    private String productId;

    /**
     * 请求分配的客服类型，取值如下：
     * 0：如果企业开通了机器人，则分配机器人，否则分配人工
     * 1：分配人工客服
     * 默认值为0.如果指定了groupId或者staffId, 则忽略这个参数
     */
    private int staffType;

    /**
     * 请求分配客服时，指定客服分组。<br>
     * 分组 ID 可在管理后台的 「设置」-&gt;「访客分流」中查询
     */
    private long groupId;

    /**
     * 请求分配客服时，指定客服ID。如果指定了此参数，则groupId会被忽略。<br>
     * 客服 ID 可在管理后台的 「设置」-&gt;「访客分流」中查询
     */
    private long staffId;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFromPage() {
        return fromPage;
    }

    public void setFromPage(String fromPage) {
        this.fromPage = fromPage;
    }

    public String getFromTitle() {
        return fromTitle;
    }

    public void setFromTitle(String fromTitle) {
        this.fromTitle = fromTitle;
    }

    public String getFromIp() {
        return fromIp;
    }

    public void setFromIp(String fromIp) {
        this.fromIp = fromIp;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getStaffType() {
        return staffType;
    }

    public void setStaffType(int staffType) {
        this.staffType = staffType;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getStaffId() {
        return staffId;
    }

    public void setStaffId(long staffId) {
        this.staffId = staffId;
    }
}
