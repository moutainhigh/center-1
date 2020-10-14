package com.cmall.groupcenter.third.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GetTraderInfoResult  extends RootResultWeb{

	@ZapcomApi(value = "保证金金额", remark = "gurrantee_balance")
	private String gurrantee_balance = "";

	@ZapcomApi(value = "商户状态", remark = "trader_status")
	private String trader_status = "";

	@ZapcomApi(value = "入驻时间", remark = "create_time")
	private String create_time = "";

	@ZapcomApi(value = "上次登录时间", remark = "last_login_date")
	private String last_login_date = "";
	
	@ZapcomApi(value = "商户名称", remark = "trader_name")
	private String trader_name = "";
	
	@ZapcomApi(value = "商户形象", remark = "trader_pic_url")
	private String trader_pic_url = "";

    @ZapcomApi(value = "开通功能", remark = "开通功能，多个功能以逗号隔开")
	private String activate_operation = "";
	
	@ZapcomApi(value = "uid", remark = "uid")
	private String uid = "";
	
	public String getGurrantee_balance() {
		return gurrantee_balance;
	}

	public void setGurrantee_balance(String gurrantee_balance) {
		this.gurrantee_balance = gurrantee_balance;
	}

	public String getTrader_status() {
		return trader_status;
	}

	public void setTrader_status(String trader_status) {
		this.trader_status = trader_status;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getLast_login_date() {
		return last_login_date;
	}

	public void setLast_login_date(String last_login_date) {
		this.last_login_date = last_login_date;
	}

	public String getTrader_name() {
		return trader_name;
	}

	public void setTrader_name(String trader_name) {
		this.trader_name = trader_name;
	}

	public String getTrader_pic_url() {
		return trader_pic_url;
	}

	public void setTrader_pic_url(String trader_pic_url) {
		this.trader_pic_url = trader_pic_url;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}


    public String getActivate_operation() {
        return activate_operation;
    }

    public void setActivate_operation(String activate_operation) {
        this.activate_operation = activate_operation;
    }
}
