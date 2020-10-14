package com.cmall.membercenter.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.cmall.membercenter.hxsupport.httpclient.api.EasemobIMUsers;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.SecrurityHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 获取环信登录信息(客服)
 * @author LHY
 * 2015年10月15日 上午11:40:17
 */
public class HXUserLoginService extends BaseClass {
	
	/**
	 * 0 注册失败，1 用户已存在，200 用户注册成功,其他状态为环信返回状态
	 * @param memberCode
	 * @return
	 */
	public MDataMap loginInfo(String memberCode) {
		if(existLoginInfo(memberCode)) {//已经存在
			return getLoginInfo(memberCode);
		}//不存在
		return insertLoginInfo(memberCode, "");
	}
	
	/**
	 * 判断用户是否存在
	 * @param memberCode
	 * @return
	 */
	protected boolean existLoginInfo(String memberCode) {
		MDataMap mWhereMap = new MDataMap();
		mWhereMap.put("member_code", memberCode);
		mWhereMap.put("status", "200");
		int a = DbUp.upTable("nc_hx_login_info").dataCount("member_code=:member_code and hx_status=:status", mWhereMap);
		return a>0;
	}
	/**
	 * 获取存在用户信息
	 * @param memberCode
	 * @return
	 */
	protected MDataMap getLoginInfo(String memberCode) {
		MDataMap mDataMap = DbUp.upTable("nc_hx_login_info").one("member_code", memberCode);
		mDataMap.put("hx_status", "1");
		mDataMap.put("hx_worker_id", bConfig("membercenter.hx_worker_id"));
		return mDataMap;
	}
	/**
	 * 用户不存在则创建用户信息并返回该信息
	 * @param memberCode
	 * @param params
	 * @return
	 */
	protected MDataMap insertLoginInfo(String memberCode, String... params) {
		MDataMap mDataMap = new MDataMap();
		try {
			String username = buildRandom();
			String password = SecrurityHelper.MD5Secruity(bConfig("membercenter.hx_defualt_password"));
			EasemobIMUsers users = new EasemobIMUsers();
			String result = users.registerUser(username, password);
			JSONObject obj = JSONObject.parseObject(result);
			String status = obj.getString("statusCode");
			if("200".equals(status)) {
				mDataMap.put("hx_status", "200");
			} else {
				mDataMap.put("hx_status", status);
			}
			mDataMap.put("hx_user_name", username);
			mDataMap.put("hx_pass_word", password);
			mDataMap.put("member_code", memberCode);
			mDataMap.put("create_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			mDataMap.put("hx_worker_id", bConfig("membercenter.hx_worker_id"));
			DbUp.upTable("nc_hx_login_info").dataInsert(mDataMap);
		} catch (Exception e) {
			mDataMap.put("hx_status", "0");
			e.printStackTrace();
		}
		return mDataMap;
	}
	
	/**
	 * 随机生成8位数字id
	 * @return
	 */
	private String buildRandom() {
		int length = 8;
		int num = 1;
		double random = Math.random();
		if (random < 0.1) {
			random = random + 0.1;
		}
		for (int i = 0; i < length; i++) {
			num = num * 10;
		}
		return String.valueOf((int)((random * num)));
	}
	public static void main(String[] args) {
		JSONObject object = JSONObject.parseObject("{\"username\":\"LHY\", \"password\":\"123\"}");
		System.out.println(object.get("password"));
	}
}