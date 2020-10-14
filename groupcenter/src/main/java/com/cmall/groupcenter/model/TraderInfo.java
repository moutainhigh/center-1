package com.cmall.groupcenter.model;

import java.math.BigDecimal;

public class TraderInfo {
    /** 
     * 
     * @Author  lipengfei
    **/
    private Integer zid;

    /** 
     * 
     * @Author  lipengfei
    **/
    private String uid;
          
    /** 
     * 商户编号
     * @Author  lipengfei
    **/
    private String traderCode;

    /** 
     * 商户名称
     * @Author  lipengfei
    **/
    private String traderName;

    /** 
     * 账户编号
     * @Author  lipengfei
    **/
    private String accountCode;

    /** 
     * 登录账号
     * @Author  lipengfei
    **/
    private String loginAccount;

    /** 
     * 商户状态
     * @Author  lipengfei
    **/
    private String traderStatus;

    /** 
     * 商户形象,即上传的头像
     * @Author  lipengfei
    **/
    private String traderPicUrl;

    /** 
     * 保证金金额,即当前的保证金余额
     * @Author  lipengfei
    **/
    private BigDecimal gurranteeBalance;

    /** 
     * 电子邮箱
     * @Author  lipengfei
    **/
    private String traderEmail;

    /** 
     * 联系地址
     * @Author  lipengfei
    **/
    private String relationAddress;

    /** 
     * 移动电话
     * @Author  lipengfei
    **/
    private String telephone;

    /** 
     * 联系人姓名
     * @Author  lipengfei
    **/
    private String relationReasonName;

    /** 
     * 固定电话号码
     * @Author  lipengfei
    **/
    private String phoneNumber;

    /** 
     * 上次登录时间
     * @Author  lipengfei
    **/
    private String lastLoginDate;

    /** 
     * 商户创建人
     * @Author  lipengfei
    **/
    private String createUser;

    /** 
     * 入驻时间
     * @Author  lipengfei
    **/
    private String createTime;

    public Integer getZid() {
        return zid;
    }

    public void setZid(Integer zid) {
        this.zid = zid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid == null ? null : uid.trim();
    }

    public String getTraderCode() {
        return traderCode;
    }

    public void setTraderCode(String traderCode) {
        this.traderCode = traderCode == null ? null : traderCode.trim();
    }

    public String getTraderName() {
        return traderName;
    }

    public void setTraderName(String traderName) {
        this.traderName = traderName == null ? null : traderName.trim();
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode == null ? null : accountCode.trim();
    }

    public String getLoginAccount() {
        return loginAccount;
    }

    public void setLoginAccount(String loginAccount) {
        this.loginAccount = loginAccount == null ? null : loginAccount.trim();
    }

    public String getTraderStatus() {
        return traderStatus;
    }

    public void setTraderStatus(String traderStatus) {
        this.traderStatus = traderStatus == null ? null : traderStatus.trim();
    }

    public String getTraderPicUrl() {
        return traderPicUrl;
    }

    public void setTraderPicUrl(String traderPicUrl) {
        this.traderPicUrl = traderPicUrl == null ? null : traderPicUrl.trim();
    }

    public BigDecimal getGurranteeBalance() {
        return gurranteeBalance;
    }

    public void setGurranteeBalance(BigDecimal gurranteeBalance) {
        this.gurranteeBalance = gurranteeBalance;
    }

    public String getTraderEmail() {
        return traderEmail;
    }

    public void setTraderEmail(String traderEmail) {
        this.traderEmail = traderEmail == null ? null : traderEmail.trim();
    }

    public String getRelationAddress() {
        return relationAddress;
    }

    public void setRelationAddress(String relationAddress) {
        this.relationAddress = relationAddress == null ? null : relationAddress.trim();
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone == null ? null : telephone.trim();
    }

    public String getRelationReasonName() {
        return relationReasonName;
    }

    public void setRelationReasonName(String relationReasonName) {
        this.relationReasonName = relationReasonName == null ? null : relationReasonName.trim();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber == null ? null : phoneNumber.trim();
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate == null ? null : lastLoginDate.trim();
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser == null ? null : createUser.trim();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }
}