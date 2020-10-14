package com.cmall.membercenter.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class NickNameHelper {
	/**
	 * @author LHY
	 * 查询昵称,步骤如下:<br>
	 * 1,根据account_code_wo和account_code_ta查询昵称表(gc_alter_nickname),昵称存在,直接返回,昵称不存在跳入步骤2;<br>
	 * 2,根据好友的member_code查询用户信息扩展表(mc_extend_info_groupcenter),若昵称存在,直接返回,若昵称不存在跳入步骤3;<br>
	 * 3,根据好友的member_code查询用户登录信息表(mc_login_info),若手机号存在,直接返回,若手机号不存在,返回空字符串;<br>
	 * @param map 该参数中请传递三个参数:本人的account_code_wo,好友的account_code_ta,好友的member_code;
	 */
	public static String getNickName(Map<String, String> map) {
		if(map==null || map.size()==0) {
			return "";
		}
		String nickName = "";
		MDataMap mDataMap = new MDataMap();
		if(map.containsKey("account_code_wo") && map.containsKey("account_code_ta")) {
			mDataMap.put("account_code_wo", map.get("account_code_wo"));
			mDataMap.put("account_code_ta", map.get("account_code_ta"));
			Object object = DbUp.upTable("gc_alter_nickname").dataGet("nick_name", "account_code_wo=:account_code_wo and account_code_ta=:account_code_ta", mDataMap);
			if(object!=null && StringUtils.isNotEmpty(String.valueOf(object))) {
				nickName = String.valueOf(object);
			} else if(map.containsKey("member_code")) {
				nickName = checkTo(mDataMap, map);
			}
		} else if(map.containsKey("member_code")) {
			nickName = checkTo(mDataMap, map);
		}
		return nickName;
	}
	private static String checkTo(MDataMap mDataMap, Map<String, String> map) {
		mDataMap = new MDataMap();
		mDataMap.put("member_code", map.get("member_code"));
		return getName(mDataMap);
	}
	private static String getName(MDataMap mDataMap) {
		String nickName = "";
		Object object = DbUp.upTable("mc_extend_info_groupcenter").dataGet("nickname", "member_code=:member_code", mDataMap);
		if(object!=null && StringUtils.isNotEmpty(String.valueOf(object))) {
			nickName = String.valueOf(object);
		} else {
			object = DbUp.upTable("mc_login_info").dataGet("login_name", "member_code=:member_code", mDataMap);
			if(object!=null && StringUtils.isNotEmpty(String.valueOf(object))) {
				nickName = String.valueOf(object);
				nickName = nickName.substring(0, nickName.length()-(nickName.substring(3)).length())+"****"+nickName.substring(7);
			}
		}
		return nickName;
	}
	/**
	 * 获取我自己昵称
	 * @param usercode
	 * @param managecode
	 * @return
	 */
	public static String getMyNickName(String usercode ,String managecode){
		String nickName=null;
		// 查出用户 信息
		MDataMap mUserMap = DbUp.upTable("mc_extend_info_groupcenter").one("member_code",usercode, "app_code", managecode);
		if(mUserMap!=null) {
			nickName=mUserMap.get("nickname");
		}
		if(StringUtils.isBlank(nickName)){
			//判断扩展信息表是否有昵称，没有则取手机号
			List<Map<String, Object>> manageCodeList = DbUp.upTable("mc_login_info").dataSqlList("select login_name from mc_login_info where  member_code=:member_code" , new MDataMap("member_code",usercode));
	        if(manageCodeList!=null && manageCodeList.size()>0){
	        	nickName=String.valueOf(manageCodeList.get(0).get("login_name"));
	        }
		}
		return nickName;
	}
	/**
	 * 
	 * @param woAccountCode 
	 * @param taAccountCode 二度好友 查找一度好友的昵称
	 * @return
	 */
	public static String  getFirstNickName(String woAccountCode,String taAccountCode){
		String nickName="";
		String relationCode="";
		MDataMap oneMap=DbUp.upTable("gc_member_relation").one("account_code",taAccountCode,"flag_enable","1");
		
		//一度好友
		if(oneMap!=null){
			String firstAccountCode=oneMap.get("parent_code");
			if(DbUp.upTable("gc_member_relation").count("account_code",firstAccountCode,"parent_code",woAccountCode,"flag_enable","1")>0){
				MDataMap accountMap=DbUp.upTable("mc_member_info").one("account_code",firstAccountCode,"manage_code","SI2011");//微公社app管理编号
		    	if(accountMap!=null){
		    		relationCode=accountMap.get("member_code");
		    	}
		    	else{
		    		MDataMap otherMap=DbUp.upTable("mc_member_info").one("account_code",firstAccountCode);
		    		if(otherMap!=null){
		    			relationCode=otherMap.get("member_code");
		    		}
		    	}
				Map<String, String> map=new HashMap<String, String>();
				map.put("member_code",relationCode );
				map.put("account_code_wo", woAccountCode);
				map.put("account_code_ta", firstAccountCode);
				/**
				 * 系统消息推送好友昵称 ：有昵称显示昵称，没有昵称显示手机前三后四中间用*
				 */
				MDataMap mDataMap = new MDataMap();
				nickName=checkTo(mDataMap, map);
			}
		}
			
			
		return nickName;
	}
	public static String checkToNickName(Map<String, String> map) {
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("member_code", map.get("member_code"));
		return getName(mDataMap);
	}


}