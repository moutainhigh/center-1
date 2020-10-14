package com.cmall.productcenter.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cmall.systemcenter.systemface.IFlowFunc;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 
 * 项目名称：productcenter 类名称：TryoutService 类描述： 查询试用活动信息 创建人：李国杰 创建时间：2014-07-30
 * 
 * @version
 * 
 */
public class TryoutService extends BaseClass implements IFlowFunc {

	/**
	 *@param uid 
	 * 
	 */
	public MDataMap getTryoutActivityCodeByUid(String uid){
		MDataMap map = new MDataMap();
		map.put("uid", uid);
		List<Map<String, Object>> reList = DbUp.upTable("oc_tryout_activity").dataQuery("activity_code", "", "", map, 0, 0);
		map.clear();
		if(!reList.isEmpty()){
				map.put("activity_code", reList.get(0).get("activity_code").toString());
		}else{
				map.put("activity_code", "");
		}
		return map; 
	}

	//获取当前时间
	public String getNowDateStr(String dataFormat){
		SimpleDateFormat sd = new SimpleDateFormat(dataFormat);
		return sd.format(new Date());
	}
	public RootResult BeforeFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus, MDataMap mSubMap) {
		// TODO Auto-generated method stub
		return null;
	}

	public RootResult afterFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus, MDataMap mSubMap) {
		// TODO Auto-generated method stub
		return null;
	}
}
