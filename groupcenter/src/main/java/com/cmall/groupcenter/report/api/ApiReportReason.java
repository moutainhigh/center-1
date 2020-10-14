package com.cmall.groupcenter.report.api;

import java.util.List;
import java.util.Map;

import com.cmall.groupcenter.report.model.ReportReasonResult;
import com.cmall.groupcenter.report.model.ReportReasonResultList;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

public class ApiReportReason extends RootApiForToken<ReportReasonResult, RootInput>{

	@Override
	public ReportReasonResult Process(RootInput inputParam, MDataMap mRequestMap) {
		ReportReasonResult result = new ReportReasonResult();
		List<Map<String, Object>> list = DbUp.upTable("gc_report_reason").dataSqlList("select zid, report_reason from gc_report_reason where is_delete ='4497472000070002'", new MDataMap());
		for(int index=0; index<list.size(); index++) {
			ReportReasonResultList resultList = new ReportReasonResultList();
			resultList.setId(String.valueOf(list.get(index).get("zid")));
			resultList.setReason(String.valueOf(list.get(index).get("report_reason")));
			result.getList().add(resultList);
		}
		result.setResultCode(1);
		return result;
	}

}