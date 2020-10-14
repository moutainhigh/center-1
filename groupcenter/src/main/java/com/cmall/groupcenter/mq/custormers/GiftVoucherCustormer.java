package com.cmall.groupcenter.mq.custormers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.cmall.groupcenter.mq.model.GiftVoucherDetailListenModel;
import com.cmall.groupcenter.mq.model.QueueName;
import com.cmall.groupcenter.mq.service.GiftVoucherService;
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
 * @remark 礼金券信息监听
 * @author 任宏斌
 * @date 2018年9月14日
 * 
 * @modify  废弃此消费者 改走接口
 * @author 任宏斌
 * @date 2020年05月15日
 */
@Deprecated
public class GiftVoucherCustormer implements ChannelAwareMessageListener{

	@Override
	public void onMessage(Message message, Channel channel) throws Exception {
		try {
			List<GiftVoucherDetailListenModel> modelList = JSONArray.parseArray(new String(message.getBody()), GiftVoucherDetailListenModel.class);
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
				MDataMap mDataMap = new MDataMap();
				mDataMap.put("queue_code", QueueName.giftVoucherQueueName);
				mDataMap.put("message", JSON.toJSONString(modelList));
				mDataMap.put("err_message", JSON.toJSONString(errList));
				mDataMap.put("create_time", DateUtil.getSysDateTimeString());
				DbUp.upTable("lc_mq_error_log").dataInsert(mDataMap);
				
				if(!errList.isEmpty() && StringUtils.isNotEmpty(TopUp.upConfig("groupcenter.mq_exception_email_addr"))) {
					//发邮件
					String title = "LD向惠家有同步优惠券详情时存在异常记录";
					String content = "异常记录:\r\n<br/>" + JSON.toJSONString(errList);
					MailSupport mailSupport = new MailSupport();
					mailSupport.sendMail(TopUp.upConfig("groupcenter.mq_exception_email_addr"), title, content);
				}
			}
			
			new MqService().ack(channel, message);
		} catch (Exception e) {
			e.printStackTrace();
			new MqService().nackNoReQueue(channel, message);
			MDataMap mDataMap = new MDataMap();
			mDataMap.put("queue_code", QueueName.giftVoucherQueueName);
			mDataMap.put("message", new String(message.getBody()));
			mDataMap.put("exception_message", e.getMessage());
			mDataMap.put("create_time", DateUtil.getSysDateTimeString());
			DbUp.upTable("lc_mq_error_log").dataInsert(mDataMap);
			
			//发邮件
			String title = "LD向惠家有同步优惠券详时异常";
			String content = "异常信息:\r\n<br/>" + e.getMessage();
			MailSupport mailSupport = new MailSupport();
			mailSupport.sendMail(TopUp.upConfig("groupcenter.mq_exception_email_addr"), title, content);
		}
		
	}

}
