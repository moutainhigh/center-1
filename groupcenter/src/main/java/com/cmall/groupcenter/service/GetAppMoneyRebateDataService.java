package com.cmall.groupcenter.service;

import java.util.List;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 微公社后台-应用设置
 * @author fengl
 *
 */
public class GetAppMoneyRebateDataService  extends BaseClass{

	@SuppressWarnings("unchecked")
	public MDataMap getAppMoneyRebate(String appCode){
		
//		List<MDataMap> mDataMapList = new ArrayList<MDataMap>();
		MDataMap mDataMap = new MDataMap();
		MDataMap mWhereMap=new MDataMap();
		List<MDataMap> mpalist=DbUp.upTable("gc_app_rebate_set").query("uid,trader_code,money_rebate_grade,money_rebate_scale,money_rebate_range", "apply_time desc", "app_code='"+appCode+"'", mWhereMap, 0, 1);
		if(mpalist!=null&&mpalist.size()>0){
			MDataMap maplistTemp=mpalist.get(0);
			mDataMap.put("money_rebate_grade", maplistTemp.get("money_rebate_grade"));
			mDataMap.put("money_rebate_scale", maplistTemp.get("money_rebate_scale"));
			mDataMap.put("money_rebate_range", maplistTemp.get("money_rebate_range"));
			mDataMap.put("uid", maplistTemp.get("uid"));
			mDataMap.put("trader_code", maplistTemp.get("trader_code"));
//			mDataMapList.add(mDataMap);
		}else{
			MDataMap traderMap=DbUp.upTable("gc_wopen_appmanage").oneWhere("trade_code","","", "app_code",appCode);
			if(traderMap!=null){
				String traderCode=traderMap.get("trade_code");
				MDataMap defaultMap=DbUp.upTable("gc_trader_rebate").oneWhere("money_rebate_grade,money_rebate_scale,money_rebate_range", "", "", "trader_code",traderCode);
				mDataMap.put("money_rebate_grade", defaultMap.get("money_rebate_grade"));
				mDataMap.put("money_rebate_scale", defaultMap.get("money_rebate_scale"));
				mDataMap.put("money_rebate_range", defaultMap.get("money_rebate_range"));
				mDataMap.put("trader_code", traderCode);
				mDataMap.put("uid", "");
//				mDataMapList.add(mDataMap);
			}
		}
		
		return mDataMap;
	}

}
