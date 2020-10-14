package com.ordercenter.express.service;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webfactory.UserFactory;

public class OrderShipmentsService {
	
	/** 1 订阅 */
	public static final int CALL_TYPE_POST = 1;
	/** 2 查询 */
	public static final int CALL_TYPE_QUERY = 2;

	/**
	 * 订阅快递或实时查询100时记录日志
	 * @param callType 1 订阅 2 查询
	 * @param orderCode
	 * @param logisticseCode
	 * @param waybill
	 */
	public void onCallKuaidi100(int callType, String orderCode, String logisticseCode, String waybill) {
		String now = FormatHelper.upDateTime();
		String sqlWhere = "order_code = :order_code and logisticse_code = :logisticse_code and waybill = waybill and call_type = :call_type and LEFT(create_time,7) = :month";
		MDataMap mWhereMap = new MDataMap();
		mWhereMap.put("order_code", orderCode);
		mWhereMap.put("logisticse_code", StringUtils.trimToEmpty(logisticseCode));
		mWhereMap.put("waybill", StringUtils.trimToEmpty(waybill));
		mWhereMap.put("call_type", callType+"");
		mWhereMap.put("month", now.substring(0, 7));
		int count = DbUp.upTable("lc_order_kuaidi100_call_log").dataCount(sqlWhere, mWhereMap);
		
		// 如果不存在则插入
		if(count == 0) {
			mWhereMap.remove("month");
			mWhereMap.put("create_time", now);
			DbUp.upTable("lc_order_kuaidi100_call_log").dataInsert(mWhereMap);
		}
	}
	
	/**
	 * 变更物流信息时记录日志
	 * @param orderCode
	 * @param logisticseCodeOld
	 * @param waybillOld
	 * @param logisticseCodeNew
	 * @param waybillNew
	 */
	public void onChangeShipment(String orderCode,String logisticseCodeOld, String waybillOld,String logisticseCodeNew, String waybillNew) {
		MUserInfo userInfo = UserFactory.INSTANCE.create();
		String userCode = userInfo == null ? "" : userInfo.getUserCode();
		
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("order_code", orderCode);
		mDataMap.put("logisticse_code_old", StringUtils.trimToEmpty(logisticseCodeOld));
		mDataMap.put("waybill_old", StringUtils.trimToEmpty(waybillOld));
		mDataMap.put("logisticse_code_new", StringUtils.trimToEmpty(logisticseCodeNew));
		mDataMap.put("waybill_new", StringUtils.trimToEmpty(waybillNew));
		mDataMap.put("create_time", FormatHelper.upDateTime());
		mDataMap.put("creator", userCode);
		DbUp.upTable("lc_order_shipments_log").dataInsert(mDataMap);
	}
}
