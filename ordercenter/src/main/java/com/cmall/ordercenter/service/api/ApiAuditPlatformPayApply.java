package com.cmall.ordercenter.service.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cmall.ordercenter.service.api.ApiAuditPlatformPayApply.ApiAuditPlatformPayApplyInput;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebSessionHelper;
import com.srnpr.zapweb.webfactory.UserFactory;

/**
 * 商户结算5.0
 * 商品行政审核平台入驻付款申请单(商品行政审核通过或商品行政驳回)
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
 *
 */
public class ApiAuditPlatformPayApply extends RootApi<RootResult, ApiAuditPlatformPayApplyInput> {
	private static SimpleDateFormat timeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Override
	public RootResult Process(ApiAuditPlatformPayApplyInput inputParam, MDataMap mRequestMap) {
		// TODO Auto-generated method stub
		RootResult result = new RootResult();
		String payCode = inputParam.getPayCode();
		if(StringUtils.isEmpty(payCode)) {
			result.setResultCode(-1);
			result.setResultMessage("平台入驻付款申请单号为空!");
			return result;
		}
		
		String status = inputParam.getStatus();
		if(StringUtils.isEmpty(status)) {
			result.setResultCode(-1);
			result.setResultMessage("平台入驻付款申请单更新状态为空!");
			return result;
		}
		
		int count = DbUp.upTable("oc_bill_apply_payment_pt").dataUpdate(new MDataMap("flag",status, "pay_code", payCode, "comment", inputParam.getComment()), 
				"flag,comment", "pay_code");
		//修改商户结算单状态
		try {
			String sql = "";
			Map<String, Object> map = DbUp.upTable("oc_bill_apply_payment_pt").dataSqlOne("select merchant_code,settle_codes from oc_bill_apply_payment_pt where pay_code=:payCode", new MDataMap("payCode", payCode));
			String settleCodes = map.get("settle_codes") == null ? "" : map.get("settle_codes").toString();
			String merchant_code = map.get("merchant_code") == null ? "" : map.get("merchant_code").toString();
			if(StringUtils.isNotEmpty(merchant_code)) {
				settleCodes = settleCodes.replaceAll(",", "','");
				if(StringUtils.isNotEmpty(settleCodes)) {
					sql = "update oc_bill_merchant_new_spec set flag='" + getSettleStatus(status)  + "' where settle_code in('" + settleCodes + "') and merchant_code='" + merchant_code + "'";
					count = DbUp.upTable("oc_bill_merchant_new").dataExec(sql, new MDataMap());
				}
			} else {
				result.setResultCode(939303201);
				result.setResultMessage(bInfo(939303201));
			}

		} catch(Exception e) {
			e.printStackTrace();
		}
		
		if(count > 0) {			
			result.setResultMessage("执行成功!");
		}
		
		//记录日志
		String creator = UserFactory.INSTANCE.create().getLoginName();
		String ip = WebSessionHelper.create().upIpaddress();
		MDataMap logMap = new MDataMap();
		logMap.put("pay_code", payCode);
		logMap.put("flag", status);
		logMap.put("ip", ip);
		logMap.put("comment", getLogComment(inputParam.getComment()));
		logMap.put("create_time", timeSdf.format(new Date()));
		logMap.put("creator", creator);
		DbUp.upTable("lc_apply_for_payment").dataInsert(logMap);
		return result;
	}
	
	private String getLogComment(String inComment) {
		String comment = "";
		if(StringUtils.isNotBlank(inComment)) {
			comment += " 备注:" + inComment;
		}
		return comment;
	}
	
	//根据付款申请单的状态取得其相应结算单的状态
	private String getSettleStatus(String status) {
		String settleStatus = "";
		if(status.equals("4497477900060002")) {
			//付款申请单:商品行政审核通过 --> 结算单:待审核
			settleStatus = "4497476900040010";
		} else if (status.equals("4497477900060003")) {
			//付款申请单:商品行政审核驳回 --> 结算单:待审核
			//商品行政与商户线下沟通,不改商户结算单待审核状态
			settleStatus = "4497476900040010";
		} else if (status.equals("4497477900060004")) {
			//付款申请单:财务审核通过 --> 结算单:待审核
			settleStatus = "4497476900040010";
		} else if (status.equals("4497477900060005")) {
			//付款申请单:财务驳回 --> 结算单:待审核
			settleStatus = "4497476900040010";
			
		}
		return settleStatus;
	}

	public static class ApiAuditPlatformPayApplyInput extends RootInput
	{
		public ApiAuditPlatformPayApplyInput() {}
		
		@ZapcomApi(value = "平台入驻付款申请单号", require=1)
		private String payCode = "";
		
		@ZapcomApi(value = "申请单状态", require=1)
		private String status = "";
		
		@ZapcomApi(value = "审核备注", require=0)
		private String comment = "";

		public String getPayCode() {
			return payCode;
		}

		public void setPayCode(String payCode) {
			this.payCode = payCode;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}
	}
}


