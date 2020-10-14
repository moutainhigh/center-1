package com.cmall.groupcenter.report.api;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.cmall.groupcenter.report.model.ReportReasonInput;
import com.cmall.groupcenter.report.model.ReportResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

public class ApiReport extends RootApiForToken<ReportResult, ReportReasonInput>{

	@Override
	public ReportResult Process(ReportReasonInput inputParam, MDataMap mRequestMap) {
		ReportResult result = new ReportResult();
		int num = DbUp.upTable("gc_report_black").count("be_report_user", inputParam.getBe_report_user());
		if(num==0) {
			MDataMap insertMap = new MDataMap();
			insertMap.put("be_report_user", inputParam.getBe_report_user());
			insertMap.put("report_num", "1");
			insertMap.put("flag_black", "4497472000050002");
			DbUp.upTable("gc_report_black").dataInsert(insertMap);
		} else {
			MDataMap insertMap = new MDataMap();
			
			insertMap.put("be_report_user", inputParam.getBe_report_user());
			
			DbUp.upTable("gc_report_black").dataExec("UPDATE gc_report_black SET report_num = report_num+1 WHERE be_report_user =:be_report_user ", insertMap);
		}
		MDataMap insertMap = new MDataMap();
		insertMap.put("be_report_user", inputParam.getBe_report_user());
		insertMap.put("report_user", inputParam.getReport_user());
		insertMap.put("report_reason", inputParam.getReport_reason_id());
		insertMap.put("supply_reason", inputParam.getSupply_reason());
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		insertMap.put("report_time", format.format(new Date()));
		DbUp.upTable("gc_report_details").dataInsert(insertMap);
		result.setResultCode(1);
		result.setResultMessage("举报成功了");
		return result;
	}

}
