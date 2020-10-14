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
public class TraderFoundsChangeLogService extends BaseClass {
		
	
	/**
	 * 
	 * @author lipengfei
	 * @date 2015-6-23
	 * @return
	 */
		public void addFoundsChangeLog(MDataMap changeLog){
			DbUp.upTable("gc_trader_founds_change_log").dataInsert(changeLog);
		} 
}
