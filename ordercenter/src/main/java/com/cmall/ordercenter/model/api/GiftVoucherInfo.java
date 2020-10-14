package com.cmall.ordercenter.model.api;

public class GiftVoucherInfo {

	/**
	 * 惠家有订单号
	 */
	private String hjy_ord_id;
	
	/**
	 * 礼金券编码
	 */
	private String lj_code;

	public String getHjy_ord_id() {
		return hjy_ord_id;
	}

	public void setHjy_ord_id(String hjy_ord_id) {
		this.hjy_ord_id = hjy_ord_id;
	}

	public String getLj_code() {
		return lj_code;
	}

	public void setLj_code(String lj_code) {
		this.lj_code = lj_code;
	}
}
