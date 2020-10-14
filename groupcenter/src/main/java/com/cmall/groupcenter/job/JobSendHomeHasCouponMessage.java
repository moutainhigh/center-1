package com.cmall.groupcenter.job;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncSendHjyDiscountSms;
import com.cmall.groupcenter.support.CouponSupport;
import com.cmall.groupcenter.support.CouponSupport.CouponMessage;
import com.cmall.groupcenter.support.CouponSupport.CouponMessage.CouponSum;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.webdo.WebConst;

/**
 * 定时为送券用户发送营销短信
 * @author cc
 *
 */
public class JobSendHomeHasCouponMessage  extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
		CouponSupport couponSupport = new CouponSupport();
		CouponMessage upSend = couponSupport.upSendListByNoticeFlag();
		if(upSend != null && upSend.getSendMessage() != null && upSend.getSendMessage().size() > 0) {
			
			RsyncSendHjyDiscountSms sendSms = new RsyncSendHjyDiscountSms();
			
			String content = bConfig("groupcenter.sms_coupon_content");
			
			/**
			 * 整理短信内容
			 */
			for(CouponSum cm : upSend.getSendMessage()) {
				sendSms.upRsyncRequest().getItems().add(new RsyncSendHjyDiscountSms.SmsInfo(cm.getCustId(), cm.getPhone(), content));
			}

			/**
			 * 将汇总好的营销短信推送给家有，并更新通知标识改为已通知
			 */
			if(!sendSms.upRsyncRequest().getItems().isEmpty()){
				if(sendSms.doRsync()){
					DbUp.upTable("oc_order_ld_coupon_task").dataExec("update oc_order_ld_coupon_task set notify_flag=1 ,notify_num = notify_num + 1,update_time = now() where zid in ("+ StringUtils.join(upSend.getZids(),	WebConst.CONST_SPLIT_COMMA) + "); ",new MDataMap());
				}else{
					DbUp.upTable("oc_order_ld_coupon_task").dataExec("update oc_order_ld_coupon_task set notify_num = notify_num + 1,update_time = now() where zid in ("+ StringUtils.join(upSend.getZids(),	WebConst.CONST_SPLIT_COMMA) + "); ",new MDataMap());
				}
			}
		}

	}

}
