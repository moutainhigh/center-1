package com.cmall.groupcenter.report.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class ReportReasonResultList {
	@ZapcomApi(value="主键",remark="主键",require=1)
	private String id;
	@ZapcomApi(value="原因",remark="原因",require=1)
	private String reason;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
}