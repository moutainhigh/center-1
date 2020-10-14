package com.cmall.groupcenter.mq.custormers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.cmall.groupcenter.mq.model.CustLvlListenModel;
import com.cmall.groupcenter.mq.model.QueueName;
import com.cmall.groupcenter.mq.service.MqCustService;
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
 * @remark 客户等级监听
 * @author 任宏斌
 * @date 2019年10月21日
 * 
 * @modify  废弃此消费者 改走接口
 * @author 任宏斌
 * @date 2020年05月15日
 */
@Deprecated
public class CustLvlCustormer implements ChannelAwareMessageListener{

	@Override
	public void onMessage(Message message, Channel channel) {
		
		try {
			List<CustLvlListenModel> modelList = JSONArray.parseArray(new String(message.getBody()), CustLvlListenModel.class);
			List<CustLvlListenModel> errList = new ArrayList<CustLvlListenModel>(); //存在错误的消息
			if(null != modelList && modelList.size() > 0) {
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
				
				//记录日志
				MDataMap mDataMap = new MDataMap();
				mDataMap.put("queue_code", QueueName.custLvlQueueName);
				mDataMap.put("message", JSON.toJSONString(modelList));
				mDataMap.put("err_message", JSON.toJSONString(errList));
				mDataMap.put("create_time", DateUtil.getSysDateTimeString());
				DbUp.upTable("lc_mq_error_log").dataInsert(mDataMap);
				
				if(!errList.isEmpty() && StringUtils.isNotEmpty(TopUp.upConfig("groupcenter.mq_exception_email_addr"))) {
					//发邮件
					String title = "LD向惠家有同步优惠券活动类型时存在异常记录";
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
			mDataMap.put("queue_code", QueueName.custLvlQueueName);
			mDataMap.put("message", new String(message.getBody()));
			mDataMap.put("exception_message", e.getMessage());
			mDataMap.put("create_time", DateUtil.getSysDateTimeString());
			DbUp.upTable("lc_mq_error_log").dataInsert(mDataMap);
			
			//发邮件
			String title = "LD向惠家有同步优惠券活动类型时存在异常";
			String content = "异常记录:\r\n<br/>" + JSON.toJSONString(e.getMessage());
			MailSupport mailSupport = new MailSupport();
			mailSupport.sendMail(TopUp.upConfig("groupcenter.mq_exception_email_addr"), title, content);
		}
		
	}

}
