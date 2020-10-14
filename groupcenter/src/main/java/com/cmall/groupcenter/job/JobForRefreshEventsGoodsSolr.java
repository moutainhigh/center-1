package com.cmall.groupcenter.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.srnpr.xmassystem.enumer.EPlusScheduler;
import com.srnpr.xmassystem.helper.PlusHelperScheduler;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.helper.KvHelper;
import com.srnpr.zapweb.rootweb.RootJob;

public class JobForRefreshEventsGoodsSolr extends RootJob {

	public void doExecute(JobExecutionContext context) { 
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String startTime=sdf.format(date);                      //系统当前时间
		String endTime=sdf.format(date.getTime()-1000*60*5);    //当前时间前五分钟
		String sql = " SELECT event_code FROM sc_event_info WHERE  (event_status = '4497472700020002' or event_status = '449747270002004') AND end_time > '"+endTime+"' AND end_time < '"+startTime+"'";
		List<Map<String, Object>> dataSqlList = DbUp.upTable("sc_event_info").dataSqlList(sql, null);
		if(null!= dataSqlList&&dataSqlList.size()>0) {
			for(Map<String, Object> map : dataSqlList) {
				String eventCode = map.get("event_code").toString();
				
				//活动商品刷新solr索引库
				PlusHelperScheduler
				.sendSchedler(
						EPlusScheduler.UpdateEventsGoods,
						KvHelper.upCode(EPlusScheduler.UpdateEventsGoods.toString()),
						eventCode);
			}
		}
	}

}
