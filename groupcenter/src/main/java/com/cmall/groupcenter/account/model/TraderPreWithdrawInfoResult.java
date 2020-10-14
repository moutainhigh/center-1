package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * Created with cgroup
 *
 * @author lipengfei
 * @date 2015-10-09
 * @time 17:25
 * @email:lipengfei217@163.com
 */
public class TraderPreWithdrawInfoResult extends RootResultWeb {


    @ZapcomApi(value = "第一次提醒是否启用", remark = "0：未启用，1：已启用", demo = "0")
    private String firstNotifyOpen;
    @ZapcomApi(value = "第一次提醒是否已提醒", remark = "0：未提醒 1：已提醒", demo = "0")
    private String firstNotifyFlag;
    @ZapcomApi(value = "第一次提醒时间", remark = "即提醒的天数", demo = "5（天）")
    private String firstNotify;


    @ZapcomApi(value = "第二次提醒时间", remark = "即提醒的天数", demo = "3（天）")
    private String secondNotify;

    @ZapcomApi(value = "第二次提醒时间是否启用", remark = "0：未启用，1：已启用", demo = "0")
    private String secondNotifyOpen;

    @ZapcomApi(value = "第二次提醒时间是否已提醒", remark = "0：未提醒 1：已提醒", demo = "0")
    private String secondNotifyFlag;

    @ZapcomApi(value = "停止返利时间", remark = "即提醒的天数", demo = "1（天）")
    private String stopRebateNotify;

    @ZapcomApi(value = "停止返利时间是否启用", remark = "0：未启用，1：已启用", demo = "0")
    private String stopRebateNotifyOpen;

    @ZapcomApi(value = "停止返利时间是否已提醒", remark = "0：未提醒 1：已提醒", demo = "0")
    private String stopRebateNotifyFlag;


    @ZapcomApi(value = "余额不足提醒", remark = "余额不足时进行提醒", demo = "30（元）")
    private String firstUnenoughBalNotify;

    @ZapcomApi(value = "余额不足提醒(是否启用", remark = "0：未启用，1：已启用", demo = "0")
    private String firstUnenoughBalNotifyOpen;

    @ZapcomApi(value = "余额不足提醒是否已提醒", remark = "0：未提醒 1：已提醒", demo = "0")
    private String firstUnenoughBalFlag;

    @ZapcomApi(value = "停止返利金额(元)", remark = "余额不足时停止返利", demo = "1（元）")
    private String secUnenoughBalNotify;

    @ZapcomApi(value = "停止返利金额(元)是否启用", remark = "0：未启用，1：已启用", demo = "0")
    private String secUnenoughBalNotifyOpen;

    @ZapcomApi(value = "停止返利金额(元)是否已提醒", remark = "0：未提醒 1：已提醒", demo = "0")
    private String secUnenoughBalFlag;


    @ZapcomApi(value = "创建时间", remark = "预警设置的数据的创建时间", demo = "2015-03-02 11:11:11")
    private String createTime;

    @ZapcomApi(value = "短信提醒", remark = "是否启用短信提醒,0：未启用，1：已启用", demo = "0")
    private String sendMessageFlag;

    @ZapcomApi(value = "邮件提醒", remark = "是否启用邮件提醒,0：未启用，1：已启用", demo = "0")
    private String sendMailFlag;

    public String getFirstNotifyOpen() {
        return firstNotifyOpen;
    }

    public void setFirstNotifyOpen(String firstNotifyOpen) {
        this.firstNotifyOpen = firstNotifyOpen;
    }

    public String getSecondNotifyOpen() {
        return secondNotifyOpen;
    }

    public void setSecondNotifyOpen(String secondNotifyOpen) {
        this.secondNotifyOpen = secondNotifyOpen;
    }

    public String getStopRebateNotifyOpen() {
        return stopRebateNotifyOpen;
    }

    public void setStopRebateNotifyOpen(String stopRebateNotifyOpen) {
        this.stopRebateNotifyOpen = stopRebateNotifyOpen;
    }

    public String getFirstNotifyFlag() {
        return firstNotifyFlag;
    }

    public void setFirstNotifyFlag(String firstNotifyFlag) {
        this.firstNotifyFlag = firstNotifyFlag;
    }

    public String getFirstNotify() {
        return firstNotify;
    }

    public void setFirstNotify(String firstNotify) {
        this.firstNotify = firstNotify;
    }

    public String getSecondNotify() {
        return secondNotify;
    }

    public void setSecondNotify(String secondNotify) {
        this.secondNotify = secondNotify;
    }

    public String getSecondNotifyFlag() {
        return secondNotifyFlag;
    }

    public void setSecondNotifyFlag(String secondNotifyFlag) {
        this.secondNotifyFlag = secondNotifyFlag;
    }

    public String getStopRebateNotify() {
        return stopRebateNotify;
    }

    public void setStopRebateNotify(String stopRebateNotify) {
        this.stopRebateNotify = stopRebateNotify;
    }

    public String getStopRebateNotifyFlag() {
        return stopRebateNotifyFlag;
    }

    public void setStopRebateNotifyFlag(String stopRebateNotifyFlag) {
        this.stopRebateNotifyFlag = stopRebateNotifyFlag;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSendMessageFlag() {
        return sendMessageFlag;
    }

    public void setSendMessageFlag(String sendMessageFlag) {
        this.sendMessageFlag = sendMessageFlag;
    }

    public String getSendMailFlag() {
        return sendMailFlag;
    }

    public void setSendMailFlag(String sendMailFlag) {
        this.sendMailFlag = sendMailFlag;
    }

    public String getFirstUnenoughBalNotify() {
        return firstUnenoughBalNotify;
    }

    public void setFirstUnenoughBalNotify(String firstUnenoughBalNotify) {
        this.firstUnenoughBalNotify = firstUnenoughBalNotify;
    }

    public String getFirstUnenoughBalNotifyOpen() {
        return firstUnenoughBalNotifyOpen;
    }

    public void setFirstUnenoughBalNotifyOpen(String firstUnenoughBalNotifyOpen) {
        this.firstUnenoughBalNotifyOpen = firstUnenoughBalNotifyOpen;
    }

    public String getFirstUnenoughBalFlag() {
        return firstUnenoughBalFlag;
    }

    public void setFirstUnenoughBalFlag(String firstUnenoughBalFlag) {
        this.firstUnenoughBalFlag = firstUnenoughBalFlag;
    }

    public String getSecUnenoughBalNotify() {
        return secUnenoughBalNotify;
    }

    public void setSecUnenoughBalNotify(String secUnenoughBalNotify) {
        this.secUnenoughBalNotify = secUnenoughBalNotify;
    }

    public String getSecUnenoughBalNotifyOpen() {
        return secUnenoughBalNotifyOpen;
    }

    public void setSecUnenoughBalNotifyOpen(String secUnenoughBalNotifyOpen) {
        this.secUnenoughBalNotifyOpen = secUnenoughBalNotifyOpen;
    }

    public String getSecUnenoughBalFlag() {
        return secUnenoughBalFlag;
    }

    public void setSecUnenoughBalFlag(String secUnenoughBalFlag) {
        this.secUnenoughBalFlag = secUnenoughBalFlag;
    }
}
