package com.cmall.groupcenter.baidupush;

import com.cmall.groupcenter.baidupush.channel.auth.ChannelKeyPair;
import com.cmall.groupcenter.baidupush.channel.client.BaiduChannelClientSingle;
import com.cmall.groupcenter.baidupush.channel.exception.ChannelClientException;
import com.cmall.groupcenter.baidupush.channel.exception.ChannelServerException;
import com.cmall.groupcenter.baidupush.channel.model.PushBroadcastMessageRequestSingle;
import com.cmall.groupcenter.baidupush.channel.model.PushUnicastMessageResponse;
import com.cmall.groupcenter.baidupush.core.utility.StringUtility;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 *@author dyc 
 *推送广播消息、通知
 * */
public class AndroidPushMessageToSingleUser {

	/**
	 * 发送通知信息
	 * @param {title:xxx(必填),content:xxx(必填), params:xxx(必填),apiKey:xxx(必填),secretKey:xxx(必填),userId:xxx(可填),channelId:xxx(可填)}
	 * */
    public MWebResult sendNotifyMsg(MDataMap map) {

    	MWebResult result = new MWebResult(); 
        // 1. 设置developer平台的ApiKey/SecretKey
        ChannelKeyPair pair = new ChannelKeyPair(map.get("apiKey"), map.get("secretKey"));

        // 2. 创建BaiduChannelClient对象实例
        BaiduChannelClientSingle channelClient = new BaiduChannelClientSingle(pair);

        try {

            // 3. 创建请求类对象
        	PushBroadcastMessageRequestSingle request = new PushBroadcastMessageRequestSingle();
            
            request.setDeviceType(3); // device_type => 1: web 2: pc 3:android 4:ios 5:wp
            //通知，
            request.setMessageType(1);
            request.setMessage("{\"title\":\""+map.get("title")+"\",\"description\":\""+map.get("content")+"\",\"custom_content\": {"+map.get("params")+"}}");
            if(StringUtility.isNotNull(map.get("userId"))){//指定用户的所有设备
            	request.setUserId(map.get("userId"));
            }
            if(StringUtility.isNotNull(map.get("channelId"))){//指定用户的指定设备
            	request.setChannelId(Long.parseLong(map.get("channelId")));
            }
            // 4. 调用pushMessage接口
            PushUnicastMessageResponse response = channelClient.pushUnicastMessage(request);
            
            // 5. 认证推送成功
            //System.out.println("Android success push amount : " + response.getSuccessAmount());
        } catch (ChannelClientException e) {
            // 处理客户端错误异常
            e.printStackTrace();
            result.inErrorMessage(918501006);
        } catch (ChannelServerException e) {
            // 处理服务端错误异常
        	e.printStackTrace();
        	result.inErrorMessage(918501007,String.format("request_id: %d, error_code: %d, error_message: %s",e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
        }
        
        return result;
    }
    
}
