package com.cmall.groupcenter.tongji.job;

import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.tongji.baidu.BaiduTongjiApi;
import com.cmall.groupcenter.tongji.umeng.ResultGetAllAppData;
import com.cmall.groupcenter.tongji.umeng.UMengTongjiApi;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

public class JobForUMengTongji extends RootJob {

	static Log LOG = LogFactory.getLog(BaiduTongjiApi.class);
	
	@Override
	public void doExecute(JobExecutionContext context) {
		String apikey = bConfig("groupcenter.tongji_umeng_apikey");
		String apiSecurity = bConfig("groupcenter.tongji_umeng_apiscurity");
		
		if(StringUtils.isBlank(apikey) || StringUtils.isBlank(apiSecurity)){
			return;
		}
		
		UMengTongjiApi api = new UMengTongjiApi(apikey, apiSecurity);
		refreshGetAllAppData(api);
	}
	
	private void refreshGetAllAppData(UMengTongjiApi api){
		String yesterday = FormatHelper.upDateTime(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd");
		ResultGetAllAppData appData = api.getReportData("com.umeng.uapp:umeng.uapp.getAllAppData", new HashMap<String, String>(), new ResultGetAllAppData.ParseImpl());
		
		if(appData.getResultCode() != 1){
			LOG.warn("JobForUMengTongji -> 接口调用失败: "+appData.getResultMessage());
			return;
		}
		
		MDataMap mDataMap = DbUp.upTable("fh_tongji_umeng_appdata").one("the_date", yesterday);
		if(mDataMap == null){
			mDataMap = new MDataMap();
		}
		
		mDataMap.put("the_date", yesterday);
		mDataMap.put("new_users", appData.getYesterdayNewUsers());
		mDataMap.put("uniq_new_users", appData.getYesterdayUniqNewUsers());
		mDataMap.put("activity_users", appData.getYesterdayActivityUsers());
		mDataMap.put("uniq_active_users", appData.getYesterdayUniqActiveUsers());
		mDataMap.put("launches", appData.getYesterdayLaunches());
		mDataMap.put("total_users", appData.getTotalUsers());
		mDataMap.put("create_time", FormatHelper.upDateTime());
		mDataMap.put("update_time", mDataMap.get("create_time"));
		
		if(StringUtils.isBlank(mDataMap.get("uid"))){
			DbUp.upTable("fh_tongji_umeng_appdata").dataInsert(mDataMap);
		} else {
			mDataMap.remove("create_time");
			DbUp.upTable("fh_tongji_umeng_appdata").update(mDataMap);
		}
	}

}
