package com.cmall.productcenter.service;

import java.util.HashMap;
import java.util.Map;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
/**
 * 返回erp系统首页中需要显示的公告内容
 * @author liqt
 *
 */
public class ReturnProclamationForCmanage extends BaseClass{
	public Map<String, String> getProclamation(){
		MDataMap mDataMap = DbUp.upTable("fh_proclamation_manage").oneWhere("proclamation_text,proclamation_title", "release_time desc", "release_time<=Now() and proclamation_status=4497477000010001 and possess_project=4497467900050004");
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
}
