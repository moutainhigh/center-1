package com.cmall.ordercenter.tallyorder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebSessionHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 
 * 类: UpdatePayForAgainstAudit <br>
 * 跨境相关-付款申请单反审核(将付款申请单状态改为待审核)<br>
 * 4497477900060001 商品行政待审核<br>
 * 4497477900060002 商品行政审核通过<br>
 * 4497477900060003 商品行政驳回<br>
 * 4497477900060004 财务审核通过<br>
 * 4497477900060005 财务驳回<br>
 * 4497477900060006 财务已确认<br>
 * 4497477900060007 财务已付款<br>
 * 作者: zhy<br>
 * 时间: 2017年5月3日 下午4:08:45
 */
public class UpdatePayForAgainstAuditKj extends RootFunc {
	private static SimpleDateFormat timeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		String payCode = mSubMap.get("payCode");
		String reject_reason = mSubMap.get("reject_reason") != null?mSubMap.get("reject_reason"):"";
		// 记日志
		Date now = new Date();
		String creator = UserFactory.INSTANCE.create().getLoginName();
		try {
			if (mResult.upFlagTrue()) {
				String ip = WebSessionHelper.create().upIpaddress();
				MDataMap logMap = new MDataMap();
				logMap.put("pay_code", payCode);
				logMap.put("flag", "4497477900060008");
				logMap.put("ip", ip);
				logMap.put("comment", " 财务反审核"+reject_reason);
				logMap.put("create_time", timeSdf.format(now));
				logMap.put("creator", creator);
				DbUp.upTable("lc_apply_for_payment").dataInsert(logMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(939301228);
		}

		// 变更商户结算单状态(待审核)
		try {
			Map<String, Object> map = DbUp.upTable("oc_bill_apply_payment_kj").dataSqlOne(
					"select * from oc_bill_apply_payment_kj where pay_code=:payCode", new MDataMap("payCode", payCode));
			if (map != null) {
				/**
				 * 修改付款申请单的状态
				 */
				MDataMap updatePayment = new MDataMap();
				updatePayment.put("pay_code", payCode);
				updatePayment.put("flag", "4497477900060008");
				updatePayment.put("reject_reason", reject_reason);
				DbUp.upTable("oc_bill_apply_payment_kj").dataUpdate(updatePayment, "flag,reject_reason", "pay_code");
				/**
				 * 更改结算单状态为待审核
				 */
				MDataMap updateSpec = new MDataMap();
				updateSpec.put("flag", "4497476900040010");
				updateSpec.put("settle_code", map.get("settle_codes").toString());
				DbUp.upTable("oc_bill_merchant_new_spec").dataUpdate(updateSpec, "flag", "settle_code");
				
				mResult.setResultMessage(bInfo(969909001));
				mResult.setResultCode(1);
			} else {
				mResult.setResultCode(-1);
				mResult.setResultMessage("付款申请单不存在");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mResult;
	}

}
