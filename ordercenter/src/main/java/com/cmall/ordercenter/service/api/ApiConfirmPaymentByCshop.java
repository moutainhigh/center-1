package com.cmall.ordercenter.service.api;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.model.api.ApiConfirmPaymentByCshopInput;
import com.cmall.ordercenter.model.api.ApiConfirmPaymentByCshopResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.helper.WebSessionHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webfactory.UserFactory;

/**
 * 
 * 类: ApiConfirmPaymentKj <br>
 * 描述: 跨境付款-确认付款申请单 <br>
 * 作者: zhy<br>
 * 时间: 2017年5月15日 上午11:16:47
 */
public class ApiConfirmPaymentByCshop extends RootApi<ApiConfirmPaymentByCshopResult, ApiConfirmPaymentByCshopInput> {

	@Override
	public ApiConfirmPaymentByCshopResult Process(ApiConfirmPaymentByCshopInput inputParam, MDataMap mRequestMap) {
		ApiConfirmPaymentByCshopResult result = new ApiConfirmPaymentByCshopResult();
		String settleCode = inputParam.getSettleCode();
		MUserInfo user = UserFactory.INSTANCE.create();
		if (StringUtils.isNotBlank(settleCode)) {
			String merchantCode = user.getManageCode();
			//判断是否已生成结算单
			int existsflag = 0;
			if (StringUtils.equals("4497477900040004", inputParam.getSettleType())) {
				existsflag = DbUp.upTable("oc_bill_apply_payment_pt").dataCount("settle_codes='"+settleCode+"' and merchant_code='"+merchantCode+"'", new MDataMap());
			} else {
				existsflag = DbUp.upTable("oc_bill_apply_payment_kj").dataCount("settle_codes='"+settleCode+"' and merchant_code='"+merchantCode+"'", new MDataMap());
			}
			if(existsflag > 0){
				result.setResultCode(-1);
				result.setResultMessage("付款申请单已生成,请勿重复确认!");
				return result;
			}
			// 根据结算单编号查询结算信息
			Map<String, Object> spec = DbUp.upTable("oc_bill_merchant_new_spec").dataSqlOne(
					"select * from oc_bill_merchant_new_spec where merchant_code=:merchant_code and settle_code=:settle_code",
					new MDataMap("merchant_code", merchantCode, "settle_code", settleCode));
			Map<String, Object> seller = DbUp.upTable("uc_seller_info_extend").dataSqlOne(
					"SELECT us.small_seller_code,us.seller_name,ue.branch_name,ue.bank_account,ue.joint_number,ue.branch_address FROM usercenter.uc_sellerinfo as us,usercenter.uc_seller_info_extend as ue WHERE us.small_seller_code = ue.small_seller_code and us.small_seller_code=:small_seller_code",
					new MDataMap("small_seller_code", merchantCode));
			// 添加信息到付款申请单表
			if (spec != null) {
				String payCode = WebHelper.upCode("FK");
				MDataMap payment = new MDataMap();
				// 付款申请单编号
				payment.put("pay_code", payCode);
				// 商户编码
				payment.put("merchant_code", merchantCode);
				// 商户名称
				payment.put("merchant_name", seller.get("seller_name").toString());
				// 结算单编号
				payment.put("settle_codes", settleCode);
				// 结算类型
				payment.put("settle_type", inputParam.getSettleType());
				// 结算周期
				payment.put("account_type", spec.get("account_type").toString());
				// 本期代收货款合计
				payment.put("period_collect_amount_total", spec.get("income_amount").toString());
				Double period_collect_amount_total = Double.valueOf(spec.get("income_amount").toString());
				// 平台服务费
				payment.put("service_fee", spec.get("service_fee").toString());
				Double service_fee = Double.valueOf(spec.get("service_fee").toString());
				// 应付代收货款
				// 应付代收货款=本期代收货款合计-平台服务费
				Double payable_collect_amount = period_collect_amount_total - service_fee;
				payment.put("payable_collect_amount",
						BigDecimal.valueOf(payable_collect_amount).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
				// 附加扣费合计
				payment.put("add_deduction", spec.get("add_deduction").toString());
				Double add_deduction = Double.valueOf(spec.get("add_deduction").toString());
				// 结算代收货款
				// 结算代收货款=应付代收货款-附加扣费合计
				Double settle_collect_amount = payable_collect_amount - add_deduction;
				payment.put("settle_collect_amount",
						BigDecimal.valueOf(settle_collect_amount).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
				// 本期质保金
				payment.put("period_money", spec.get("period_money").toString());
				Double period_money = Double.valueOf(spec.get("period_money").toString());
				// 实付代收货款
				// 实付代收货款=结算代收货款-本期质保金
				Double actual_pay_amount = settle_collect_amount - period_money;
				payment.put("actual_pay_amount",
						BigDecimal.valueOf(actual_pay_amount).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
				// 商户确认时间
				payment.put("confirm_datetime", DateUtil.getSysDateTimeString());
				// 是否有付款日期
				payment.put("is_pay", "4497477900020002");
				// 状态
				payment.put("flag", "4497477900060001");
				// 开户行支行名称
				payment.put("branch_name", seller.get("branch_name").toString());
				// 银行账号
				payment.put("bank_account", seller.get("bank_account").toString());
				// 开户行支行联行号
				payment.put("joint_number", seller.get("joint_number").toString());
				// 开户支行所在省市
				payment.put("branch_address", seller.get("branch_address").toString());
				// 创建时间
				payment.put("create_time", DateUtil.getSysDateTimeString());
				// 创建人
				payment.put("creator", user.getUserCode());
				// 更新时间
				payment.put("update_time", DateUtil.getSysDateTimeString());
				// 更新人
				payment.put("updator", user.getUserCode());

				/**
				 * 根据结算类型存储不同的数据表<br>
				 * 4497477900040004:平台入驻<br>
				 * 4497477900040002:跨境保税,4497477900040003:跨境直邮<br>
				 */
				try {
					String flag = "";
					if (StringUtils.equals("4497477900040004", inputParam.getSettleType())) {
						flag = DbUp.upTable("oc_bill_apply_payment_pt").dataInsert(payment);
					} else {
						flag = DbUp.upTable("oc_bill_apply_payment_kj").dataInsert(payment);
					}
					if (StringUtils.isNotBlank(flag)) {
						MDataMap updateSpec = new MDataMap();
						updateSpec.put("settle_code", settleCode);
						updateSpec.put("flag", "4497476900040010");
						updateSpec.put("merchant_code", merchantCode);
						DbUp.upTable("oc_bill_merchant_new_spec").dataUpdate(updateSpec, "flag", "settle_code,merchant_code");
					}
					/**
					 * 添加日志
					 */
					MDataMap logMap = new MDataMap();
					logMap.put("pay_code", payCode);
					logMap.put("flag", "4497477900060001");
					logMap.put("ip", WebSessionHelper.create().upIpaddress());
					logMap.put("comment", "生成付款申请单");
					logMap.put("create_time", DateUtil.getSysDateTimeString());
					logMap.put("creator", UserFactory.INSTANCE.create().getLoginName());
					DbUp.upTable("lc_apply_for_payment").dataInsert(logMap);
					result.setResultCode(1);
					result.setResultMessage("生成付款申请单成功");
				} catch (Exception e) {
					e.printStackTrace();
					result.setResultCode(-1);
					result.setResultMessage("生成付款申请单失败");
				}
			} else {
				result.setResultCode(-1);
				result.setResultMessage("查询结算单失败");
			}
		} else {
			result.setResultCode(-1);
			result.setResultMessage("结算单编号为空");
		}
		return result;
	}

}
