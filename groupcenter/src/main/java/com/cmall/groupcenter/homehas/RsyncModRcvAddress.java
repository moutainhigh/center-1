package com.cmall.groupcenter.homehas;

import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncRequest;
import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncBase;
import com.cmall.groupcenter.homehas.model.RsyncResponseBase;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 修改下单收货地址或sku信息接口
 */
public class RsyncModRcvAddress extends RsyncHomeHas<RsyncModRcvAddress.RsyncConfig, RsyncModRcvAddress.RsyncRequest, RsyncModRcvAddress.RsyncResponse> {

	final static RsyncConfig CONFIG = new RsyncConfig();
	
	private RsyncRequest tRequest = new RsyncRequest();
	private RsyncResponse tResponse = new RsyncResponse();

	public RsyncConfig upConfig() {
		return CONFIG;
	}
	
	public RsyncRequest upRsyncRequest() {
		return tRequest;
	}
	
	public RsyncResult doProcess(RsyncRequest tRequest, RsyncResponse tResponse) {
		this.tResponse = tResponse;
		
		RsyncResult result = new RsyncResult();
		if (!"true".equals(tResponse.getSuccess())) {
			result.setResultCode(0);
			result.setResultMessage(tResponse.getMessage());
		}
		return result;
	}

	public RsyncResponse upResponseObject() {
		return tResponse;
	}
	
	public static class RsyncConfig extends RsyncConfigRsyncBase {

		@Override
		public String getRsyncTarget() {
			return "modRcvAddress";
		}
	}
	
	public static class RsyncRequest implements IRsyncRequest {
		private String rcver_nm;
		private String mobile;
		private String srgn_cd;
		private String send_addr;
		private String ord_id;
		private List<GoodInfo> good_info;
		
		public String getRcver_nm() {
			return rcver_nm;
		}
		public void setRcver_nm(String rcver_nm) {
			this.rcver_nm = rcver_nm;
		}
		public String getMobile() {
			return mobile;
		}
		public void setMobile(String mobile) {
			this.mobile = mobile;
		}
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
		public String getOrd_id() {
			return ord_id;
		}
		public void setOrd_id(String ord_id) {
			this.ord_id = ord_id;
		}
		public List<GoodInfo> getGood_info() {
			return good_info;
		}
		public void setGood_info(List<GoodInfo> good_info) {
			this.good_info = good_info;
		}
		
	}
	
	public static class RsyncResponse extends RsyncResponseBase {

		
	}
	
	public static class GoodInfo {
		private String good_id;
		private String color_id;
		private String style_id;
		private String color_id_old;
		private String style_id_old;
		
		public String getGood_id() {
			return good_id;
		}
		public void setGood_id(String good_id) {
			this.good_id = good_id;
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
		public String getColor_id_old() {
			return color_id_old;
		}
		public void setColor_id_old(String color_id_old) {
			this.color_id_old = color_id_old;
		}
		public String getStyle_id_old() {
			return style_id_old;
		}
		public void setStyle_id_old(String style_id_old) {
			this.style_id_old = style_id_old;
		}
		
	}
}
