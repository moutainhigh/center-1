package com.cmall.groupcenter.homehas.model;

public class CouponYth {

	/**
	 * 操作类型
	 * U：使用
	 * R：还原
	 * C：新建
	 */
	private String do_type;
	
	/**
	 * 优惠券编号
	 * 其实就是 活动编号-客代-礼金序号
	 */
	private String lj_code;
	
	/**
	 * 惠家有订单号
	 */
	private String hjy_ord_id;
	
	/**
	 * 惠家有用户编号
	 */
	private String member_code;
	
	/**
	 * 收货人姓名
	 */
	private String address_name;
	
	/**
	 * 手机号
	 */
	private String phone;
	
	/**
	 * 默认收货地址
	 */
	private String address;
	
	/**
	 * 活动编号
	 */
	private String event_id;
	
	/**
	 * 惠家有优惠券编号
	 */
	private String coupon_code;
	
	/**
	 * 折扣类型
	 */
	private String dis_type;
	
	/**
	 * 修改人
	 */
	private String mdf_id;
	
	/**
	 * 礼金剩余金额
	 */
	private String lj_balance_amt;

	public String getDo_type() {
		return do_type;
	}

	public void setDo_type(String do_type) {
		this.do_type = do_type;
	}

	public String getLj_code() {
		return lj_code;
	}

	public void setLj_code(String lj_code) {
		this.lj_code = lj_code;
	}

	public String getHjy_ord_id() {
		return hjy_ord_id;
	}

	public void setHjy_ord_id(String hjy_ord_id) {
		this.hjy_ord_id = hjy_ord_id;
	}

	public String getMember_code() {
		return member_code;
	}

	public void setMember_code(String member_code) {
		this.member_code = member_code;
	}

	public String getAddress_name() {
		return address_name;
	}

	public void setAddress_name(String address_name) {
		this.address_name = address_name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEvent_id() {
		return event_id;
	}

	public void setEvent_id(String event_id) {
		this.event_id = event_id;
	}

	public String getCoupon_code() {
		return coupon_code;
	}

	public void setCoupon_code(String coupon_code) {
		this.coupon_code = coupon_code;
	}

	public String getDis_type() {
		return dis_type;
	}

	public void setDis_type(String dis_type) {
		this.dis_type = dis_type;
	}

	public String getMdf_id() {
		return mdf_id;
	}

	public void setMdf_id(String mdf_id) {
		this.mdf_id = mdf_id;
	}

	public String getLj_balance_amt() {
		return lj_balance_amt;
	}

	public void setLj_balance_amt(String lj_balance_amt) {
		this.lj_balance_amt = lj_balance_amt;
	}
}
