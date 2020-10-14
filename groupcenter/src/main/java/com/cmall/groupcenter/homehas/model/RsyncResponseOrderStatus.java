package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncResponse;

public class RsyncResponseOrderStatus implements IRsyncResponse {

	private boolean success;
	private String message; 
	private List<Result> result=new ArrayList<RsyncResponseOrderStatus.Result>();
	
	public static class Result {
		private String yc_update_time; //更新时间
		private String cod_stat_cd;//配送状态
		private String yc_claim_time;//配送状态更新时间
		private String yc_orderform_status;//订单状态
		public String getYc_update_time() {
			return yc_update_time;
		}
		public void setYc_update_time(String yc_update_time) {
			this.yc_update_time = yc_update_time;
		}
		public String getCod_stat_cd() {
			return cod_stat_cd;
		}
		public void setCod_stat_cd(String cod_stat_cd) {
			this.cod_stat_cd = cod_stat_cd;
		}
		public String getYc_claim_time() {
			return yc_claim_time;
		}
		public void setYc_claim_time(String yc_claim_time) {
			this.yc_claim_time = yc_claim_time;
		}
		public String getYc_orderform_status() {
			return yc_orderform_status;
		}
		public void setYc_orderform_status(String yc_orderform_status) {
			this.yc_orderform_status = yc_orderform_status;
		}
		
	}

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

	public List<Result> getResult() {
		return result;
	}

	public void setResult(List<Result> result) {
		this.result = result;
	}
	
	
}
