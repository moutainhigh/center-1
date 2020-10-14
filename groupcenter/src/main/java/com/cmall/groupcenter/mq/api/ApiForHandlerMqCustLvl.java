package com.cmall.groupcenter.mq.api;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.cmall.groupcenter.mq.model.CustLvlListenModel;
import com.cmall.groupcenter.mq.model.api.ApiForHandlerMqCustLvlInput;
import com.cmall.groupcenter.mq.model.api.ApiForHandlerMqCustLvlResult;
import com.cmall.groupcenter.mq.service.MqCustService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

public class ApiForHandlerMqCustLvl extends RootApi<ApiForHandlerMqCustLvlResult, ApiForHandlerMqCustLvlInput> {

	@Override
	public ApiForHandlerMqCustLvlResult Process(ApiForHandlerMqCustLvlInput inputParam, MDataMap mRequestMap) {
		
		ApiForHandlerMqCustLvlResult result = new ApiForHandlerMqCustLvlResult();
		
		List<CustLvlListenModel> modelList = inputParam.getModeList();
		List<CustLvlListenModel> errList = new ArrayList<CustLvlListenModel>(); //存在错误的消息
		
		if(null != modelList && !modelList.isEmpty()) {
			MqCustService mqCustService = new MqCustService();
			for (CustLvlListenModel custLvl : modelList) {
				
				try {
					String lock_uid=WebHelper.addLock(1000*60, custLvl.getCust_id().toString());
					MWebResult mResult = mqCustService.reginRsyncCustLvl(custLvl);
					if(!mResult.upFlagTrue()) {
						custLvl.setMessage(mResult.getResultMessage());
						errList.add(custLvl);
					}
					WebHelper.unLock(lock_uid);
				} catch (Exception e) {
					e.printStackTrace();
					custLvl.setMessage(e.getMessage());
					errList.add(custLvl);
				}
			}
			
			if(!errList.isEmpty()) {
				result.setResultCode(0);
				result.setResultMessage(JSON.toJSONString(errList));
			}
		}
		return result;
	}

}
