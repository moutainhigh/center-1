package com.cmall.groupcenter.mq.custormers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.cmall.groupcenter.mq.model.ActivityListenModel;
import com.cmall.groupcenter.mq.model.QueueName;
import com.cmall.groupcenter.mq.service.MqActivityService;
import com.cmall.groupcenter.mq.service.MqService;
import com.cmall.ordercenter.common.DateUtil;
import com.rabbitmq.client.Channel;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 
 * @remark 活动信息监听
 * @author 任宏斌
 * @date 2018年9月14日
 * 
 * @modify  废弃此消费者 改走接口
 * @author 任宏斌
 * @date 2020年05月15日
 */
@Deprecated
public class ActivityCustormer implements ChannelAwareMessageListener{

	@Override
	public void onMessage(Message message, Channel channel) {
		
		try {
			List<ActivityListenModel> modelList = JSONArray.parseArray(new String(message.getBody()), ActivityListenModel.class);
			List<ActivityListenModel> errList = new ArrayList<ActivityListenModel>(); //存在错误的消息
			if(null != modelList && modelList.size() > 0) {
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
				
				//记录日志
				MDataMap mDataMap = new MDataMap();
				mDataMap.put("queue_code", QueueName.activityQueueName);
				mDataMap.put("message", JSON.toJSONString(modelList));
				mDataMap.put("err_message", JSON.toJSONString(errList));
				mDataMap.put("create_time", DateUtil.getSysDateTimeString());
				DbUp.upTable("lc_mq_error_log").dataInsert(mDataMap);
				
				if(!errList.isEmpty() && StringUtils.isNotEmpty(TopUp.upConfig("groupcenter.mq_exception_email_addr"))) {
					//发邮件
					String title = "LD向惠家有同步优惠券活动时存在异常记录";
					String content = "异常记录:\r\n<br/>" + JSON.toJSONString(errList);
					MailSupport mailSupport = new MailSupport();
					mailSupport.sendMail(TopUp.upConfig("groupcenter.mq_exception_email_addr"), title, content);
				}
			}
			
			new MqService().ack(channel, message);
		} catch (Exception e) {
			e.printStackTrace();
			new MqService().ack(channel, message);
			MDataMap mDataMap = new MDataMap();
			mDataMap.put("queue_code", QueueName.activityQueueName);
			mDataMap.put("message", new String(message.getBody()));
			mDataMap.put("exception_message", e.getMessage());
			mDataMap.put("create_time", DateUtil.getSysDateTimeString());
			DbUp.upTable("lc_mq_error_log").dataInsert(mDataMap);
			
			//发邮件
			String title = "LD向惠家有同步优惠券活动时存在异常";
			String content = "异常记录:\r\n<br/>" + JSON.toJSONString(e.getMessage());
			MailSupport mailSupport = new MailSupport();
			mailSupport.sendMail(TopUp.upConfig("groupcenter.mq_exception_email_addr"), title, content);
		}
		
	}

}
