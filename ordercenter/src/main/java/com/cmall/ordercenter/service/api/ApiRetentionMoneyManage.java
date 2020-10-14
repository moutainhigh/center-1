package com.cmall.ordercenter.service.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.model.api.ApiRetentionMoneyManageInput;
import com.cmall.ordercenter.model.api.ApiRetentionMoneyManageResult;
import com.cmall.ordercenter.service.RetentionMoneyReceiptService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;

/**
 * 
 * 类: ApiRetentionMoneyManage <br>
 * 描述: 商户质保金管理 <br>
 * 作者: zhy<br>
 * 时间: 2017年6月7日 下午5:03:46
 */
public class ApiRetentionMoneyManage extends RootApi<ApiRetentionMoneyManageResult, ApiRetentionMoneyManageInput> {

	RetentionMoneyReceiptService receiptService = new RetentionMoneyReceiptService();
	
	@Override
	public ApiRetentionMoneyManageResult Process(ApiRetentionMoneyManageInput input, MDataMap mRequestMap) {
		ApiRetentionMoneyManageResult result = new ApiRetentionMoneyManageResult();
		String userCode = UserFactory.INSTANCE.create().getUserCode();
		String date = DateUtil.getSysDateTimeString();
		try {
			// 根据small_seller_code查询商户是否存在
			Map<String, Object> map = DbUp.upTable("oc_seller_retention_money").dataSqlOne(
					"select * from oc_seller_retention_money where small_seller_code=:small_seller_code",
					new MDataMap("small_seller_code", input.getSmallSellerCode()));
			if (map != null) {
				// 已扣质保金
				Double deduct_retention_money = Double.valueOf(map.get("deduct_retention_money").toString());
				// 预收质保金
				Double receive_retention_money = Double.valueOf(map.get("receive_retention_money").toString());
				// 调整质保金
				Double adjust_retention_money = Double.valueOf(map.get("adjust_retention_money").toString());
				MDataMap log = new MDataMap();
				log.put("small_seller_code", input.getSmallSellerCode());
				log.put("small_seller_name", map.get("small_seller_name").toString());
				log.put("operate_type", String.valueOf(input.getType()));
				log.put("remark", input.getRemark() != null ? input.getRemark() : "");
				log.put("create_time", date);
				log.put("creator", userCode);

				MDataMap update = new MDataMap();
				update.put("small_seller_code", input.getSmallSellerCode());
				update.put("update_time", date);
				update.put("updator", userCode);

				/**
				 * 根据类型判断是否修改预收还是调整
				 */
				if (input.getType() == 0) {
					receive_retention_money = receive_retention_money
							+ Double.valueOf(input.getReceiveRetentionMoney());
					Double retention_money_sum = deduct_retention_money + receive_retention_money
							+ adjust_retention_money;
					update.put("receive_retention_money", String.valueOf(receive_retention_money));
					update.put("receive_retention_money_date", input.getReceiveRetentionMoneyDate());
					update.put("retention_money_sum", String.valueOf(retention_money_sum));
					DbUp.upTable("oc_seller_retention_money").dataUpdate(update,
							"receive_retention_money,receive_retention_money_date,retention_money_sum,updator,update_time",
							"small_seller_code");
					log.put("retention_money", input.getReceiveRetentionMoney());
					log.put("operate_date", input.getReceiveRetentionMoneyDate());
					
					receiptService.addReceiptFromManager(input.getSmallSellerCode(), input.getReceiveRetentionMoney());
				} else {
					adjust_retention_money = adjust_retention_money + Double.valueOf(input.getAdjustRetentionMoney());
					Double retention_money_sum = deduct_retention_money + receive_retention_money
							+ adjust_retention_money;
					update.put("adjust_retention_money", String.valueOf(adjust_retention_money));
					update.put("adjust_retention_money_date", input.getAdjustRetentionMoneyDate());
					update.put("retention_money_sum", String.valueOf(retention_money_sum));
					DbUp.upTable("oc_seller_retention_money").dataUpdate(update,
							"adjust_retention_money,adjust_retention_money_date,retention_money_sum,updator,update_time",
							"small_seller_code");
					/**
					 * 添加质保金记录
					 */
					MDataMap oc_bill_seller_retention_money = new MDataMap();
					oc_bill_seller_retention_money.put("settle_time", DateUtil.getSysDateTimeString());
					// 获取调整质保金之和
					String adjust_retention_money_sql = "select SUM(period_retention_money) as period_retention_money from ordercenter.oc_bill_seller_retention_money where small_seller_code=:small_seller_code and retention_money_type='4497480200010002'";
					Map<String, Object> adjust_retention_money_map = DbUp.upTable("oc_bill_seller_retention_money").dataSqlOne(
							adjust_retention_money_sql, new MDataMap("small_seller_code", input.getSmallSellerCode()));
					Double old_adjust_retention_money = Double.valueOf("0.00");
					if (adjust_retention_money_map != null
							&& adjust_retention_money_map.get("period_retention_money") != null) {
						old_adjust_retention_money = Double
								.valueOf(adjust_retention_money_map.get("period_retention_money").toString());
					}
					// 获取最大结算单编号，及本期质保金之和
					String period_retention_money_sql = "SELECT MAX(settle_code) as settle_code,sum(period_retention_money) as period_retention_money FROM (SELECT DISTINCT m.settle_code,m.period_retention_money FROM ordercenter.oc_bill_seller_retention_money AS m,ordercenter.oc_bill_finance_amount AS a";
					period_retention_money_sql += " WHERE m.settle_code = a.settle_code AND m.small_seller_code =:small_seller_code AND a.settle_status = '2' AND m.retention_money_type = '4497480200010001') tmp";
					Map<String, Object> settle = DbUp.upTable("oc_bill_seller_retention_money").dataSqlOne(
							period_retention_money_sql, new MDataMap("small_seller_code", input.getSmallSellerCode()));
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					String settleCode = sdf.format(new Date());
					if (settle != null && settle.get("settle_code") != null
							&& settle.get("period_retention_money") != null) {
						Double period_retention_money = Double.valueOf(settle.get("period_retention_money").toString());
						period_retention_money = period_retention_money + old_adjust_retention_money;
						oc_bill_seller_retention_money.put("settle_code", settleCode);
						oc_bill_seller_retention_money.put("small_seller_code", input.getSmallSellerCode());
						oc_bill_seller_retention_money.put("max_retention_money",
								map.get("max_retention_money").toString());
						oc_bill_seller_retention_money.put("deduct_retention_money",
								settle.get("period_retention_money").toString());
						oc_bill_seller_retention_money.put("period_retention_money",
								String.valueOf(Double.valueOf(input.getAdjustRetentionMoney())));
						oc_bill_seller_retention_money.put("retention_money_type", "4497480200010002");
						DbUp.upTable("oc_bill_seller_retention_money").dataInsert(oc_bill_seller_retention_money);
						/**
						 * 添加数据到商户结算质保金汇总表中
						 */
						MDataMap oc_retention_money_merchant = new MDataMap();
						oc_retention_money_merchant.put("small_seller_code", input.getSmallSellerCode());
						Map<String, Object> seller = DbUp.upTable("uc_sellerinfo").dataSqlOne(
								"select seller_name from usercenter.uc_sellerinfo where small_seller_code=:small_seller_code",
								new MDataMap("small_seller_code", input.getSmallSellerCode()));
						oc_retention_money_merchant.put("small_seller_name", seller.get("seller_name").toString());
						oc_retention_money_merchant.put("settle_code", settleCode);
						oc_retention_money_merchant.put("settle_type", settleType(input.getSmallSellerCode()));
						oc_retention_money_merchant.put("account_type", accountType(input.getSmallSellerCode()));
						oc_retention_money_merchant.put("retention_money_type", "4497480200010002");
						oc_retention_money_merchant.put("max_retention_money",
								map.get("max_retention_money").toString());
						oc_retention_money_merchant.put("deduct_retention_money",
								String.valueOf(period_retention_money));
						oc_retention_money_merchant.put("period_retention_money",
								String.valueOf(Double.valueOf(input.getAdjustRetentionMoney())));
						DbUp.upTable("oc_retention_money_merchant").dataInsert(oc_retention_money_merchant);
					} else {
						oc_bill_seller_retention_money.put("settle_code", settleCode);
						oc_bill_seller_retention_money.put("small_seller_code", input.getSmallSellerCode());
						oc_bill_seller_retention_money.put("max_retention_money",
								map.get("max_retention_money").toString());
						oc_bill_seller_retention_money.put("deduct_retention_money", "0.00");
						oc_bill_seller_retention_money.put("period_retention_money",
								String.valueOf(Double.valueOf(input.getAdjustRetentionMoney())));
						oc_bill_seller_retention_money.put("retention_money_type", "4497480200010002");
						DbUp.upTable("oc_bill_seller_retention_money").dataInsert(oc_bill_seller_retention_money);

						/**
						 * 添加数据到商户结算质保金汇总表中
						 */
						MDataMap oc_retention_money_merchant = new MDataMap();
						oc_retention_money_merchant.put("small_seller_code", input.getSmallSellerCode());
						Map<String, Object> seller = DbUp.upTable("uc_sellerinfo").dataSqlOne(
								"select seller_name from usercenter.uc_sellerinfo where small_seller_code=:small_seller_code",
								new MDataMap("small_seller_code", input.getSmallSellerCode()));
						oc_retention_money_merchant.put("small_seller_name", seller.get("seller_name").toString());
						oc_retention_money_merchant.put("settle_code", settleCode);
						oc_retention_money_merchant.put("settle_type", settleType(input.getSmallSellerCode()));
						oc_retention_money_merchant.put("account_type", accountType(input.getSmallSellerCode()));
						oc_retention_money_merchant.put("retention_money_type", "4497480200010002");
						oc_retention_money_merchant.put("max_retention_money",
								map.get("max_retention_money").toString());
						oc_retention_money_merchant.put("deduct_retention_money",
								String.valueOf(old_adjust_retention_money));
						oc_retention_money_merchant.put("period_retention_money",
								String.valueOf(Double.valueOf(input.getAdjustRetentionMoney())));
						DbUp.upTable("oc_retention_money_merchant").dataInsert(oc_retention_money_merchant);
					}
					log.put("retention_money", input.getAdjustRetentionMoney());
					log.put("operate_date", input.getAdjustRetentionMoneyDate());
				}
				/**
				 * 记录日志
				 */
				DbUp.upTable("lc_retention_money").dataInsert(log);
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
