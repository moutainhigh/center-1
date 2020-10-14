package com.cmall.ordercenter.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.model.AccountDetail;
import com.cmall.ordercenter.model.AccountInfo;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;

/**
 * 结算信息处理service
 * 
 * @author jack
 * @version 1.0
 * */
public class AccountInfoService extends BaseClass {
	private static final String ACCOUNT_NAME = "oc_accountinfo";// 结算信息主表
	private static final String ACCOUNT_LOGNAME = "lc_account_status";// 结算信息状态表
	private static final String ACCOUNT_RELATIONNAME = "oc_accountinfo_relation";// 结算信息与付款退款等关系表

	/**
	 * 插入结算主信息 及状态记录信息(跑任务时调用此方法)
	 * 
	 * @param mDataMap
	 *            插入数据库的名值对信息
	 * @return boolean 处理是否成功
	 */

	public boolean saveAccountInfo(AccountInfo info){
		boolean flag = true;
		try {
			MDataMap insertMap = new MDataMap();
			insertMap.put("account_code", WebHelper.upCode("JS"));
			insertMap.put("account_status", info.getAccount_status());
			insertMap.put("create_time", info.getCreate_time());
			insertMap.put("create_user", info.getCreate_user());
			insertMap.put("end_time", info.getEnd_time());
			insertMap.put("seller_code", info.getSeller_code());
			insertMap.put("start_time", info.getStart_time());
			insertMap.put("account_amount", String.valueOf(info.getAccount_amount().doubleValue()));
			insertMap.put("procedure_amount", String.valueOf(info.getProcedure_amount().doubleValue()));
			insertMap.put("sellershare_amount", String.valueOf(info.getSellershare_amount().doubleValue()));
			insertMap.put("storeshare_amount", String.valueOf(info.getStoreshare_amount().doubleValue()));
			DbUp.upTable(ACCOUNT_NAME).dataInsert(insertMap);
			insertRelation(info,insertMap.get("account_code"));
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	
	/**
	 * 插入关系信息
	 * 
	 * @param mDataMap
	 *            插入数据库的名值对信息
	 * @return boolean 处理是否成功
	 */
	public boolean insertRelation(AccountInfo info,String account_code) {
		boolean flag = true;
		try {
			info.getReOrders().addAll(info.getList());
			Iterator<AccountDetail> iterator = info.getReOrders().iterator();
			while (iterator.hasNext()) {
				AccountDetail accountDetail = (AccountDetail) iterator.next();
				DbUp.upTable(ACCOUNT_RELATIONNAME).dataInsert(objectToMap(accountDetail, account_code));
			}
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	private MDataMap objectToMap(AccountDetail detail,String account_code){
		MDataMap map = new MDataMap();
		map.put("account_code", account_code);
		map.put("account_time", detail.getAccount_time());
		map.put("order_code", detail.getOrder_code());
		map.put("order_time", detail.getOrder_time());
		map.put("pay_type", detail.getPay_type());
		map.put("remark", detail.getRemark());
		map.put("return_code", detail.getReturn_code());
		map.put("return_time", detail.getReturn_time());
		map.put("account_money", String.valueOf(detail.getAccount_money().doubleValue()));
		map.put("order_money", String.valueOf(detail.getOrder_money().doubleValue()));
		map.put("pay_cost", String.valueOf(detail.getPay_cost().doubleValue()));
		map.put("return_money", String.valueOf(detail.getReturn_money().doubleValue()));
		map.put("return_cost", String.valueOf(detail.getReturn_cost().doubleValue()));
		map.put("storeshare_money", String.valueOf(detail.getStoreshare_money().doubleValue()));
		return map;
	}
	/**
	 * 更新结算主信息的
	 * 
	 * @param mDataMap
	 *            更新数据库的名值对信息
	 * @return boolean 处理是否成功
	 */

	public boolean updateAccountInfo(MDataMap mDataMap) {
		boolean flag = true;
		try {
			MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
			DbUp.upTable(ACCOUNT_NAME).update(mAddMaps);
			this.insertAccountStatusLog(mAddMaps);
		} catch (Exception e) {
			flag = false;
			// 异常处理待定
		}
		return flag;
	}

	/**
	 * 插入状态更新记录信息
	 * 
	 * @param mDataMap
	 *            插入数据库的名值对信息
	 * @return boolean 处理是否成功
	 */
	public boolean insertAccountStatusLog(MDataMap mDataMap) {
		boolean flag = true;
		try {
			MDataMap map = new MDataMap();
			map.put("account_code", mDataMap.get("account_code"));
			map.put("account_status", mDataMap.get("account_status"));
			SimpleDateFormat df = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			map.put("create_time", DateUtil.toString(new Date(), df));
			// 从缓存中读取当前用户的用户标识,待定
			map.put("create_user", UserFactory.INSTANCE.create().getUserCode());
			DbUp.upTable(ACCOUNT_LOGNAME).dataInsert(map);
		} catch (Exception e) {
			flag = false;
			// 异常处理待定
		}
		return flag;
	}
}
