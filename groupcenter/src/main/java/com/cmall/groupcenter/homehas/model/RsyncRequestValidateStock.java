package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncRequest;

public class RsyncRequestValidateStock implements IRsyncRequest {

	/**
	 * 商品信息，多个
	 */
	private List<GoodInfo> good_info = new ArrayList<RsyncRequestValidateStock.GoodInfo>();
	/**
	 * 收货人省
	 */
	private String laddr;
	/**
	 * 收货人市
	 */
	private String maddr;
	/**
	 * 收货人区
	 */
	private String saddr;
	/**
	 * 收货人详细地址
	 */
	private String send_addr;
	/**
	 * 收货人地址行政区划
	 */
	private String srgn_cd;
	/**
	 * 邮编
	 */
	private String zip_no;
	/**
	 * 是否货到付款方式  True/false
	 */
	private String pay_type;
	
	public List<GoodInfo> getGood_info() {
		return good_info;
	}

	public void setGood_info(List<GoodInfo> good_info) {
		this.good_info = good_info;
	}

	public String getLaddr() {
		return laddr;
	}

	public void setLaddr(String laddr) {
		this.laddr = laddr;
	}

	public String getMaddr() {
		return maddr;
	}

	public void setMaddr(String maddr) {
		this.maddr = maddr;
	}

	public String getSaddr() {
		return saddr;
	}

	public void setSaddr(String saddr) {
		this.saddr = saddr;
	}

	public String getSend_addr() {
		return send_addr;
	}

	public void setSend_addr(String send_addr) {
		this.send_addr = send_addr;
	}

	public String getSrgn_cd() {
		return srgn_cd;
	}

	public void setSrgn_cd(String srgn_cd) {
		this.srgn_cd = srgn_cd;
	}

	public String getZip_no() {
		return zip_no;
	}
	
	public void setZip_no(String zip_no) {
		this.zip_no = zip_no;
	}
	
	public String getPay_type() {
		return pay_type;
	}

	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}



	public static class GoodInfo {
		
		private String good_id;
		private String good_cnt;
		private String color_id;
		private String style_id;
		
		public String getGood_id() {
			return good_id;
		}
		public void setGood_id(String good_id) {
			this.good_id = good_id;
		}
		public String getGood_cnt() {
			return good_cnt;
		}
		public void setGood_cnt(String good_cnt) {
			this.good_cnt = good_cnt;
		}
		public String getColor_id() {
			return color_id;
		}
		public void setColor_id(String color_id) {
			this.color_id = color_id;
		}
		public String getStyle_id() {
			return style_id;
		}
		public void setStyle_id(String style_id) {
			this.style_id = style_id;
		}
		
	}
}
