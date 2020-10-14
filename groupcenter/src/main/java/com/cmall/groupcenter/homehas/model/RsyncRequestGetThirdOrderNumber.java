package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 获取LD订单数量
 * @author cc
 *
 */
public class RsyncRequestGetThirdOrderNumber implements IRsyncRequest{

	/**
	 * 会员手机号
	 */
	private String tel = "";

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}
}
