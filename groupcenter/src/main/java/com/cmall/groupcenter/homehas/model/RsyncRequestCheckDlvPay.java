package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 4.56.查询货到付款地区配置信息接口的请求参数
 */
public class RsyncRequestCheckDlvPay implements IRsyncRequest {

	private String srgn_cd = "";
	private String send_addr = "";
	private String virtual_ord = "N";
	private String medi_mclss_id = "";
	private List<GoodInfo> good_info = new ArrayList<RsyncRequestCheckDlvPay.GoodInfo>();
	
	public String getSrgn_cd() {
		return srgn_cd;
	}

	public void setSrgn_cd(String srgn_cd) {
		this.srgn_cd = srgn_cd;
	}

	public String getSend_addr() {
		return send_addr;
	}

	public void setSend_addr(String send_addr) {
		this.send_addr = send_addr;
	}

	public String getVirtual_ord() {
		return virtual_ord;
	}

	public void setVirtual_ord(String virtual_ord) {
		this.virtual_ord = virtual_ord;
	}

	public String getMedi_mclss_id() {
		return medi_mclss_id;
	}

	public void setMedi_mclss_id(String medi_mclss_id) {
		this.medi_mclss_id = medi_mclss_id;
	}

	public List<GoodInfo> getGood_info() {
		return good_info;
	}

	public void setGood_info(List<GoodInfo> good_info) {
		this.good_info = good_info;
	}

	public static class GoodInfo {
		private String good_id = "";
		private String good_cnt = "";
		private String color_id = "";
		private String style_id = "";
		private String site_no = "";
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
		public String getSite_no() {
			return site_no;
		}
		public void setSite_no(String site_no) {
			this.site_no = site_no;
		}
		
	}
}
