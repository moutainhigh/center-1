package com.cmall.ordercenter.tallyorder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.service.RetentionMoneyReceiptService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebSessionHelper;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 确认后的付款单如果待付金额为负,确认收货后将其状态变为已收款
 * 侍款申请单新增状态4497477900010006已收款
 * @author Administrator
 *
 */
public class UpdateApplyForPayStatusToGetMoney extends RootFunc {
	private static SimpleDateFormat timeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	RetentionMoneyReceiptService receiptService = new RetentionMoneyReceiptService();
	
	@Override
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		String uid = mDataMap.get("uid");
		String payCode = mDataMap.get("payCode");
		String sql = "update oc_bill_apply_payment set flag='4497477900010006' "
				+ "where uid='" + uid + "'";
		int count = DbUp.upTable("oc_bill_apply_payment").dataExec(sql, mDataMap);
		//mResult.setResultCode(939303103);
		if(count == 0) {
			mResult.setResultMessage("更新记录数:0.请检查是否有新导入付款日期的记录");
		} else {
			mResult.setResultMessage("操作成功!");
		}
		
		//记日志
		Date now = new Date();
		String creator = UserFactory.INSTANCE.create().getLoginName();
		try{
			if (mResult.upFlagTrue()) {
				String ip = WebSessionHelper.create().upIpaddress();
				MDataMap logMap = new MDataMap();
				logMap.put("pay_code", payCode);
				logMap.put("flag", "4497477900010006");
				logMap.put("ip", ip);
				logMap.put("comment", "付款申请单已收款");
				logMap.put("create_time", timeSdf.format(now));
				logMap.put("creator", creator);
				DbUp.upTable("lc_apply_for_payment").dataInsert(logMap);
			}
		}catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(939301228);
		}
		
		//变更商户结算单状态(已结算)
		try {
			sql = "select merchant_code,settle_codes,pay_time from oc_bill_apply_payment "
					+ "where flag='4497477900010006' and is_pay='4497477900020001' and pay_time is not null and uid ='" + uid +"'";
			List<Map<String, Object>> applyList = DbUp.upTable("oc_bill_apply_payment").dataSqlList(sql, new MDataMap());
			if(null != applyList && applyList.size() > 0) {
				for(Map<String, Object> apply : applyList) {
					String settleCodes = apply.get("settle_codes") == null ? "" : apply.get("settle_codes").toString();
					String merchant_code = apply.get("merchant_code") == null ? "" : apply.get("merchant_code").toString();
					if(StringUtils.isNotEmpty(merchant_code)) {
						if(StringUtils.isNotEmpty(settleCodes)) {
							settleCodes = settleCodes.replaceAll(",", "','");
							sql = "update oc_bill_merchant_new set flag='4497476900040009' where settle_code in('" + settleCodes + "') and merchant_code='" + merchant_code + "'";
							count = DbUp.upTable("oc_bill_merchant_new").dataExec(sql, new MDataMap());
							
							// 更新质保金收据的付款状态
							receiptService.updatePaymentStatusByBill(apply.get("settle_codes")+"",apply.get("merchant_code")+"",apply.get("pay_time")+"");
						}
						mResult.setResultMessage(bInfo(969909001));
						mResult.setResultCode(969909001);
					} else {
						mResult.setResultCode(939303201);
						mResult.setResultMessage(bInfo(939303201));
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return mResult;
	}
}
