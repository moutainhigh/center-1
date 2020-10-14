package com.cmall.groupcenter.report.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ReportReasonResult extends RootResultWeb {
	@ZapcomApi(value="举报原因列表",remark="举报原因列表",require=1)
	private List<ReportReasonResultList> list = new ArrayList<ReportReasonResultList>();

	public List<ReportReasonResultList> getList() {
		return list;
	}

	public void setList(List<ReportReasonResultList> list) {
		this.list = list;
	}
}