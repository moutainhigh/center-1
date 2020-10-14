package com.cmall.ordercenter.service.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cmall.ordercenter.alipay.util.MD5Util;
import com.cmall.ordercenter.model.api.ApiOrderShipmentsNoticInput;
import com.cmall.ordercenter.model.api.ApiOrderShipmentsNoticResult;
import com.cmall.ordercenter.util.AESCipher;
import com.cmall.systemcenter.dcb.RecordInterfaceLogForDcb;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapcom.topapi.RootApi;

/**
 * 订单发货通知(用于多彩)
 * @author renhongbin
 */
public class ApiOrderShipmentsNotic extends RootApi<ApiOrderShipmentsNoticResult, ApiOrderShipmentsNoticInput>{

	public ApiOrderShipmentsNoticResult Process(ApiOrderShipmentsNoticInput inputParam, MDataMap mRequestMap) {

		Date request_time = new Date();
		
		ApiOrderShipmentsNoticResult result = new ApiOrderShipmentsNoticResult();
		MDataMap requestParams = new MDataMap();
		Map<String,Object> sendMap = new LinkedHashMap<>();
		String reqResult = "";
		String exception = "";
		
		try {
			MDataMap mDataMap = new MDataMap();
			mDataMap.put("jyOrderCode", inputParam.getJyOrderCode());
			mDataMap.put("logisticseName", inputParam.getLogisticseName());
			mDataMap.put("logisticseCode", inputParam.getLogisticseCode());
			mDataMap.put("waybill", inputParam.getWaybill());
			mDataMap.put("deliveryTime", inputParam.getDeliveryTime());
			
			//排序
	        Collection<String> keyset = mDataMap.keySet();
	        List<String> list = new ArrayList<String>(keyset);
	        Collections.sort(list);
	        for (String str : list){
	        	sendMap.put(str,mDataMap.get(str));
	        }
	        //签名字符串
	        String md5Str = MD5Util.MD5Encode(JSONObject.toJSONString(sendMap), null);
	        //加密字符串
	        String encrypt = AESCipher.encryptAES(JSONObject.toJSONString(mDataMap),AESCipher.key);
	        
	        requestParams.put("signature",md5Str);
	        requestParams.put("inputstring",encrypt);
	        
			String url = bConfig("ordercenter.dcb_interface_url") + "online-shop/_3rd/huijiayou/delivery_shop";

			MDataMap headMap = new MDataMap();
			headMap.put("Content-Type", "application/json");
			
			reqResult = WebClientSupport.upPostToDC(url, requestParams, headMap);
			
			if(null != reqResult && !"".equals(reqResult)){
				JSONObject jsonObject = JSON.parseObject(reqResult);
				if(jsonObject.containsKey("success")){
					boolean success = JSON.parseObject(reqResult).getBoolean("success");
					result.setSuccess(success);
				}
			}else{
				result.setResultCode(916401109);
				result.setResultMessage(bInfo(916401109));
			}
		} catch (Exception e) {
			e.printStackTrace();
			exception = ExceptionUtils.getStackTrace(e);
			result.setResultCode(916401109);
			result.setResultMessage(bInfo(916401109));
		}
		
		//记录日志
		RecordInterfaceLogForDcb recordInterfaceLogForDcb = new RecordInterfaceLogForDcb();
		recordInterfaceLogForDcb.insertLogTable(this.getClass().getName(), request_time, new Date(), 
				JSON.toJSONString(sendMap), JSON.toJSONString(requestParams), reqResult, "push", exception);
		
		return result;
	}

}
