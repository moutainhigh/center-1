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
 * 商户结算3.0-审核付款申请单-确认(将付款申请单状态改为已确认)
 * 待审核   4497477900010001
 * 审核通过 4497477900010002
 * 已确认   4497477900010003
 * 已付款   4497477900010004
 * 拒绝     4497477900010005
 * @author zht
 *
 */
public class UpdatePayForAuditStatusToConfirm extends RootFunc {
	private static SimpleDateFormat timeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		String uid=mSubMap.get("uid");
		String payCode=mSubMap.get("payCode");
		DbUp.upTable("oc_bill_apply_payment").dataUpdate(new MDataMap("uid",uid,"flag","4497477900010003"),"flag", "uid");
		//记日志
		Date now = new Date();
		String creator = UserFactory.INSTANCE.create().getLoginName();
		try{
			if (mResult.upFlagTrue()) {
				String ip = WebSessionHelper.create().upIpaddress();
				MDataMap logMap = new MDataMap();
				logMap.put("pay_code", payCode);
				logMap.put("flag", "4497477900010003");
				logMap.put("ip", ip);
				logMap.put("comment", "付款申请单审核已确认");
				logMap.put("create_time", timeSdf.format(now));
				logMap.put("creator", creator);
				DbUp.upTable("lc_apply_for_payment").dataInsert(logMap);
			}
		}catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(939301228);
		}
		
		//变更商户结算单状态(已审核)
		try {
			String sql = "";
			Map<String, Object> applyMap = DbUp.upTable("oc_bill_apply_payment").dataSqlOne("select merchant_code,settle_codes from oc_bill_apply_payment where pay_code=:payCode", new MDataMap("payCode", payCode));
			String settleCodes = applyMap.get("settle_codes") == null ? "" : applyMap.get("settle_codes").toString();
			String merchant_code = applyMap.get("merchant_code") == null ? "" : applyMap.get("merchant_code").toString();
			if(StringUtils.isNotEmpty(merchant_code)) {
				settleCodes = settleCodes.replaceAll(",", "','");
				if(StringUtils.isNotEmpty(settleCodes)) {
					sql = "update oc_bill_merchant_new set flag='4497476900040011' where settle_code in('" + settleCodes + "') and merchant_code='" + merchant_code + "'";
					int count = DbUp.upTable("oc_bill_merchant_new").dataExec(sql, new MDataMap());
				}
				mResult.setResultMessage(bInfo(969909001));
				mResult.setResultCode(969909001);
			} else {
				mResult.setResultCode(939303201);
				mResult.setResultMessage(bInfo(939303201));
			}

		} catch(Exception e) {
			e.printStackTrace();
		}
		return mResult;
	}
}
