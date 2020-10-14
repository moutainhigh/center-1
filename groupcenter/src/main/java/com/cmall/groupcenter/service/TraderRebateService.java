package com.cmall.groupcenter.service;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 添加默认返现的情况的表
 *
 * @author lipengfei
 * @date 2015-6-23
 * email:lipf@ichsy.com
 *
 */
public class TraderRebateService extends BaseClass {
		
	
	/**
	 * 
	 * @author lipengfei
	 * @date 2015-6-23
	 * @return
	 */
		public void addNewTraderRebate(MDataMap traderRebate){
			
			DbUp.upTable("gc_trader_rebate").dataInsert(traderRebate);
			
		} 
}
