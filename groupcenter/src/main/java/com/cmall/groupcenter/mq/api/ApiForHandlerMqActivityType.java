package com.cmall.groupcenter.mq.api;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.cmall.groupcenter.mq.model.ActivityTypeListenModel;
import com.cmall.groupcenter.mq.model.api.ApiForHandlerMqActivityTypeInput;
import com.cmall.groupcenter.mq.model.api.ApiForHandlerMqActivityTypeResult;
import com.cmall.groupcenter.mq.service.MqActivityService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

public class ApiForHandlerMqActivityType extends RootApi<ApiForHandlerMqActivityTypeResult, ApiForHandlerMqActivityTypeInput> {

	@Override
	public ApiForHandlerMqActivityTypeResult Process(ApiForHandlerMqActivityTypeInput inputParam, MDataMap mRequestMap) {
		
		ApiForHandlerMqActivityTypeResult result = new ApiForHandlerMqActivityTypeResult();
		
		List<ActivityTypeListenModel> modelList = inputParam.getModeList();
		List<ActivityTypeListenModel> errList = new ArrayList<ActivityTypeListenModel>(); //存在错误的消息
		
		if(null != modelList && !modelList.isEmpty()) {
			MqActivityService mqActivityService = new MqActivityService();
			for (ActivityTypeListenModel activity : modelList) {
				
				try {
					String lock_uid=WebHelper.addLock(1000*60, activity.getEvent_id().toString());
					MWebResult mResult = mqActivityService.reginRsyncActivityType(activity);
					if(!mResult.upFlagTrue()) {
						activity.setMessage(mResult.getResultMessage());
						errList.add(activity);
					}
					WebHelper.unLock(lock_uid);
				} catch (Exception e) {
					e.printStackTrace();
					activity.setMessage(e.getMessage());
					errList.add(activity);
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
