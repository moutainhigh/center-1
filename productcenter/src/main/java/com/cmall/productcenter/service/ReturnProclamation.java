package com.cmall.productcenter.service;

import java.util.HashMap;
import java.util.Map;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
/**
 * 返回商户后台首页中需要显示的公告内容
 * @author liqt
 *
 */
public class ReturnProclamation extends BaseClass{
	public Map<String, String> getProclamationShow(){
		MDataMap mDataMap = DbUp.upTable("fh_proclamation_manage").oneWhere("proclamation_text,proclamation_title", "release_time desc", "release_time<=Now() and proclamation_status=4497477000010001 and possess_project=4497467900050002 and proclamation_or_news=1");
		Map<String,String> map = new HashMap<String, String>();
		if(null!=mDataMap){
			map.put("proclamation_text", mDataMap.get("proclamation_text"));
			map.put("proclamation_title",  mDataMap.get("proclamation_title"));
		}else {
			map.put("proclamation_text", "");
			map.put("proclamation_title",  "");
		}
		return map;
	}
	public Map<String, String> getProclamation(){
		MDataMap mDataMap = DbUp.upTable("fh_proclamation_manage").oneWhere("proclamation_text,proclamation_title", "release_time desc", "release_time<=Now() and proclamation_status=4497477000010001 and possess_project=4497467900050002 and proclamation_or_news=2");
		Map<String,String> map = new HashMap<String, String>();
		if(null!=mDataMap){
			map.put("proclamation_text", mDataMap.get("proclamation_text"));
			map.put("proclamation_title",  mDataMap.get("proclamation_title"));
		}else {
			map.put("proclamation_text", "");
			map.put("proclamation_title",  "");
		}
		return map;
	}
	
	public Map<String, String> getIsConfirmLatestProclamation(String userCode){
		MDataMap mDataMap = DbUp.upTable("fh_proclamation_manage").oneWhere("proclamation_code,proclamation_text,proclamation_title_confirmation", "release_time desc", "release_time<=Now() and proclamation_status=4497477000010001 and possess_project=4497467900050002 and opening_merchant_confirmation = 449746250001 and proclamation_or_news=2");
		Map<String,String> map = new HashMap<String, String>();
		map.put("needAlert", "0");
		map.put("proclamation_text", "");
		map.put("proclamation_code", "");
		map.put("proclamation_title_confirmation", "");
		if(null!=mDataMap){
			MDataMap mData = DbUp.upTable("fh_proclamation_confirmation").oneWhere("proclamation_code,user_code", "", "user_code = '"+userCode +"' and proclamation_code = '" + mDataMap.get("proclamation_code")+"'");
			if(null==mData) {
				map.put("needAlert", "1");//未确认，需确认
				map.put("proclamation_text", mDataMap.get("proclamation_text"));
				map.put("proclamation_code", mDataMap.get("proclamation_code"));
				map.put("proclamation_title_confirmation",  mDataMap.get("proclamation_title_confirmation"));
			}
		}
		return map;
	}
	
}
