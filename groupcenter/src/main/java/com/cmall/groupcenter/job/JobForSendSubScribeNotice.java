package com.cmall.groupcenter.job;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.quartz.JobExecutionContext;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.webwx.WxGateSupport;

public class JobForSendSubScribeNotice  extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
		String sql = "SELECT * FROM newscenter.nc_push_news_subscribe WHERE push_time <= now() AND if_push = 0";
		List<Map<String,Object>> pushNews = DbUp.upTable("nc_push_news_subscribe").dataSqlList(sql, null);
		if(pushNews == null || pushNews.size() <= 0) {
			return;
		}
		for(Map<String,Object> map : pushNews) {
			MDataMap mmap = new MDataMap(map);
			mmap.put("if_push", "1");
			DbUp.upTable("nc_push_news_subscribe").dataUpdate(mmap, "if_push", "zid");
			this.pushNews(mmap);
		}
		
	}

	/**
	 * 消息推送逻辑处理
	 * @param mmap
	 */
	private void pushNews(MDataMap mmap) {
		String open_id = mmap.get("open_id");
//		String tradekeyid = mmap.get("form_id");
		String receivers  = open_id+ "|1||/pages/product_detail/product_detail?pid="+mmap.get("product_code");
//		String message = "{\"keyword1\":{\"color\":\"#336699\",\"value\":\""+mmap.get("product_name")+"\"},\"keyword2\":{\"color\":\"#336699\",\"value\":\""+mmap.get("event_start_time")+"\"}}";
		String event_start_time = mmap.get("event_start_time").substring(0,4)+"年"+mmap.get("event_start_time").substring(5,7)+"月"+mmap.get("event_start_time").substring(8,10)+"日 "+mmap.get("event_start_time").substring(11,16);
		String thing1 = mmap.get("product_name");
		if(thing1.length()>20) {
			thing1 = thing1.substring(0,17)+"...";
		}
		String message = "{\"thing1\":{\"value\":\""+thing1+"\"},\"date2\":{\"value\":\""+event_start_time+"\"}}";
		MDataMap logMap = new MDataMap();
		WxGateSupport wxGateSupport = new WxGateSupport();
		logMap.put("create_time", DateUtil.getNowTime());
		String result = wxGateSupport.sendMsgForNotice(receivers, message);
		logMap.put("uid", UUID.randomUUID().toString().replace("-", "").trim());
		logMap.put("request_date", "{\"open_id\":"+open_id+",\"message\":"+message+"}");
		logMap.put("url", "Subscribe");
		logMap.put("response_data", result);
		logMap.put("push_target", "Subscribe");
		logMap.put("api_input", "{\"open_id\":"+open_id+",\"message\":"+message+"}");
		logMap.put("response_time", DateUtil.getNowTime());
		DbUp.upTable("lc_push_news_log").dataInsert(logMap);
	}
	
}
