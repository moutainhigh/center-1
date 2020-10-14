package com.cmall.productcenter.service;

import java.util.List;
import java.util.Map;

import com.cmall.systemcenter.systemface.IFlowFunc;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 
 * 项目名称：productcenter 类名称：StockChangeService 类描述： 查询库存变更记录总表信息 创建人：李国杰 创建时间：2014-08-12
 * 
 * @version
 * 
 */
public class StockChangeService extends BaseClass implements IFlowFunc {


	/**
	 * 根据uid获取库存变更记录信息
	 *@param uid
	 *@author 李国杰 
	 *@return  sku_code,change_time 
	 * 
	 */
	public MDataMap getStockChangeByUid(String uid){
		MDataMap map = new MDataMap();
		map.put("uid", uid);
		List<Map<String, Object>> reList = DbUp.upTable("lc_stockchange_info").dataQuery("sku_code,change_time", "", "", map, 0, 0);
		map.clear();
		if(!reList.isEmpty()){
			map.put("sku_code", reList.get(0).get("sku_code").toString());
			map.put("change_time", reList.get(0).get("change_time").toString());
		}else{
			map.put("sku_code", "");
			map.put("change_time", "");
		}
		return map; 
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
