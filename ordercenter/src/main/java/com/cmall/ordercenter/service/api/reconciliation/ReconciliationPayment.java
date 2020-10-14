package com.cmall.ordercenter.service.api.reconciliation;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.model.api.reconciliation.input.ReconciliationPaymentInput;
import com.cmall.ordercenter.model.api.reconciliation.result.ReconciliationPaymentResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;

/**
 * 
 * 类: ReconciliationPayment <br>
 * 描述: 对账单支付确定接口 <br>
 * 作者: zhy<br>
 * 时间: 2017年4月1日 下午5:44:56
 */
public class ReconciliationPayment extends RootApi<ReconciliationPaymentResult, ReconciliationPaymentInput> {

	@Override
	public ReconciliationPaymentResult Process(ReconciliationPaymentInput inputParam, MDataMap mRequestMap) {
		ReconciliationPaymentResult result = new ReconciliationPaymentResult();
		try {
			String ids = inputParam.getIds();
			if(StringUtils.isNotBlank(ids)){
				String userName = UserFactory.INSTANCE.create().getLoginName();
				String dateTime = DateUtil.getSysDateTimeString();
				String[] idArray = ids.split(",");
				if(idArray.length>0){
					for (String id : idArray) {
						MDataMap update = new MDataMap();
						update.put("zid", id);
						Map<String, Object> data = DbUp.upTable("oc_payment_reconciliation_collect").dataSqlOne("select pay_type,reconciliation_time from oc_payment_reconciliation_collect where zid=:zid", update);
						if(data != null){
							update.put("pay_status", "4497479900020002");
							update.put("pay_time", dateTime);
							update.put("update_user", userName);
							update.put("update_time", dateTime);
							DbUp.upTable("oc_payment_reconciliation_collect").dataUpdate(update, "pay_status,pay_time,update_user,update_time", "zid");
							/**
							 * 修改对账状态为已对账
							 */
							MDataMap reconciliation = new MDataMap(data);
							reconciliation.put("reconciliation_status", "4497479900040002");
							reconciliation.put("update_user", userName);
							reconciliation.put("update_time", dateTime); 
							DbUp.upTable("oc_payment_reconciliation").dataUpdate(reconciliation, "reconciliation_status,update_user,update_time", "pay_type,reconciliation_time");
						}
					}
				}else{
					result.setResultCode(-1);
					result.setResultMessage("支付确定选择不能为空");					
				}
			}else{
				result.setResultCode(-1);
				result.setResultMessage("支付确定选择不能为空");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
