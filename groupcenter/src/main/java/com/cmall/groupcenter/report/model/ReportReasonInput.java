package com.cmall.groupcenter.report.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ReportReasonInput extends RootInput {
	@ZapcomApi(value="被举报人",remark="被举报人",require=1)
	private String be_report_user = "";
	@ZapcomApi(value="举报人",remark="举报人",require=1)
	private String report_user = "";
	@ZapcomApi(value="举报原因",remark="举报原因")
	private String report_reason_id = "";
	@ZapcomApi(value="补充说明",remark="补充说明")
	private String supply_reason = "";
	
	public String getBe_report_user() {
		return be_report_user;
	}
	public void setBe_report_user(String be_report_user) {
		this.be_report_user = be_report_user;
	}
	public String getReport_user() {
		return report_user;
	}
	public void setReport_user(String report_user) {
		this.report_user = report_user;
	}
	public String getReport_reason_id() {
		return report_reason_id;
	}
	public void setReport_reason_id(String report_reason_id) {
		this.report_reason_id = report_reason_id;
	}
	public String getSupply_reason() {
		return supply_reason;
	}
	public void setSupply_reason(String supply_reason) {
		this.supply_reason = supply_reason;
	}
}