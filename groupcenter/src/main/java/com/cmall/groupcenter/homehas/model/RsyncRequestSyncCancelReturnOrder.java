package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 查询取消销退订单
 * @author lipengfei
 * @date 2015-09-10
 * @time 14:49
 */
public class RsyncRequestSyncCancelReturnOrder implements IRsyncRequest {

//	调用子系统
	private String subsystem = "";

//	调用用户
	private String account = "";

//	调用密码
	private String password = "";

    //指定通路
    private String medi_mclss_id="";

//	开始时间
	private String beginTime = "";

//	结束时间
	private String endTime = "";

//	开始数目
	private String start = "";

//	每页显示数目
	private String limit = "";

    public String getSubsystem() {
        return subsystem;
    }

    public void setSubsystem(String subsystem) {
        this.subsystem = subsystem;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getMedi_mclss_id() {
        return medi_mclss_id;
    }

    public void setMedi_mclss_id(String medi_mclss_id) {
        this.medi_mclss_id = medi_mclss_id;
    }
}
