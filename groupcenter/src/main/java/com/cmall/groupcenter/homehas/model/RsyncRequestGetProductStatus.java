package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncRequest;

public class RsyncRequestGetProductStatus implements IRsyncRequest {

	/**
	 * 调用子系统
	 */
	private String subsystem = "";
	/**
	 * 调用用户
	 */
	private String account = "";
	/**
	 * 调用密码
	 */
	private String password = "";

	/**
	 * 商品信息(必填)
	 */
	private List<RsyncModelProductStatus> good_info = new ArrayList<RsyncModelProductStatus>();

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

	public List<RsyncModelProductStatus> getGood_info() {
		return good_info;
	}

	public void setGood_info(List<RsyncModelProductStatus> good_info) {
		this.good_info = good_info;
	}
	

}
