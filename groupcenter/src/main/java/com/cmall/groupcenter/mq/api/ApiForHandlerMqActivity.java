package com.cmall.groupcenter.mq.api;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.cmall.groupcenter.mq.model.ActivityListenModel;
import com.cmall.groupcenter.mq.model.api.ApiForHandlerMqActivityInput;
import com.cmall.groupcenter.mq.model.api.ApiForHandlerMqActivityResult;
import com.cmall.groupcenter.mq.service.MqActivityService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

public class ApiForHandlerMqActivity extends RootApi<ApiForHandlerMqActivityResult, ApiForHandlerMqActivityInput> {

	@Override
	public ApiForHandlerMqActivityResult Process(ApiForHandlerMqActivityInput inputParam, MDataMap mRequestMap) {
		
		ApiForHandlerMqActivityResult result = new ApiForHandlerMqActivityResult();
		
		List<ActivityListenModel> modelList = inputParam.getModeList();
		List<ActivityListenModel> errList = new ArrayList<ActivityListenModel>(); //存在错误的消息
		
		if(null != modelList && !modelList.isEmpty()) {
			MqActivityService mqActivityService = new MqActivityService();
			for (ActivityListenModel activity : modelList) {
				
				try {
					String lock_uid=WebHelper.addLock(1000*60, activity.getEvent_id().toString());
					MWebResult mResult = mqActivityService.reginRsyncActivity(activity);
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
