package com.qiyukf.openapi.session.model;

import java.util.List;

/**
 * 客服与用户的一次会话的信息。
 */
public class Session {

    /**
     * 会话的ID
     */
    private long sessionId;

    /**
     * 接待的客服的名字
     */
    private String staffName;

    /**
     * 接待的客服的类型，0表示机器人，1表示人工
     */
    private int staffType;

    /**
     * 客服头像的url
     */
    private String staffIcon;

    /**
     * 会话中的访客的uid
     */
    private String uid;

    /**
     * 如果此会话由其他会话转接而来，此字段有值，表示转接来源会话的ID
     */
    private Long transferFrom;

    /**
     * 如果会话已经关闭，此字段有值，表示关闭类型。
     * 其中，0表示客服关闭，2表示用户长时间不说话，自动关闭，3表示有机器人转到人工会话了，4表示客服离开导致关闭，5表示转接会话。
     */
    private Integer closeType;

    /**
     * 如果closeType为5，此字段有值，表示转接到的会话的ID
     */
    private Long transferTo;

    /**
     * 此会话的用户评价模型，
     */
    private SatisfactionSetting evaluationModel;

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public int getStaffType() {
        return staffType;
    }

    public void setStaffType(int staffType) {
        this.staffType = staffType;
    }

    public String getStaffIcon() {
        return staffIcon;
    }

    public void setStaffIcon(String staffIcon) {
        this.staffIcon = staffIcon;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getCloseType() {
        return closeType;
    }

    public void setCloseType(Integer closeType) {
        this.closeType = closeType;
    }

    public Long getTransferFrom() {
        return transferFrom;
    }

    public void setTransferFrom(Long transferFrom) {
        this.transferFrom = transferFrom;
    }

    public Long getTransferTo() {
        return transferTo;
    }

    public void setTransferTo(Long transferTo) {
        this.transferTo = transferTo;
    }

    public SatisfactionSetting getEvaluationModel() {
        return evaluationModel;
    }

    public void setEvaluationModel(SatisfactionSetting evaluationModel) {
        this.evaluationModel = evaluationModel;
    }

    /**
     * 用户满意度评价模型配置
     */
    public static class SatisfactionSetting {

        /**
         * 评价名
         */
        private String title;
        /**
         * 评价提示语
         */
        private String note;
        /**
         * 评价模型类型， 对应于管理后台的设置，其值等于评价项的个数
         */
        private int type;

        /**
         * 评价项详情列表
         */
        private List<SatisfactionEntry> list;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public List<SatisfactionEntry> getList() {
            return list;
        }

        public void setList(List<SatisfactionEntry> list) {
            this.list = list;
        }
    }

    public static class SatisfactionEntry {
        /**
         * 评价项描述文字，例如满意，不满意等
         */
        private String name;
        /**
         * 该评价项对应的分数
         */
        private int value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
