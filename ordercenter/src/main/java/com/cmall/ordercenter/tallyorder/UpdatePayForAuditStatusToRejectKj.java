package com.cmall.ordercenter.tallyorder;

import java.util.Map;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebSessionHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 
 * 类: UpdatePayForAuditStatusToRejectKj <br>
 * 跨境相关-付款申请单审核-拒绝(将付款申请单状态改为已拒绝)<br>
 * 4497477900060001 商品行政待审核<br>
 * 4497477900060002 商品行政审核通过<br>
 * 4497477900060003 商品行政驳回<br>
 * 4497477900060004 财务审核通过<br>
 * 4497477900060005 财务驳回<br>
 * 4497477900060006 财务已确认<br>
 * 4497477900060007 财务已付款<br>
 * 作者: zhy<br>
 * 时间: 2017年5月3日 下午5:42:28
 */
public class UpdatePayForAuditStatusToRejectKj extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		String uid = mSubMap.get("uid");
		String payCode = mSubMap.get("payCode");
		String rejectReason = mSubMap.get("reject_reason");
		String creator = UserFactory.INSTANCE.create().getLoginName();
		try {
			if (mResult.upFlagTrue()) {
				String ip = WebSessionHelper.create().upIpaddress();
				MDataMap logMap = new MDataMap();
				logMap.put("pay_code", payCode);
				logMap.put("flag", "4497477900060005");
				logMap.put("ip", ip);
				logMap.put("comment", "付款申请单审核被拒绝");
				logMap.put("create_time", DateUtil.getSysDateTimeString());
				logMap.put("creator", creator);
				DbUp.upTable("lc_apply_for_payment").dataInsert(logMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(939301228);
		}

		try {
			Map<String, Object> map = DbUp.upTable("oc_bill_apply_payment_kj").dataSqlOne(
					"select * from oc_bill_apply_payment_kj where pay_code=:payCode", new MDataMap("payCode", payCode));
			if (map != null) {
				DbUp.upTable("oc_bill_apply_payment_kj").dataUpdate(
						new MDataMap("uid", uid, "flag", "4497477900060005", "reject_reason", rejectReason),
						"flag,reject_reason", "uid");
				/**
				 * 更改结算单状态为待审核
				 */
				MDataMap updateSpec = new MDataMap();
				updateSpec.put("flag", "4497476900040010");
				updateSpec.put("settle_code", map.get("settle_codes").toString());
				DbUp.upTable("oc_bill_merchant_new_spec").dataUpdate(updateSpec, "flag", "settle_code");
				mResult.setResultMessage(bInfo(969909001));
				mResult.setResultCode(969909001);
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
