package com.cmall.groupcenter.homehas;

import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.baseface.IBaseCreate;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.HttpClientSupport;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapdata.dbdo.DbUp;

/*
 * 同步网易考拉数据类
 */
public class RsyncKaoLaSupport extends BaseClass implements IBaseCreate {
	
	private static String KAOLA_URL = TopUp.upConfig("groupcenter.wangyi_kaola_url");
	private static String KAOLA_APPKEY = TopUp.upConfig("groupcenter.wangyi_kaola_appkey");
	private static String KAOLA_SECRET = TopUp.upConfig("groupcenter.wangyi_kaola_secret");
	private static String KAOLA_CHANNELID = TopUp.upConfig("groupcenter.wangyi_kaola_channelid");
	private static String KAOLA_VERSION = TopUp.upConfig("groupcenter.wangyi_kaola_version");
	private static String KAOLA_SIGNMETHOD = TopUp.upConfig("groupcenter.wangyi_kaola_signmethod");
	private static String KAOLA_SWITCH = TopUp.upConfig("groupcenter.wangyi_kaola_log_switch");
	
	private static String[] times = {"00","06","12","18","21"};
	
	public static String doPostRequest(String apiName, String supplierField, TreeMap<String, String> params) {
		String result = "";
		String requestTime = FormatHelper.upDateTime();
		MDataMap logParams = new MDataMap();
		
		try {
			params.put(supplierField, KAOLA_CHANNELID);
			params.put("timestamp", new Timestamp(System.currentTimeMillis()).toString());
			params.put("v", KAOLA_VERSION);
			params.put("sign_method", KAOLA_SIGNMETHOD);
			params.put("app_key", KAOLA_APPKEY);
			String sign = createSign(params);
			params.put("sign", sign);
			
			// 特定的接口要求请求时使用GET方法
			if("queryAccountBalance".equals(apiName)) {
				// 渠道账户余额查询  仅支持GET请求
				result = HttpClientSupport.doGet(KAOLA_URL + apiName, params);
			} else {
				result = HttpClientSupport.doPost(KAOLA_URL + apiName, params);
			}
			
			logParams.put("response_time", FormatHelper.upDateTime());
			logParams.put("response_data", result);
		}catch(Exception e) {
			logParams.put("exception_data", String.valueOf(e));
			e.printStackTrace();
		}finally {
			logParams.put("rsync_target", apiName);
			logParams.put("request_url", KAOLA_URL + apiName);
			logParams.put("request_data", params.toString());
			logParams.put("request_time", requestTime);
			logParams.put("create_time", FormatHelper.upDateTime());
			
			// 屏蔽接口queryGoodsInfoByIds的部分日志，减少数据库日志量
			if(StringUtils.isNotBlank(logParams.get("exception_data"))
					|| !"queryGoodsInfoByIds".equals(apiName)
					|| ("queryGoodsInfoByIds".equals(apiName) && ArrayUtils.contains(times, FormatHelper.upDateTime("HH")))){
				addKaolaLog(logParams);
			}
		}
		
		return result;
	}
	
	private static void addKaolaLog(MDataMap logParams) {
		if("on".equals(KAOLA_SWITCH)) {
			DbUp.upTable("lc_rsync_kaola_log").dataInsert(logParams);
		}
	}
	
	private static String createSign(TreeMap<String, String> params) {
		String sign = KAOLA_SECRET;
		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
		while(iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			if("sign".equals(entry.getKey())) {
				continue;
			}
			sign += entry.getKey() + entry.getValue();
		}
		sign += KAOLA_SECRET;
		return doKaolaMd5Encode(sign);
	}
	
	private static String doKaolaMd5Encode(String sign) {
		StringBuffer buffer = new StringBuffer();
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte byteArray[] = sign.getBytes("UTF-8");
			byte md5Byte [] = digest.digest(byteArray);
			for(int i = 0; i < md5Byte.length; i ++) {
				int val = ((int)md5Byte[i]) & 0xff;
				if(val < 16) {
					buffer.append("0");
				}
				buffer.append(Integer.toHexString(val));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return buffer.toString().toUpperCase();
	}
	
	public static String getSign(TreeMap<String, String> params) {
		return createSign(params);
	}
	
	public static String getChannelId() {
		return KAOLA_CHANNELID;
	}
	
	public static String getSwitch() {
		return KAOLA_SWITCH;
	}
}
