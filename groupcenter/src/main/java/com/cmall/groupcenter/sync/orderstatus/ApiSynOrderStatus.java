package com.cmall.groupcenter.sync.orderstatus;


import com.cmall.groupcenter.service.SyncOrderStatusService;
import com.cmall.groupcenter.sync.orderstatus.model.SyncOrderStatusInput;
import com.cmall.groupcenter.sync.orderstatus.model.SyncOrderStatusResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 微公社对接系统通过该接口同步订单最新状态api
 * 
 * @author chenxk
 *
 */
public class ApiSynOrderStatus extends
    RootApiForManage<SyncOrderStatusResult, SyncOrderStatusInput> {

	public SyncOrderStatusResult Process(SyncOrderStatusInput inputParam, MDataMap mRequestMap) {
		
		return new SyncOrderStatusService().saveOrderStatusInfo(inputParam, this.getManageCode());
		
	}


}
