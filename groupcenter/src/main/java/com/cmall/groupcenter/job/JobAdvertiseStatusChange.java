package com.cmall.groupcenter.job;

import java.util.List;

import org.quartz.JobExecutionContext;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 微公社广告管理状态变更（已结束）
 * @author Administrator
 *
 */
public class JobAdvertiseStatusChange extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
		MDataMap mWhereMap=new MDataMap();
		mWhereMap.put("now_time", FormatHelper.upDateTime());
		List<MDataMap> advertList=DbUp.upTable("nc_advertise").queryAll("", "", "app_code='SI2011' and status!='4497472000110003'"
				+ " and end_time <:now_time", mWhereMap);
		if(advertList!=null){
			for(MDataMap advert:advertList){
				advert.put("status", "4497472000110003");
				advert.put("update_time", FormatHelper.upDateTime());
				String adWhere = " place_code = '"+advert.get("place_code")+"' ";
				List<MDataMap> adResultMap = DbUp.upTable("nc_advertise").queryAll("(MAX(sort_num)+1) as sort_num","",adWhere,null);
				advert.put("sort_num", adResultMap.get(0).get("sort_num"));
				DbUp.upTable("nc_advertise").update(advert);
			}
		}
	}

}
