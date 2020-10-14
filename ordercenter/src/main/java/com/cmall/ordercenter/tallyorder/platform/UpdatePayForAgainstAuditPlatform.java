package com.cmall.ordercenter.tallyorder.platform;

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
 * 商户结算5.0-平台入驻付款申请单财务审核通过后反审核(将付款申请单状态改为财务反审核,在付款申请审核确认列表[财务使用]中出现)
 * 审核平台入驻商户付款申请单状态
 * 4497477900060001 : 商品行政待审核
 * 4497477900060002 : 商品行政审核通过
 * 4497477900060003 : 商品行政驳回
 * 4497477900060004 : 财务审核通过
 * 4497477900060005 : 财务驳回
 * 4497477900060006 : 财务已确认
 * 4497477900060007 : 财务已付款
 * 4497477900060008 : 财务反审核
 * 
 * 平台入驻商户结算单状态
 * 4497476900040008 : 未结算
 * 4497476900040009 : 已结算
 * 4497476900040010 : 待审核
 * 4497476900040011 : 已审核
 * @author zht
 */
public class UpdatePayForAgainstAuditPlatform extends RootFunc{
	private static SimpleDateFormat timeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		String uid = mSubMap.get("uid");
		String payCode = mSubMap.get("payCode");
		String rejectReason = mSubMap.get("reject_reason");
		DbUp.upTable("oc_bill_apply_payment_pt").dataUpdate(new MDataMap("uid",uid,"flag","4497477900060008"),"flag", "uid");
		//记日志
		Date now = new Date();
		String creator = UserFactory.INSTANCE.create().getLoginName();
		try{
			if (mResult.upFlagTrue()) {
				String ip = WebSessionHelper.create().upIpaddress();
				MDataMap logMap = new MDataMap();
				logMap.put("pay_code", payCode);
				logMap.put("flag", "4497477900060008");
				logMap.put("ip", ip);
				String comment = "平台入驻付款申请单被财务反审核" + (StringUtils.isNotBlank(rejectReason) ? " 备注:" + rejectReason : "");
				logMap.put("comment", comment);
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
			Map<String, Object> map = DbUp.upTable("oc_bill_apply_payment_pt").dataSqlOne("select merchant_code,settle_codes from oc_bill_apply_payment_pt where pay_code=:payCode", new MDataMap("payCode", payCode));
			String settleCodes = map.get("settle_codes") == null ? "" : map.get("settle_codes").toString();
			String merchant_code = map.get("merchant_code") == null ? "" : map.get("merchant_code").toString();
			if(StringUtils.isNotEmpty(merchant_code)) {
				settleCodes = settleCodes.replaceAll(",", "','");
				if(StringUtils.isNotEmpty(settleCodes)) {
					sql = "update oc_bill_merchant_new_spec set flag='4497476900040010' where settle_code in('" + settleCodes + "') and merchant_code='" + merchant_code + "'";
					int count = DbUp.upTable("oc_bill_merchant_new_spec").dataExec(sql, new MDataMap());
				}
				mResult.setResultMessage(bInfo(969909001));
				mResult.setResultCode(1);
			} else {
				mResult.setResultCode(939303201);
				mResult.setResultMessage(bInfo(939303201));
			}

		} catch(Exception e) {
			e.printStackTrace();
		}
		//mResult.setResultCode(939303103);
//		mResult.setResultMessage("操作成功");
		return mResult;
	}

}
