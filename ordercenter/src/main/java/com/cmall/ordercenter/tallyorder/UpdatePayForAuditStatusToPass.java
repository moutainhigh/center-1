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
 * hw 16/4/1
 *  商户结算3.0-付款申请单审核-通过(将付款申请单状态改为已通过)
 *	待审核   4497477900010001
 *	审核通过 4497477900010002
 *	已确认   4497477900010003
 *	已付款   4497477900010004
 *	拒绝     4497477900010005
 */
public class UpdatePayForAuditStatusToPass extends RootFunc{
	private static SimpleDateFormat timeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		String uid=mSubMap.get("uid");
		String payCode=mSubMap.get("payCode");
		DbUp.upTable("oc_bill_apply_payment").dataUpdate(new MDataMap("uid",uid,"flag","4497477900010002"),"flag", "uid");
		//记日志
		Date now = new Date();
		String creator = UserFactory.INSTANCE.create().getLoginName();
		try{
			if (mResult.upFlagTrue()) {
				String ip = WebSessionHelper.create().upIpaddress();
				MDataMap logMap = new MDataMap();
				logMap.put("pay_code", payCode);
				logMap.put("flag", "4497477900010002");
				logMap.put("ip", ip);
				logMap.put("comment", "付款申请单审核被通过");
				logMap.put("create_time", timeSdf.format(now));
				logMap.put("creator", creator);
				DbUp.upTable("lc_apply_for_payment").dataInsert(logMap);
			}
		}catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(939301228);
		}
		
		//变更商户结算单状态(待审核)
		try {
			String sql = "";
			Map<String, Object> map = DbUp.upTable("oc_bill_apply_payment").dataSqlOne("select merchant_code,settle_codes from oc_bill_apply_payment where pay_code=:payCode", new MDataMap("payCode", payCode));
			String settleCodes = map.get("settle_codes") == null ? "" : map.get("settle_codes").toString();
			String merchant_code = map.get("merchant_code") == null ? "" : map.get("merchant_code").toString();
			if(StringUtils.isNotEmpty(merchant_code)) {
				settleCodes = settleCodes.replaceAll(",", "','");
				if(StringUtils.isNotEmpty(settleCodes)) {
					sql = "update oc_bill_merchant_new set flag='4497476900040010' where settle_code in('" + settleCodes + "') and merchant_code='" + merchant_code + "'";
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

		//mResult.setResultCode(939303103);
//		mResult.setResultMessage("付款申请单[" + payCode + "]审批通过!");
		return mResult;
	}
}
