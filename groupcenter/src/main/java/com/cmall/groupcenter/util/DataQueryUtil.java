package com.cmall.groupcenter.util;

import com.cmall.groupcenter.weixin.WebchatConstants;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;


/**
 * 
 * 处理数据查询的util,用于从数据库查询
 * 相应信息的UTIL
 * @author lipengfei
 * @date 2015-5-21
 * email:lipf@ichsy.com
 *
 */
public class DataQueryUtil {
	
	/**
	 * 通过手机号码获取会员信息
	 * @author lipengfei
	 * @date 2015-5-21
	 * @param phoneNum
	 * @return-
	 */
	public static MDataMap getLoginInfoByPhoneNum(String phoneNum){
		
		MDataMap mLoginMap = DbUp.upTable("mc_login_info").one("login_name",phoneNum,"manage_code",WebchatConstants.CGROUP_MANAGE_CODE);
		return mLoginMap;
		
	}

	/**
	 * 通过手机号码获取会员信息
	 * @author lipengfei
	 * @date 2015-5-21
	 * @param phoneNum
	 * @return-
	 */
	public static MDataMap getMemInfoByMemcode(String memCode){
		
		MDataMap mLoginMap = DbUp.upTable("mc_member_info").one("member_code",memCode,"manage_code",WebchatConstants.CGROUP_MANAGE_CODE);
		
		return mLoginMap;
		
	}

	/**
	 * 通过会员的微信的openId获取绑定信息
	 * @author lipengfei
	 * @date 2015-5-21
	 * @param phoneNum
	 * @return
	 */
	public static MDataMap getBindInfoByOpenId(String openId){
		
		MDataMap mLoginMap = DbUp.upTable("mc_weixin_binding").one("open_id",openId,"manage_code",WebchatConstants.CGROUP_MANAGE_CODE);
		
		return mLoginMap;
		
	}
	
	
}
