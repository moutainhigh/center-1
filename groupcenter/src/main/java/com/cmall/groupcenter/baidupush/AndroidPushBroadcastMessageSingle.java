package com.cmall.groupcenter.baidupush;

import com.cmall.groupcenter.baidupush.channel.auth.ChannelKeyPair;
import com.cmall.groupcenter.baidupush.channel.client.BaiduChannelClientSingle;
import com.cmall.groupcenter.baidupush.channel.exception.ChannelClientException;
import com.cmall.groupcenter.baidupush.channel.exception.ChannelServerException;
import com.cmall.groupcenter.baidupush.channel.model.PushBroadcastMessageRequestSingle;
import com.cmall.groupcenter.baidupush.channel.model.PushUnicastMessageResponse;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 *@author yangrong 
 *推送广播消息、通知
 * */
public class AndroidPushBroadcastMessageSingle {

	/**
	 * 发送通知信息
	 * */
    public MWebResult sendNotifyMsg(String title,String comment,String params,String apiKey,String secretKey,String userId) {

    	MWebResult result = new MWebResult(); 
        // 1. 设置developer平台的ApiKey/SecretKey
        ChannelKeyPair pair = new ChannelKeyPair(apiKey, secretKey);

        // 2. 创建BaiduChannelClient对象实例
        BaiduChannelClientSingle channelClient = new BaiduChannelClientSingle(pair);

        try {

            // 4. 创建请求类对象
        	PushBroadcastMessageRequestSingle request = new PushBroadcastMessageRequestSingle();
            
            request.setDeviceType(3); // device_type => 1: web 2: pc 3:android 4:ios 5:wp
            //通知，
            request.setMessageType(1);
            request.setMessage("{\"title\":\""+title+"\",\"description\":\""+comment+"\",\"custom_content\": {"+params+"}}");
            request.setUserId(userId);
            // 5. 调用pushMessage接口
            PushUnicastMessageResponse response = channelClient.pushUnicastMessage(request);
            
            // 6. 认证推送成功
           // System.out.println("Android success push amount : " + response.getSuccessAmount());
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
