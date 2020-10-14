package com.cmall.ordercenter.service;

import com.cmall.ordercenter.model.ExchangegoodsStatusLogModel;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 
 * 类名称：     PlatformgoodsStatusLogService 
 * 类描述：     记录换货状态日志
 * 创建人：     shiyz
 * 创建时间：2014-07-03
 *
 */
public class PlatformgoodsStatusLogService extends BaseClass{

	/**
	 * 更新换货日志信息
	 * @param statusLog 换货日志信息
	 * @return 否成功标志：true:成功 false:失败
	 */
	public boolean addExchangegoodsStatusLogService(ExchangegoodsStatusLogModel statusLog) throws Exception{
		
		//更新状态日志是否成功标志：true:成功 false:失败
		boolean insFlag = true;
		try{
			
			MDataMap insMap = new MDataMap();
			//换货日志信息设定
			insMap.put("exchange_no", statusLog.getExchangeNo());
			insMap.put("info", statusLog.getInfo());
			insMap.put("create_time", statusLog.getCreateTime());
			insMap.put("create_user", statusLog.getCreateUser());
			insMap.put("old_status", statusLog.getOldStatus());
			insMap.put("now_status", statusLog.getNowStatus());
			
			//插入新的换货状态日志信息
			DbUp.upTable("lc_exchangegoods").dataInsert(insMap);
			
		} catch (Exception ex){
			//异常处理
			return false;
		}
		
		return insFlag;
	}
}
