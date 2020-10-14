package com.cmall.ordercenter.tallyorder;

import java.util.Map;

import com.cmall.ordercenter.service.RetentionMoneyReceiptService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class UpdateReleaseStatus extends RootFunc{

	/**
	 * 更新数据库财务结算表是否发布的状态 2代表已发布 0代表已确认 1代表未确认
	 */
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		String uid=mSubMap.get("uid");
		Map<String, String> skumap = DbUp.upTable("oc_bill_finance_amount").oneWhere("uid,start_time,end_time,tuistart,tuiend,settle_code,settle_type,"
				+ "account_type,settle_status", "","uid=:uid", "uid", uid);
		String start_time = skumap.get("start_time").toString();
		String end_time = skumap.get("end_time").toString();
		String tui_start = skumap.get("tuistart").toString();
		String tui_end = skumap.get("tuiend").toString();
		//结算单唯一编号
		String settle_code = skumap.get("settle_code").toString();
		//结算类型
		String settle_type = skumap.get("settle_type").toString();
		//结算帐期类型
		String account_type = skumap.get("account_type").toString();
		
		
		//帐务结算单状态
		String settle_status = skumap.get("settle_status").toString();
		//已确认才可以发布
		if(settle_status.equals("0")) {
			DbUp.upTable("oc_bill_finance_amount").dataUpdate(new MDataMap("uid",uid,"settle_status","2"),"settle_status", "uid");
			SendBillToSuppliers.sendToMerchant(settle_code,settle_type,account_type,start_time,end_time,tui_start,tui_end);//下发商户，商品详细信息
			//mResult.setResultCode(939303103);
			mResult.setResultMessage("操作成功");
			
			// 生成质保金收据
			new RetentionMoneyReceiptService().addReceiptFromBill(uid);
		}else{
			mResult.setResultMessage("请在确认的状态发布财务结算单!");
		}
		return mResult;
	}
}
