package com.cmall.groupcenter.homehas.model;

/**
 * 礼金券信息
 * @author cc
 *
 */
public class RsyncModelGiftVoucher {

	/**
	 * 礼金券编码
	 */
	private String lj_code;
	
	/**
	 * 惠家有订单号
	 */
	private String hjy_ord_id;

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
}
