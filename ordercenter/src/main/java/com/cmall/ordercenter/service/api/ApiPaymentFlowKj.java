package com.cmall.ordercenter.service.api;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.model.api.ApiPaymentFlowKjInput;
import com.cmall.ordercenter.model.api.ApiPaymentFlowKjResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebSessionHelper;
import com.srnpr.zapweb.webfactory.UserFactory;

/**
 * 
 * 类: ApiPaymentFlow <br>
 * 描述: 跨境付款申请审批流程状态修改接口 <br>
 * 作者: zhy<br>
 * 时间: 2017年5月23日 下午2:04:16
 */
public class ApiPaymentFlowKj extends RootApi<ApiPaymentFlowKjResult, ApiPaymentFlowKjInput> {

	/**
	 * 
	 * 方法: Process <br>
	 * 4497477900060001 商品行政待审核<br>
	 * 4497477900060002 商品行政审核通过<br>
	 * 4497477900060003 商品行政驳回<br>
	 * 4497477900060004 财务审核通过<br>
	 * 4497477900060005 财务驳回<br>
	 * 4497477900060006 财务已确认<br>
	 * 4497477900060007 财务已付款<br>
	 * 4497477900060008 财务反审核<br>
	 * 
	 * @param inputParam
	 * @param mRequestMap
	 * @return
	 * @see com.srnpr.zapcom.baseface.IBaseApi#Process(com.srnpr.zapcom.baseface.IBaseInput,
	 *      com.srnpr.zapcom.basemodel.MDataMap)
	 */
	@Override
	public ApiPaymentFlowKjResult Process(ApiPaymentFlowKjInput inputParam, MDataMap mRequestMap) {
		ApiPaymentFlowKjResult result = new ApiPaymentFlowKjResult();
		String comment = "";// 审批日志备注
		try {
			Map<String, Object> payment = DbUp.upTable("oc_bill_apply_payment_kj").dataSqlOne(
					"select * from oc_bill_apply_payment_kj where pay_code=:pay_code",
					new MDataMap("pay_code", inputParam.getPayCode()));
			if (payment != null) {
				if (StringUtils.equals("4497477900060001", inputParam.getFlag())) {
					comment = "商品行政待审核";
				} else if (StringUtils.equals("4497477900060002", inputParam.getFlag())) {
					comment = "商品行政审核通过";
				} else if (StringUtils.equals("4497477900060003", inputParam.getFlag())) {
					comment = "商品行政驳回";
				} else if (StringUtils.equals("4497477900060004", inputParam.getFlag())) {
					comment = "财务审核通过";
				} else if (StringUtils.equals("4497477900060005", inputParam.getFlag())) {
					comment = "财务驳回";
				} else if (StringUtils.equals("4497477900060006", inputParam.getFlag())) {
					comment = "财务已确认";
					/**
					 * 修改结算单状态为已审核
					 */
					MDataMap spec = new MDataMap();
					spec.put("settle_code", payment.get("settle_codes").toString());
					spec.put("merchant_code", payment.get("merchant_code").toString());
					spec.put("flag", "4497476900040011");
					DbUp.upTable("oc_bill_merchant_new_spec").dataUpdate(spec, "flag", "settle_code,merchant_code");

				} else if (StringUtils.equals("4497477900060007", inputParam.getFlag())) {
					comment = "财务已付款";
					/**
					 * 修改结算单状态为已审核
					 */
					MDataMap spec = new MDataMap();
					spec.put("settle_code", payment.get("settle_codes").toString());
					spec.put("merchant_code", payment.get("merchant_code").toString());
					spec.put("flag", "4497476900040009");
					DbUp.upTable("oc_bill_merchant_new_spec").dataUpdate(spec, "flag", "settle_code,merchant_code");
				} else if (StringUtils.equals("4497477900060008", inputParam.getFlag())) {
					comment = "财务反审核";
				}
				/**
				 * 确认付款申请单
				 */
				MDataMap update = new MDataMap();
				update.put("pay_code", inputParam.getPayCode());
				update.put("flag", inputParam.getFlag());
				update.put("comment", inputParam.getComment());
				DbUp.upTable("oc_bill_apply_payment_kj").dataUpdate(update, "flag,comment", "pay_code");
				/**
				 * 添加日志
				 */
				String ip = WebSessionHelper.create().upIpaddress();
				MDataMap logMap = new MDataMap();
				logMap.put("pay_code", inputParam.getPayCode());
				logMap.put("flag", inputParam.getFlag());
				logMap.put("ip", ip);
				if (StringUtils.isNotBlank(inputParam.getComment())) {
					comment = comment + "," + inputParam.getComment();
				}
				logMap.put("comment", comment);
				logMap.put("create_time", DateUtil.getSysDateTimeString());
				logMap.put("creator", UserFactory.INSTANCE.create().getLoginName());
				DbUp.upTable("lc_apply_for_payment").dataInsert(logMap);
				result.setResultCode(1);
				result.setResultMessage("操作成功");
			} else {
				result.setResultCode(-1);
				result.setResultMessage("付款申请单不存在");
			}
		} catch (Exception e) {
			result.setResultCode(-1);
			result.setResultMessage("你的网络被高智慧生物屏蔽了，稍后再试试");
		}
		return result;
	}

}
