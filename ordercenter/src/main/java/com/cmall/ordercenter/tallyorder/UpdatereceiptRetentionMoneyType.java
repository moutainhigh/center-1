package com.cmall.ordercenter.tallyorder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebSessionHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 
 * 作者: lzf<br>
 * 时间: 2018年3月2日17:12:04
 */
public class UpdatereceiptRetentionMoneyType extends RootFunc {
	private static SimpleDateFormat timeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		String payCode = mSubMap.get("payCode");
		// 记日志
		Date now = new Date();
		String creator = UserFactory.INSTANCE.create().getLoginName();
		try {
			if (mResult.upFlagTrue()) {
				String ip = WebSessionHelper.create().upIpaddress();
				MDataMap logMap = new MDataMap();
				logMap.put("pay_code", payCode);
				logMap.put("flag", "4497477900060006");
				logMap.put("ip", ip);
				logMap.put("comment", "付款申请单审核已确认");
				logMap.put("create_time", timeSdf.format(now));
				logMap.put("creator", creator);
				DbUp.upTable("lc_apply_for_payment").dataInsert(logMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(939301228);
		}

		// 变更商户结算单状态(已审核)
		try {
			String sql = "";
			Map<String, Object> applyMap = DbUp.upTable("oc_bill_apply_payment_kj").dataSqlOne(
					"select merchant_code,settle_codes from oc_bill_apply_payment_kj where pay_code=:payCode",
					new MDataMap("payCode", payCode));
			String settleCodes = applyMap.get("settle_codes") == null ? "" : applyMap.get("settle_codes").toString();
			String merchant_code = applyMap.get("merchant_code") == null ? ""
					: applyMap.get("merchant_code").toString();
			if (StringUtils.isNotEmpty(merchant_code)) {
				settleCodes = settleCodes.replaceAll(",", "','");
				if (StringUtils.isNotEmpty(settleCodes)) {
					sql = "update oc_bill_merchant_new_spec set flag='4497476900040011' where settle_code in('" + settleCodes
							+ "') and merchant_code='" + merchant_code + "'";
					DbUp.upTable("oc_bill_merchant_new_spec").dataExec(sql, new MDataMap());
				}
				/**
				 * 修改付款申请单的状态
				 */
				MDataMap updatePayment = new MDataMap();
				updatePayment.put("pay_code", payCode);
				updatePayment.put("flag", "4497477900060006");
				DbUp.upTable("oc_bill_apply_payment_kj").dataUpdate(updatePayment, "flag", "pay_code");
				mResult.setResultMessage(bInfo(969909001));
				mResult.setResultCode(1);
			} else {
				mResult.setResultCode(939303201);
				mResult.setResultMessage(bInfo(939303201));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mResult;
	}
}
