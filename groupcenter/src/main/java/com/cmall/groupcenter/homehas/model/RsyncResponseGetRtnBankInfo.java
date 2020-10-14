package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncResponse;

public class RsyncResponseGetRtnBankInfo implements IRsyncResponse {

	private boolean success;
	private String message;
	private List<RtnBankInfo> resultList = new ArrayList<RsyncResponseGetRtnBankInfo.RtnBankInfo>();
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<RtnBankInfo> getResultList() {
		return resultList;
	}
	public void setResultList(List<RtnBankInfo> resultList) {
		this.resultList = resultList;
	}

	public static class RtnBankInfo {
		private String ord_id;
		private String ord_seq;
		private String web_ord_id;
		private String pay_no;
		private String pay_money;
		public String getOrd_id() {
			return ord_id;
		}
		public void setOrd_id(String ord_id) {
			this.ord_id = ord_id;
		}
		public String getOrd_seq() {
			return ord_seq;
		}
		public void setOrd_seq(String ord_seq) {
			this.ord_seq = ord_seq;
		}
		public String getWeb_ord_id() {
			return web_ord_id;
		}
		public void setWeb_ord_id(String web_ord_id) {
			this.web_ord_id = web_ord_id;
		}
		public String getPay_no() {
			return pay_no;
		}
		public void setPay_no(String pay_no) {
			this.pay_no = pay_no;
		}
		public String getPay_money() {
			return pay_money;
		}
		public void setPay_money(String pay_money) {
			this.pay_money = pay_money;
		}
		
	}
}