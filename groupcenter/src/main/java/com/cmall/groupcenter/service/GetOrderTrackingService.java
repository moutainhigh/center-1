package com.cmall.groupcenter.service;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.homehas.RsyncGetOrderTracking;
import com.srnpr.zapcom.baseclass.BaseClass;
/**
 * 订单配送轨迹查询接口
 * @author wz
 *
 */
public class GetOrderTrackingService extends BaseClass{
	/**
	 * 
	 * @param orderCode  订单号
	 * @param account  调用用户
	 * @param password  调用密码
	 */
	public boolean synchronizationGetOrderTracking(String orderCode){
		boolean bol = false;
		if(!StringUtils.isEmpty(orderCode)){
			RsyncGetOrderTracking  rsyncGetOrderTracking = new RsyncGetOrderTracking();
			rsyncGetOrderTracking.upRsyncRequest().setOrd_id(orderCode);
			bol = rsyncGetOrderTracking.doRsync();
		}
		return bol;
	}
	
	public static void main(String[] args) {
		GetOrderTrackingService GetOrderTrackingService = new GetOrderTrackingService();
		GetOrderTrackingService.synchronizationGetOrderTracking("20109130");
	}
}
