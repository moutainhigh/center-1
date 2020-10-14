package com.cmall.groupcenter.jd.job;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;

import com.alibaba.fastjson.JSONObject;
import com.cmall.groupcenter.jd.model.JDTokenBean;
import com.srnpr.xmassystem.load.LoadJDToken;
import com.srnpr.xmassystem.plusquery.PlusModelQuery;
import com.srnpr.xmassystem.util.DateUtil;
import com.srnpr.xmassystem.util.MD5Code;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.HttpClientSupport;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapcom.topdo.TopConfig;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;
/**
 * 定时刷新调用京东接口api的token接口
 * @author 张圣瑞
 *
 */
public class JdRefreshToken extends RootJob{
	private static String JINGDONG_TOKEN_URL = TopUp.upConfig("xmassystem.jingdong_token_url");
	private static String JINGDONG_APPKEY = TopUp.upConfig("xmassystem.jingdong_app_key");
	private static String JINGDONG_APPSECRET = TopUp.upConfig("xmassystem.jingdong_app_secret");
	private static String JINGDONG_USERNAME = TopUp.upConfig("xmassystem.jingdong_username");
	private static String JINGDONG_PASSWORD = TopUp.upConfig("xmassystem.jingdong_password");
	@Override
	public void doExecute(JobExecutionContext context) {
		MDataMap mDataMap = new MDataMap();
		MDataMap logParams = new MDataMap();
		String requestTime = FormatHelper.upDateTime();
		String gettoken = "";
		String doGetDg = null;
		try {
			gettoken = String.format(
					JINGDONG_TOKEN_URL
					.concat("?grant_type=refresh_token&app_key=%s&app_secret=%s&state=jiayou&username=%s&password=%s"),
				JINGDONG_APPKEY,
				JINGDONG_APPSECRET,
				JINGDONG_USERNAME,
				MD5Code.encode(JINGDONG_PASSWORD));
			doGetDg = HttpClientSupport.doGetDg(gettoken);
			logParams.put("response_time", FormatHelper.upDateTime());
			logParams.put("response_data", doGetDg);
			JDTokenBean jdTokenBean = JSONObject.parseObject(doGetDg,JDTokenBean.class);
			
			mDataMap.put("access_token", jdTokenBean.getAccess_token());
			mDataMap.put("code", StringUtils.trimToEmpty(jdTokenBean.getCode()));
			mDataMap.put("expires_in", StringUtils.trimToEmpty(jdTokenBean.getExpires_in()));
			mDataMap.put("refresh_token", StringUtils.trimToEmpty(jdTokenBean.getRefresh_token()));
			mDataMap.put("time", StringUtils.trimToEmpty(jdTokenBean.getTime()));
			mDataMap.put("token_type", StringUtils.trimToEmpty(jdTokenBean.getToken_type()));
			mDataMap.put("jd_uid", StringUtils.trimToEmpty(jdTokenBean.getUid()));
			mDataMap.put("user_nick", StringUtils.trimToEmpty(jdTokenBean.getUser_nick()));
			mDataMap.put("open_id", StringUtils.trimToEmpty(jdTokenBean.getOpen_id()));
			
			Long expires_in = Long.parseLong(jdTokenBean.getExpires_in());
			String expires_time = DateUtil.toString(Long.parseLong(jdTokenBean.getTime())+(expires_in*1000), DateUtil.DATE_FORMAT_DATETIME);
			mDataMap.put("expires_time", expires_time);
			mDataMap.put("create_time", DateUtil.toString(new Date(), DateUtil.DATE_FORMAT_DATETIME));
			DbUp.upTable("sc_jingdong_token").dataInsert(mDataMap);
			LoadJDToken loadJDToken = new LoadJDToken();
			loadJDToken.refresh(new PlusModelQuery("jd"));
		} catch (Exception e) {
			logParams.put("exception_data", String.valueOf(e));
			e.printStackTrace();
			String noticeMail = TopConfig.Instance.bConfig("oneall.count_notice");
			// 京东token获取失败
			if(StringUtils.isNotBlank(noticeMail)){
				MailSupport.INSTANCE.sendMail(noticeMail, "获取京东token失败", "京东返回值: "+doGetDg+ "\r\n失败消息："+String.valueOf(e));
			}
		}finally {
			logParams.put("rsync_target", "京东token");
			logParams.put("request_url", gettoken);
			logParams.put("request_data", "");
			logParams.put("request_time", requestTime);
			logParams.put("create_time", FormatHelper.upDateTime());
			addJingDongLog(logParams);
		}
	}
	private static void addJingDongLog(MDataMap logParams) {
		DbUp.upTable("lc_rsync_jingdong_log").dataInsert(logParams);
	}

}
