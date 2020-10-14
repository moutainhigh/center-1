package com.cmall.groupcenter.homehas;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncRequest;
import com.cmall.groupcenter.groupface.IRsyncResponse;
import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncBase;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 4.58.折扣券发送短信接口
 */
public class RsyncSendHjyDiscountSms extends RsyncHomeHas<RsyncSendHjyDiscountSms.RsyncConfigSendHjyDiscountSms, RsyncSendHjyDiscountSms.RsyncRequestSendHjyDiscountSms, RsyncSendHjyDiscountSms.RsyncResponseSendHjyDiscountSms> {

	final static RsyncConfigSendHjyDiscountSms CONFIG = new RsyncConfigSendHjyDiscountSms();
	
	private RsyncRequestSendHjyDiscountSms tRequest = new RsyncRequestSendHjyDiscountSms();
	private RsyncResponseSendHjyDiscountSms tResponse = new RsyncResponseSendHjyDiscountSms();

	public RsyncConfigSendHjyDiscountSms upConfig() {
		return CONFIG;
	}
	
	public RsyncRequestSendHjyDiscountSms upRsyncRequest() {
		return tRequest;
	}
	
	public RsyncResult doProcess(RsyncRequestSendHjyDiscountSms tRequest, RsyncResponseSendHjyDiscountSms tResponse) {
		this.tResponse = tResponse;
		
		RsyncResult result = new RsyncResult();
		if (!tResponse.isSuccess()) {
			result.setResultCode(0);
			result.setResultMessage("接口调用失败");
		}
		return result;
	}

	public RsyncResponseSendHjyDiscountSms upResponseObject() {
		return tResponse;
	}
	
	public static class RsyncConfigSendHjyDiscountSms extends RsyncConfigRsyncBase {

		@Override
		public String getRsyncTarget() {
			return "sendHjyDiscountSms";
		}
	}
	
	public static class RsyncRequestSendHjyDiscountSms implements IRsyncRequest {
		private List<SmsInfo> items = new ArrayList<RsyncSendHjyDiscountSms.SmsInfo>();

		public List<SmsInfo> getItems() {
			return items;
		}

		public void setItems(List<SmsInfo> items) {
			this.items = items;
		}
	}
	
	public static class RsyncResponseSendHjyDiscountSms implements IRsyncResponse {

		private boolean success;
		private String msg;
		private List<SmsInfo> items = new ArrayList<RsyncSendHjyDiscountSms.SmsInfo>();
		
		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}

		public List<SmsInfo> getItems() {
			return items;
		}

		public void setItems(List<SmsInfo> items) {
			this.items = items;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}
		
	}
	
	public static class SmsInfo {
		private String custId;
		private String phone;
		private String content;
		
		public SmsInfo(String custId, String phone, String content) {
			super();
			this.custId = custId;
			this.phone = phone;
			this.content = content;
		}
		public String getCustId() {
			return custId;
		}
		public void setCustId(String custId) {
			this.custId = custId;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
	}

}
