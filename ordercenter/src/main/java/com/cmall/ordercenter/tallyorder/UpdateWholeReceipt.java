package com.cmall.ordercenter.tallyorder;

import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 为所有符合查询条件的开收据
 * @author lzf
 *2018年3月13日10:12:15
 */
public class UpdateWholeReceipt extends RootFunc {

	@Override
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap fieldMap = upFieldMap(mDataMap);
		
		// 已付款
		fieldMap.put("payment_type", "449748100001");
		// 未开
		fieldMap.put("receipt_retention_money_type", "449748110002");
		
		String sql = "update v_oc_seller_retention_money_receipt set receipt_retention_money_type = '449748110001', receipt_retention_money_time = '"+FormatHelper.upDateTime("yyyy-MM-dd")+"'";
		sql += " where 1 = 1";
		
		// 先移除范围查询字段，避免下面的循环当成普通字段加入条件
		String payment_time_zw_a_between_from = fieldMap.remove("payment_time_zw_a_between_from");
		String payment_time_zw_a_between_to = fieldMap.remove("payment_time_zw_a_between_to");
		
		for(Entry<String, String> entry : fieldMap.entrySet()){
			sql += " and "+entry.getKey()+" = :"+entry.getKey();
		}
		
		// 对范围查询的字段特殊处理
		if(StringUtils.isNotBlank(payment_time_zw_a_between_from)) {
			sql += " and payment_time >= :payment_time_zw_a_between_from";
			fieldMap.put("payment_time_zw_a_between_from", payment_time_zw_a_between_from);
		}
		if(StringUtils.isNotBlank(payment_time_zw_a_between_to)) {
			sql += " and payment_time <= :payment_time_zw_a_between_to";
			fieldMap.put("payment_time_zw_a_between_to", payment_time_zw_a_between_to);
		}
		
		int success = DbUp.upTable("v_oc_seller_retention_money_receipt").dataExec(sql, fieldMap);
		
		mResult.setResultMessage("操作成功!更新记录数:" + success);
		return mResult;
	}

}
