package com.cmall.groupcenter.job.push;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.systemcenter.util.AESUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.webwx.WxGateSupport;

/**
 * 积分打卡签到
 * @author Angel Joy
 * @date 2020-8-5 10:25:56
 * @version 
 * @desc TODO
 */
public class JobSendSubScribeNoticeForSignIn  extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
		String sql = "SELECT DISTINCT user_code,open_id FROM newscenter.nc_push_news_subscribe_pro WHERE subscribe_type = '449748680001' AND if_post = 0";
		List<Map<String,Object>> pushNews = DbUp.upTable("nc_push_news_subscribe_pro").dataSqlList(sql, new MDataMap());
		if(pushNews == null || pushNews.size() <= 0) {
			return;
		}
		for(Map<String,Object> map : pushNews) {
			MDataMap mmap = new MDataMap(map);
			boolean flag = this.pushNews(mmap);
			if(flag) {
				mmap.put("if_post", "1");
				mmap.put("subscribe_type", "449748680001");
				mmap.put("push_time", DateUtil.getSysDateTimeString());
				DbUp.upTable("nc_push_news_subscribe_pro").dataUpdate(mmap, "if_post,push_time", "user_code,open_id,subscribe_type");
			}
		}
		
	}

	/**
	 * 消息推送逻辑处理
	 * @param mmap
	 */
	@SuppressWarnings("deprecation")
	private boolean pushNews(MDataMap mmap) {
		//需要判断下该用户是否已经签到
		String userCode = mmap.get("user_code");
		Integer count = DbUp.upTable("lc_huodong_event_dakajf_user").count("member_code",userCode,"jf_dk_time",DateUtil.getSysDateString());
		if(count > 0) {
			return false;
		}
		MDataMap userInfo =  DbUp.upTable("mc_login_info").one("member_code",userCode,"manage_code","SI2003");
		String mobile = "";
		if(userInfo != null && !userInfo.isEmpty()) {
			mobile = userInfo.get("login_name");
		}
		String token = "";
		String sqlOneToken = "SELECT * FROM zapdata.za_oauth WHERE user_code = :user_code  AND expires_time > sysdate() AND flag_enable = 1 ORDER BY create_time DESC LIMIT 1";
		Map<String,Object> tokenMap = DbUp.upTable("za_oauth").dataSqlOne(sqlOneToken, new MDataMap("user_code",userCode));
		if(tokenMap != null && !tokenMap.isEmpty()) {
			token = MapUtils.getString(tokenMap, "access_token","");
		}
		if(StringUtils.isEmpty(token) || StringUtils.isEmpty(mobile)) {//二者有一为空，则不发通知
			return false;
		}
		AESUtil aesutil = new AESUtil();
		aesutil.initialize();
		mobile = URLEncoder.encode(aesutil.encrypt(mobile));
		token = URLEncoder.encode(aesutil.encrypt(token));
		String open_id = mmap.get("open_id");
		String xcx_target_version = bConfig("groupcenter.xcx_target_version");
		String xcx_language = bConfig("groupcenter.xcx_language");
		String receivers  = open_id + "|12||/packageAct/pages/jifendaka/jifendaka?mobile="+mobile+"&token="+token + "|"+xcx_target_version+"|"+xcx_language;
		String thing1 = bConfig("groupcenter.push_jfdk_title");
		if(thing1.length()>20) {
			thing1 = thing1.substring(0,17)+"...";
		}
		String vaule = bConfig("groupcenter.push_jkdl_content");
		String defaultS = bConfig("groupcenter.push_jfdk_desc");
		String message = "{\"thing1\":{\"value\":\""+thing1+"\"},\"thing4\":{\"value\":\""+vaule+"\"},\"thing3\":{\"value\":\""+defaultS+"\"}}";
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
		return true;
	}
	
}
