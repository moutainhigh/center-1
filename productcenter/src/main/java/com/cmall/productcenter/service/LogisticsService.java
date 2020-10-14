package com.cmall.productcenter.service;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * 物流管理
 * 
 * @author wangkecheng
 *
 */
public class LogisticsService {
	/**
	 *
	 * 
	 * @param mDataMap
	 *            插入数据库的名值对信息
	 * @return boolean 处理是否成功
	 */
	public boolean addCompany(MDataMap mDataMap) {
		try {
			mDataMap.put("company_code", WebHelper.upCode("LC"));// 获取以LC开头的信息编号
			DbUp.upTable("sc_logisticscompany").dataInsert(mDataMap);
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
