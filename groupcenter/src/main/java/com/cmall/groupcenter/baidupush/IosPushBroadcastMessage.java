package com.cmall.groupcenter.baidupush;

import com.cmall.groupcenter.baidupush.channel.auth.ChannelKeyPair;
import com.cmall.groupcenter.baidupush.channel.client.BaiduChannelClient;
import com.cmall.groupcenter.baidupush.channel.exception.ChannelClientException;
import com.cmall.groupcenter.baidupush.channel.exception.ChannelServerException;
import com.cmall.groupcenter.baidupush.channel.model.PushBroadcastMessageRequest;
import com.cmall.groupcenter.baidupush.channel.model.PushBroadcastMessageResponse;
import com.cmall.groupcenter.baidupush.core.log.YunLogEvent;
import com.cmall.groupcenter.baidupush.core.log.YunLogHandler;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 *@author dyc 
 *推送广播消息、通知
 * */
public class IosPushBroadcastMessage {

	/**
	 * 发送通知信息
	 * */
    public MWebResult sendNotifyMsg(String title,String comment,String params,String apiKey,String secretKey,int deployStatus) {

    	MWebResult result = new MWebResult(); 
        // 1. 设置developer平台的ApiKey/SecretKey
        ChannelKeyPair pair = new ChannelKeyPair(apiKey, secretKey);

        // 2. 创建BaiduChannelClient对象实例
        BaiduChannelClient channelClient = new BaiduChannelClient(pair);

        try {

            // 4. 创建请求类对象
            PushBroadcastMessageRequest request = new PushBroadcastMessageRequest();
            request.setDeviceType(4); // device_type => 1: web 2: pc 3:android 4:ios 5:wp
            //通知，
            request.setMessageType(1);
            request.setDeployStatus(deployStatus); // DeployStatus => 1: Developer 2: Production
            request.setMessage("{\"aps\":{\"alert\":\""+comment+"\"},"+params+"}");
            // 5. 调用pushMessage接口
            PushBroadcastMessageResponse response = channelClient.pushBroadcastMessage(request);

            // 6. 认证推送成功
          //  System.out.println("IOS success push amount : " + response.getSuccessAmount());
        } catch (ChannelClientException e) {
            // 处理客户端错误异常
            e.printStackTrace();
            result.inErrorMessage(918501008);
        } catch (ChannelServerException e) {
            // 处理服务端错误异常
        	e.printStackTrace();
        	result.inErrorMessage(918501009,String.format("request_id: %d, error_code: %d, error_message: %s",e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
        }
        
        return result;
    }

}
