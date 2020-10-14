package com.cmall.groupcenter.job;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.quartz.JobExecutionContext;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.systemcenter.util.Http_Request_Post;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srnpr.zapcom.basehelper.SecrurityHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;
/**
 * 
 *每天上午10：00推送一次
 *
 */
public class JobPushPrice extends RootJob {
	
	private final String api_key = "appfamilyhas";
	private final String MD5key = "amiauhsnehnujiauhz";

	public void doExecute(JobExecutionContext context) {
		String  baiduPushUrl = bConfig("familyhas.baidu_push_url");
		List<Map<String,Object>> dataSqlList = DbUp.upTable("fh_collection_push").dataSqlList("select * from fh_collection_push where push_status = 0",new MDataMap());
		for(Map<String,Object> map : dataSqlList) {
			String member_code = MapUtils.getString(map,"member_code");
			MDataMap mWhereMap = new MDataMap();
			mWhereMap.put("member_code", member_code);
			String sSql = "select * from mc_login_info where member_code =:member_code";
			List<Map<String,Object>> dataSqlList2 = DbUp.upTable("mc_login_info").dataSqlList(sSql, mWhereMap);
			if(dataSqlList2.size() > 0) {
				map.put("phone", MapUtils.getString(dataSqlList2.get(0),"login_name"));
			}
			
			// 手机号不存在的情况不发送
			if(StringUtils.isBlank((String)map.get("phone"))) {
				map.put("push_status", "1");
				DbUp.upTable("fh_collection_push").dataUpdate(new MDataMap(map), "push_status", "zid");
				continue;
			}
			
			String result = this.push(map,baiduPushUrl);
			String api_timespan = DateUtil.getNowTime();
			JSONObject jo = new JSONObject(result);
			Integer status = 0;
			if(jo!=null) {
				status = jo.getInt("resultCode");
			}
			if(status == 1) {
				MDataMap mDataMap = new MDataMap();
				mDataMap.put("push_time", api_timespan);
				mDataMap.put("push_status", "1");
				mDataMap.put("uid", map.get("uid").toString());
				DbUp.upTable("fh_collection_push").dataUpdate(mDataMap, "push_time,push_status", "uid");
			}
		}
	}
	/**
	 * 推送方法
	 * @param map
	 */
	private String push(Map<String, Object> data,String url) {
		MDataMap map = new MDataMap();
		map.put("api_project", "jyhapi");
		String api_target =  "com_cmall_familyhas_api_APIBaiDuPush";
		map.put("api_target", api_target);
		String api_timespan = DateUtil.getNowTime();
		map.put("api_timespan",api_timespan );
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("toPage", "17");
		mDataMap.put("toUrl", "");
		String str = data.get("push_content").toString();
		try {
			mDataMap.put("msgContent", URLEncoder.encode(str, "utf-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		mDataMap.put("phone",MapUtils.getString(data, "phone"));//为空时全局推送
		ObjectMapper om = new ObjectMapper();
		String api_input = "";
		try {
			api_input = om.writeValueAsString(mDataMap);
			map.put("api_input", api_input);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		String apiSecret =  api_target +api_key+ api_input + api_timespan +  MD5key;
		apiSecret = SecrurityHelper.MD5(apiSecret);
		map.put("api_secret", apiSecret.toLowerCase());
		String result = Http_Request_Post.doPost(url, map, "utf-8");
		MDataMap pushLog = new  MDataMap();
		pushLog.put("uid", UUID.randomUUID().toString().replaceAll("-", ""));
		pushLog.put("request_date", map.toString());
		pushLog.put("response_data", result);
		pushLog.put("url", url);
		pushLog.put("push_target", api_target);
		pushLog.put("api_input", api_input);
		pushLog.put("create_time", api_timespan);
		pushLog.put("response_time", DateUtil.getNowTime());
		DbUp.upTable("lc_push_news_log").dataInsert(pushLog);
		return result;
	}
}
