package com.cmall.groupcenter.mq.api;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.cmall.groupcenter.mq.model.GiftVoucherDetailListenModel;
import com.cmall.groupcenter.mq.model.api.ApiForHandlerMqGifvoucherInput;
import com.cmall.groupcenter.mq.model.api.ApiForHandlerMqGifvoucherResult;
import com.cmall.groupcenter.mq.service.GiftVoucherService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

public class ApiForHandlerMqGifvoucher extends RootApi<ApiForHandlerMqGifvoucherResult, ApiForHandlerMqGifvoucherInput>{

	@Override
	public ApiForHandlerMqGifvoucherResult Process(ApiForHandlerMqGifvoucherInput inputParam, MDataMap mRequestMap) {
		
		ApiForHandlerMqGifvoucherResult result = new ApiForHandlerMqGifvoucherResult();
		
		List<GiftVoucherDetailListenModel> modelList = inputParam.getModeList();
		List<GiftVoucherDetailListenModel> errList = new ArrayList<GiftVoucherDetailListenModel>(); //存在错误的消息
		
		if(null != modelList && modelList.size() > 0) {
			GiftVoucherService giftVoucherService = new GiftVoucherService();
			for (GiftVoucherDetailListenModel detail : modelList) {
				
				try {
					String lock_uid=WebHelper.addLock(1000*60, detail.getLj_code().toString());
					MWebResult mResult = giftVoucherService.reginRsyncGiftVoucherDetail(detail);
					if(!mResult.upFlagTrue()) {
						detail.setMessage(mResult.getResultMessage());
						errList.add(detail);
					}
					WebHelper.unLock(lock_uid);
				} catch (Exception e) {
					e.printStackTrace();
					detail.setMessage(e.getMessage());
					errList.add(detail);
				}
			}
			
			//记录日志
			if(!errList.isEmpty()) {
				result.setResultCode(0);
				result.setResultMessage(JSON.toJSONString(errList));
			}
		}
		
		return result;
	}

}
