

package com.cmall.ordercenter.service.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.model.api.ApiRetentionAdjustMoneyManageInput;
import com.cmall.ordercenter.model.api.ApiRetentionAdjustMoneyManageResult;
import com.cmall.ordercenter.model.api.ApiRetentionMoneyManageInput;
import com.cmall.ordercenter.model.api.ApiRetentionMoneyManageResult;
import com.cmall.ordercenter.service.RetentionMoneyReceiptService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;

/**
 * 
 * 类: ApiRetentionAdjustMoneyManage <br>
 * 描述: 商户质保金调整接口管理 <br>
 * 作者: zhangbo<br>
 * 时间: 2018年9月11日 
 */
public class ApiRetentionAdjustMoneyManage extends RootApi<ApiRetentionAdjustMoneyManageResult, ApiRetentionAdjustMoneyManageInput> {

	RetentionMoneyReceiptService receiptService = new RetentionMoneyReceiptService();
	
	@Override
	public ApiRetentionAdjustMoneyManageResult Process(ApiRetentionAdjustMoneyManageInput input, MDataMap mRequestMap) {
		ApiRetentionAdjustMoneyManageResult result = new ApiRetentionAdjustMoneyManageResult();
		String userName = UserFactory.INSTANCE.create().getUserCode();
		String date = DateUtil.getSysDateTimeString();
		try {
			// 根据small_seller_code查询商户是否存在
			Map<String, Object> map = DbUp.upTable("oc_seller_retention_money_receipt").dataSqlOne(
					"select * from oc_seller_retention_money_receipt where small_seller_code=:small_seller_code and receipt_retention_money_code=:receipt_retention_money_code",
					new MDataMap("small_seller_code", input.getSmallSellerCode(),"receipt_retention_money_code",input.getReceiptRetentionMoneyCode()));
			if (map != null) {
				// 质保金金额
				Double retention_money = Double.valueOf(map.get("retention_money").toString());
				// 调整金额
				Double ajust_money = Double.valueOf(input.getAdjustRetentionMoney());
				// 质保金收据编号
				String receipt_retention_money_code = map.get("receipt_retention_money_code").toString();
				//调整原因
				String ajust_reason = input.getAjustReason(); 
				//日志
				MDataMap log = new MDataMap();
				log.put("small_seller_code", input.getSmallSellerCode());
				log.put("adjust_money",input.getAdjustRetentionMoney());
				log.put("adjust_time", date);
				log.put("adjust_reason", ajust_reason);
				log.put("operator", userName);
				log.put("receipt_retention_money_code", receipt_retention_money_code);
				
				MDataMap update = new MDataMap();
				update.put("small_seller_code", input.getSmallSellerCode());
				update.put("receipt_retention_money_code", receipt_retention_money_code);
				update.put("ajust_money",input.getAdjustRetentionMoney());
				update.put("receipt_retention_money_code",receipt_retention_money_code);
				
				DbUp.upTable("oc_seller_retention_money_receipt").dataUpdate(update,"ajust_money","small_seller_code,receipt_retention_money_code");
				/**
				 * 记录日志
				 */
				DbUp.upTable("lc_retention_adjust_money").dataInsert(log);
				result.setResultCode(1);
				result.setResultMessage("操作成功");
			} else {
				result.setResultCode(-1);
				result.setResultMessage("商户不存在");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setResultCode(-1);
			result.setResultMessage("操作失败，请联系技术人员");
		}
		return result;
	}

	
	
	private static String settleType(String smallSellerCode) {
		String settleType = "";
		Map<String, Object> seller = DbUp.upTable("uc_seller_info_extend").dataSqlOne(
				"select * from uc_seller_info_extend where small_seller_code=:small_seller_code",
				new MDataMap("small_seller_code", smallSellerCode));
		if (StringUtils.equals("4497478100050001", seller.get("uc_seller_type").toString())
				|| StringUtils.equals("4497478100050005", seller.get("uc_seller_type").toString())) {
			settleType = "4497477900040001";
		} else if (StringUtils.equals("4497478100050002", seller.get("uc_seller_type").toString())) {
			settleType = "4497477900040002";
		} else if (StringUtils.equals("4497478100050003", seller.get("uc_seller_type").toString())) {
			settleType = "4497477900040003";
		} else if (StringUtils.equals("4497478100050004", seller.get("uc_seller_type").toString())) {
			settleType = "4497477900040004";
		}
		return settleType;
	}

	private static String accountType(String smallSellerCode) {
		String accountType = "";
		Map<String, Object> seller = DbUp.upTable("uc_seller_info_extend").dataSqlOne(
				"select * from uc_seller_info_extend where small_seller_code=:small_seller_code",
				new MDataMap("small_seller_code", smallSellerCode));
		if (StringUtils.equals("4497478100030003", seller.get("account_clear_type").toString())) {
			accountType = "4497477900030001";
		} else if (StringUtils.equals("4497478100030004", seller.get("account_clear_type").toString())) {
			accountType = "4497477900030002";
		}
		return accountType;
	}
}
