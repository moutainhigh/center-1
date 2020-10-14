package com.cmall.groupcenter.service;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 添加商户信息的service
 *
 * @author lipengfei
 * @date 2015-6-23
 * email:lipf@ichsy.com
 *
 */
public class TraderInfoService extends BaseClass {
		
	          
	/**
	 * 
	 * @author lipengfei
	 * @date 2015-6-23
	 * @return
	 */
		public void addNewTrader(MDataMap tradeInfo){
			
			DbUp.upTable("gc_trader_info").dataInsert(tradeInfo);
			
		} 
		  
		/**
		 * 
		 * 更新商户信息
		 * @author lipengfei
		 * @date 2015-6-29
		 * @param tradeInfo
		 * @param sUpdateFields
		 * @param sWhereFields
		 * 
		 */
		public void updateTraderInfo(MDataMap tradeInfo,String sUpdateFields,String sWhereFields){
			
			DbUp.upTable("gc_trader_info").dataUpdate(tradeInfo, sUpdateFields, sWhereFields);
			
		}
		
		/**
		 * 
		 * 获取商户信息
		 * @author huangs
		 * @date 2015-7-20
		 * @param sPrams
		 */
		
		
        public MDataMap queryTraderInfo(String... sPrams){
			
			return DbUp.upTable("gc_trader_info").oneWhere("", "", "", sPrams);
			
		}  
        /**
		 * 获取商户账户信息
		 * @author huangs
		 * @date 2015-7-20
		 * @param sPrams
		 */
       public MDataMap queryTraderAccountInfo(String... sPrams){
			
			return DbUp.upTable("gc_group_account").oneWhere("", "", "", sPrams);
			     
		}
			
}
