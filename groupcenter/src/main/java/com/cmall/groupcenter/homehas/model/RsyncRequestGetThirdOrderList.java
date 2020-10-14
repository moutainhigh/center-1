package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 5.2.6 LD订单列表
 * @author cc
 *
 */
public class RsyncRequestGetThirdOrderList implements IRsyncRequest {

	/**
	 * 会员手机号
	 */
	private String tel = "";
	
	/**
	 * 订单状态
	 * 全部                '00'
	 * 待付款            '01'
	 * 待发货            '02'
	 * 待收货            '03'
	 * 已完成            '05'
	 */
	private String ord_type = "";
	
	/**
	 * 此字段如果有值 05的订单仅查确认收货30内的
	 */
	private String source = "";

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getOrd_type() {
		return ord_type;
	}

	public void setOrd_type(String ord_type) {
		this.ord_type = ord_type;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
}
