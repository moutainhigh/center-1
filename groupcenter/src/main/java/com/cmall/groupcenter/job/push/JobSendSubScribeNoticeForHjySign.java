package com.cmall.groupcenter.job.push;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.systemcenter.util.AESUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.webwx.WxGateSupport;

/**
 * 惠家有首页签到提醒
 * @author lgx
 */
public class JobSendSubScribeNoticeForHjySign  extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
		String sql = "SELECT DISTINCT user_code,open_id FROM newscenter.nc_push_news_subscribe_pro WHERE subscribe_type = '449748680005' AND if_post = 0";
		List<Map<String,Object>> pushNews = DbUp.upTable("nc_push_news_subscribe_pro").dataSqlList(sql, new MDataMap());
		if(pushNews == null || pushNews.size() <= 0) {
			return;
		}
		for(Map<String,Object> map : pushNews) {
			MDataMap mmap = new MDataMap(map);
			try {
				boolean flag = this.pushNews(mmap);
				if(flag) {
					mmap.put("if_post", "1");
					mmap.put("subscribe_type", "449748680005");
					mmap.put("push_time", DateUtil.getSysDateTimeString());
					DbUp.upTable("nc_push_news_subscribe_pro").dataUpdate(mmap, "if_post,push_time", "user_code,open_id,subscribe_type");
				}
			} catch (Exception e) {
				e.printStackTrace();
				WebHelper.errorMessage(mmap.get("user_code"), "jobSendHjySignNotice", 9,"jobSendHjySignNotice on JobSendSubScribeNoticeForHjySign", "", e);
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
		// 查询用户今天是否签到
		Integer count = DbUp.upTable("mc_sign").count("user_code",userCode,"flag_sign_today","1");
		if(count > 0) {
			// 已签到则不发送提醒
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
		// 取随机数防止缓存
		Random random = new Random();
		int nextInt = random.nextInt(9999);
		String src = URLEncoder.encode(bConfig("groupcenter.push_hjy_sign_src")+nextInt);
		String xcx_target_version = bConfig("groupcenter.xcx_target_version");
		String xcx_language = bConfig("groupcenter.xcx_language");
		String receivers  = open_id + "|12||/pages/openweb/openweb?autoLogin=2&src="+src+"&mobile="+mobile+"&token="+token + "|"+xcx_target_version+"|"+xcx_language;
		String thing1 = bConfig("groupcenter.push_hjy_sign_title");
		String thing4 = bConfig("groupcenter.push_hjy_sign_content");
		String thing3 = bConfig("groupcenter.push_hjy_sign_desc");
		if(thing1.length()>20) {
			thing1 = thing1.substring(0,17)+"...";
		}
		if(thing4.length()>20) {
			thing4 = thing4.substring(0,17)+"...";
		}
		if(thing3.length()>20) {
			thing3 = thing3.substring(0,17)+"...";
		}
		String message = "{\"thing1\":{\"value\":\""+thing1+"\"},\"thing4\":{\"value\":\""+thing4+"\"},\"thing3\":{\"value\":\""+thing3+"\"}}";
		WxGateSupport wxGateSupport = new WxGateSupport();
		String result = wxGateSupport.sendMsgForNotice(receivers, message);
		
		MDataMap logMap = new MDataMap();
		logMap.put("create_time", DateUtil.getNowTime());
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
