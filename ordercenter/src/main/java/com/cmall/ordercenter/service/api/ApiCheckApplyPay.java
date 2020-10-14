package com.cmall.ordercenter.service.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.cmall.ordercenter.service.api.ApiCheckApplyPay.ApiCheckApplyPayInput;
import com.cmall.ordercenter.service.api.ApiCheckApplyPay.ApiCheckApplyPayResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 商户结算3.0-创建结算单时检查发票号与结算单是否合规
 * @author zht
 *
 */
public class ApiCheckApplyPay extends RootApi<ApiCheckApplyPayResult, ApiCheckApplyPayInput> {

	public static class ApiCheckApplyPayResult extends RootResult {
		private int passed;
		private String reason;
		public int getPassed() {
			return passed;
		}
		public void setPassed(int passed) {
			this.passed = passed;
		}
		public String getReason() {
			return reason;
		}
		public void setReason(String reason) {
			this.reason = reason;
		}
	}
	
	public static class ApiCheckApplyPayInput extends RootInput {
		private String invoiceCodes;	//发票号
		private String settleCodes;	    //结算单号
		private String sellerCode;      //商户编号
		private String payCode;			//付款申请单编号
		public String getInvoiceCodes() {
			return invoiceCodes;
		}
		public void setInvoiceCodes(String invoiceCodes) {
			this.invoiceCodes = invoiceCodes;
		}
		public String getSettleCodes() {
			return settleCodes;
		}
		public void setSettleCodes(String settleCodes) {
			this.settleCodes = settleCodes;
		}
		public String getSellerCode() {
			return sellerCode;
		}
		public void setSellerCode(String sellerCode) {
			this.sellerCode = sellerCode;
		}
		public String getPayCode() {
			return payCode;
		}
		public void setPayCode(String payCode) {
			this.payCode = payCode;
		}
	}

	/**
	 * 查询新的发票号与结算单号是否与过去的付款申请单绑定的发票号和结算单号重复
	 */
	@Override
	public ApiCheckApplyPayResult Process(ApiCheckApplyPayInput input,
			MDataMap rMap) {
		ApiCheckApplyPayResult result = new ApiCheckApplyPayResult();
		String sellerCode = input.getSellerCode();
		String payCode = input.getPayCode();	
		//待审核   4497477900010001@审核通过 4497477900010002@已确认   4497477900010003@已付款   4497477900010004
		//拒绝    4497477900010005
		//查询某商户已有的状态为待审核|审核通过|已确认|已付款的付款申请单
		//新增发票号不能与上述状态的付款申请单绑定
		String invoiceCodes = input.getInvoiceCodes(); //rMap.get("zw_f_invoice_codes");
		Map<String, String> sameMap = checkSame(sellerCode, invoiceCodes, "invoice_codes", payCode);
		if(sameMap.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("提交失败！下列发票已提交付款申请.<br>");
			Set<Entry<String, String>> entrys = sameMap.entrySet();
			Iterator<Entry<String, String>> it = entrys.iterator();
			while(it.hasNext()) {
				Entry<String, String> entry = it.next();
				sb.append("付款申请单:").append(entry.getValue()).append("--发票号:").append(entry.getKey()).append("<br>");
			}
			result.setPassed(0);
			result.setReason(sb.toString());
			return result;
		}
		//查询某商户已有的状态为待审核|审核通过|已确认|已付款的付款申请单
		//新增结算单号不能与上述状态的付款申请单绑定
		String settleCodes = input.getSettleCodes(); //rMap.get("zw_f_settle_codes");
		sameMap = checkSame(sellerCode, settleCodes, "settle_codes", payCode);
		if(sameMap.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("提交失败！下列结算单已提交付款申请.<br>");
			Set<Entry<String, String>> entrys = sameMap.entrySet();
			Iterator<Entry<String, String>> it = entrys.iterator();
			while(it.hasNext()) {
				Entry<String, String> entry = it.next();
				sb.append("付款申请单:").append(entry.getValue()).append("--结算单号:").append(entry.getKey()).append("<br>");
			}
			result.setPassed(0);
			result.setReason(sb.toString());
			return result;
		}	
		//校验通过
		result.setPassed(1);
		return result;
	}
	
	private Map<String, String> checkSame(String sellerCode, String newValues, String compareColumn, String payCodeSelf) {
		Map<String, String> sameMap = new HashMap<String, String>();
		String sql = "select pay_code, " + compareColumn + " from oc_bill_apply_payment " + "where merchant_code='" + sellerCode 
				+ "' and flag in ('4497477900010001','4497477900010002','4497477900010003','4497477900010004') ";
		if(!StringUtils.isEmpty(payCodeSelf)) {
			//修改申请单时,payCode不为空,查询重复时
			//排除本单已关联的发票和结算单
			sql += "and pay_code !='" + payCodeSelf + "'";
		}
		List<Map<String, Object>> compareColumnList = DbUp.upTable("oc_bill_apply_payment").dataSqlList(sql, null);
		if(null != compareColumnList && compareColumnList.size() > 0) {
			String[] valueNew = newValues.split(",");
			for(Map<String, Object> entry : compareColumnList) {
				String payCode = (String) entry.get("pay_code");
				String compareValues = (String) entry.get(compareColumn);
				if(!StringUtils.isEmpty(compareValues)) {
					String[] valueExists = compareValues.split(",");
					for(String exist : valueExists) {
						for(String vNew : valueNew) {
							if(vNew.equals(exist)) {
								sameMap.put(exist, payCode);
							}
						}
					}
				}
			}
		}
		return sameMap;
	}
}
