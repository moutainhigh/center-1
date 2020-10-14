package com.cmall.newscenter.api;


import com.cmall.newscenter.model.Torder;
import com.cmall.newscenter.model.TrialOrderList;
import com.cmall.newscenter.model.TryOrderServiceApiResult;
import com.cmall.newscenter.model.TryOrderTrialApiInput;
import com.cmall.newscenter.service.TxTryOrderService;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 试用商品处理逻辑
 * @author shiyz
 *
 */
public class TryOrderServiceApi extends RootApiForToken<TryOrderServiceApiResult,TryOrderTrialApiInput> {

	public TryOrderServiceApiResult Process(TryOrderTrialApiInput inputParam, MDataMap mRequestMap) {
		
		TryOrderServiceApiResult result = new TryOrderServiceApiResult();
		
		
			//订单处理
			TxTryOrderService tryOrderService = BeansHelper.upBean("bean_com_cmall_newscenter_service_TxTryOrderService");
			
			Torder torder = tryOrderService.taddOrder(getUserCode(), inputParam.getProduct(), inputParam.getAddress(), inputParam.getAmount(), getManageCode(), result,inputParam.getAreaCode());
			
			if(torder!=null){
			
				TrialOrderList trial_orderList =new TrialOrderList();
				
				trial_orderList.setCreate_time(torder.getCreate_time());
				
				trial_orderList.setId(torder.getId());
				
				trial_orderList.setOrder_description(torder.getOrder_description());
				
				trial_orderList.setOrder_id(torder.getOrder_id());
				
				trial_orderList.setState(0);
				
				trial_orderList.setTotal(torder.getTotal());
				
				trial_orderList.setProducts(torder.getProducts());
				
				result.setTRIAL_ORDER(trial_orderList);
				
			}else{
			
			result.setResultCode(result.getResultCode());
			result.setResultMessage(result.getResultMessage());
			}
		return result;
	}

	
	
}
