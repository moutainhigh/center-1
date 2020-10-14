package com.cmall.ordercenter.tallyorder;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 
 * @author lzf
 * 2018年3月13日10:12:00
 *
 */
public class UpdateReceipt extends RootFunc {

	@Override
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap fieldMap = upFieldMap(mDataMap);
		String code = fieldMap.get("receipt_retention_money_code");
		
		MDataMap updateMap = new MDataMap();
		updateMap.put("receipt_retention_money_code", code);
		updateMap.put("receipt_retention_money_type", "449748110001");
		updateMap.put("receipt_retention_money_time", FormatHelper.upDateTime("yyyy-MM-dd"));
		
		DbUp.upTable("oc_seller_retention_money_receipt").dataUpdate(updateMap,"receipt_retention_money_type,receipt_retention_money_time", "receipt_retention_money_code");
		mResult.setResultMessage("操作成功!");
		return mResult;
	}
}
