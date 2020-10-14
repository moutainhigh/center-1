package com.cmall.ordercenter.tallyorder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebSessionHelper;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 
 * 类: UpdatePayForAuditStatusAllPassKj <br>
 * 跨境相关-审核付款申请单-全部确认(将状态为已审核的付款申请单全部改为已确认)<br>
 * 4497477900060001 商品行政待审核<br>
 * 4497477900060002 商品行政审核通过<br>
 * 4497477900060003 商品行政驳回<br>
 * 4497477900060004 财务审核通过<br>
 * 4497477900060005 财务驳回<br>
 * 4497477900060006 财务已确认<br>
 * 4497477900060007 财务已付款<br>
 * 作者: zhy<br>
 * 时间: 2017年5月3日 下午2:58:43
 */
public class UpdatePayForAuditStatusAllPassKj extends RootFunc {
	private static SimpleDateFormat timeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		List<Map<String, Object>> payCodeList = DbUp.upTable("oc_bill_apply_payment_kj").dataSqlList(
				"select pay_code from oc_bill_apply_payment_kj where flag='4497477900060004'", new MDataMap());
		// 记日志
		Date now = new Date();
		String creator = UserFactory.INSTANCE.create().getLoginName();
		try {
			if (null != payCodeList && payCodeList.size() > 0) {
				for (Map<String, Object> entry : payCodeList) {
					String payCode = (String) entry.get("pay_code");
					if (!StringUtils.isEmpty(payCode) && mResult.upFlagTrue()) {
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

					// 变更商户结算单状态(已审核)
					try {
						String sql = "";
						Map<String, Object> applyMap = DbUp.upTable("oc_bill_apply_payment_kj").dataSqlOne(
								"select merchant_code,settle_codes from oc_bill_apply_payment_kj where pay_code=:payCode",
								new MDataMap("payCode", payCode));
						String settleCodes = applyMap.get("settle_codes") == null ? ""
								: applyMap.get("settle_codes").toString();
						String merchant_code = applyMap.get("merchant_code") == null ? ""
								: applyMap.get("merchant_code").toString();
						if (StringUtils.isNotEmpty(merchant_code)) {
							if (StringUtils.isNotEmpty(settleCodes)) {
								settleCodes = settleCodes.replaceAll(",", "','");
								sql = "update oc_bill_merchant_new_spec set flag='4497476900040011' where settle_code in('"
										+ settleCodes + "') and merchant_code='" + merchant_code + "'";
								DbUp.upTable("oc_bill_merchant_new_spec").dataExec(sql, new MDataMap());
							}
							mResult.setResultMessage(bInfo(969909001));
							mResult.setResultCode(1);
						} else {
							mResult.setResultCode(939303201);
							mResult.setResultMessage(bInfo(939303201));
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			DbUp.upTable("oc_bill_apply_payment_kj")
					.dataExec("UPDATE oc_bill_apply_payment_kj oc set oc.flag ='4497477900060006' where oc.flag ='4497477900060004'", new MDataMap());
		} catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(939301228);
		}

		return mResult;
	}

}
