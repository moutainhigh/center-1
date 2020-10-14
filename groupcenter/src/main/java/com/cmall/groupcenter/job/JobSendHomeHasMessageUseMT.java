package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.systemcenter.util.SmsUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.websupport.MessageSupport;

/**
 * 家有发送短信定时 使用梦网短信接口
 * 
 * @author srnpr
 * 
 */
public class JobSendHomeHasMessageUseMT extends RootJob {

	public void doExecute(JobExecutionContext context) {

		MessageSupport messageSupport = new MessageSupport();

		//RsyncEndMessageUseM rsyncEndMessage = new RsyncEndMessageUseM();

//		List<String> zidList=new ArrayList<String>();
		
		for (MDataMap mDataMap : messageSupport.upSendListBySendSource("4497467200020001")) {

			
			/*
			rsyncEndMessage.upRsyncRequest().setHp_tel(
					mDataMap.get("msg_receive"));
			rsyncEndMessage.upRsyncRequest().setContent(
					mDataMap.get("msg_content"));

			rsyncEndMessage.doRsync();
			*/
			
			SmsUtil smsUtil=new SmsUtil();
			StringBuffer error= new StringBuffer();
//			boolean b=smsUtil.sendSms(mDataMap.get("msg_receive"), mDataMap.get("msg_content"),error);
			boolean b=smsUtil.sendSms6(mDataMap.get("msg_receive"), mDataMap.get("msg_content"),error);
			
//			if(!b){
//				zidList.add(mDataMap.get("zid"));
//			}
		}

		
//		DbUp.upTable("za_message").dataExec("update za_message set flag_finish=0 where zid in ("+ StringUtils.join(zidList,WebConst.CONST_SPLIT_COMMA) + ") ",new MDataMap());
		
		
		
		for (MDataMap mDataMap : messageSupport.upSendListBySendSource("4497467200020004")) {
			
			SmsUtil smsUtil=new SmsUtil();
			StringBuffer error= new StringBuffer();
			boolean b=smsUtil.sendSms(mDataMap.get("msg_receive"), mDataMap.get("msg_content"),error);
			
			
		}
		
		//微公社短信
		for (MDataMap mDataMap : messageSupport.upSendListBySendSource("4497467200020005")) {
			SmsUtil smsUtil=new SmsUtil();
			StringBuffer error= new StringBuffer();
			boolean b=smsUtil.sendSms5(mDataMap.get("msg_receive"), mDataMap.get("msg_content"),error);
		}
		
		//惠家有短信
		for (MDataMap mDataMap : messageSupport.upSendListBySendSource("4497467200020006")) {
			SmsUtil smsUtil=new SmsUtil();
			StringBuffer error= new StringBuffer();
			boolean b=smsUtil.sendSms6(mDataMap.get("msg_receive"), mDataMap.get("msg_content"),error);
		}
		
		//沙皮狗短信
		for (MDataMap mDataMap : messageSupport.upSendListBySendSource("4497467200020007")) {
			SmsUtil smsUtil=new SmsUtil();
			StringBuffer error= new StringBuffer();
			boolean b=smsUtil.sendSms7(mDataMap.get("msg_receive"), mDataMap.get("msg_content"),error);
		}
		
	}
	
	public static void main(String[] args) {
		SmsUtil smsUtil=new SmsUtil();
		StringBuffer error= new StringBuffer();
		boolean b=smsUtil.sendSms6("15313168722", "第一条短信",error);
		System.out.println(b);
	}

}
