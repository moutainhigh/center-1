package com.cmall.groupcenter.report.api;

import java.util.Date;

import com.cmall.groupcenter.report.model.ReportBlackInput;
import com.cmall.groupcenter.report.model.ReportBlackResult;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 微公社用户聊天是否冻结
 * @author panwei
 *
 */
public class ApiReportBlack extends RootApiForToken<ReportBlackResult, ReportBlackInput>{

	@Override
	public ReportBlackResult Process(ReportBlackInput inputParam, MDataMap mRequestMap) {
		ReportBlackResult result = new ReportBlackResult();
		MDataMap reportBlackMap=DbUp.upTable("gc_report_black").one("member_code",inputParam.getMember_code());
		if(null!=reportBlackMap&&reportBlackMap.size()>0){
			//判断时间
			Date startTime=DateUtil.toDate(reportBlackMap.get("black_start_time"),DateUtil.DATE_FORMAT_DATETIME);
			Date endTime=DateUtil.toDate(reportBlackMap.get("black_end_time"),DateUtil.DATE_FORMAT_DATETIME);
			Date currentTime=new Date();
			//判断是否黑名单且 当前时间大于黑名单开始时间 且小于黑名单结束时间
			if(reportBlackMap.get("flag_black").equals("4497472000050001")&&
					currentTime.after(startTime) && currentTime.before(endTime)){
					result.setResultCode(2);
					result.setResultMessage("该用户处于冻结状态");
					return result;
			}
		}
		result.setResultCode(1);
		return result;
	}

}