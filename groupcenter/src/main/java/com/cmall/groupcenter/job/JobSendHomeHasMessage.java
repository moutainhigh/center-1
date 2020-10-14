package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncEndMessage;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.websupport.MessageSupport;

/**
 * 家有发送短信定时<br>
 * 使用家有接口发送短信
 * @author srnpr
 * 
 */
public class JobSendHomeHasMessage extends RootJob {

	public void doExecute(JobExecutionContext context) {

//		MessageSupport messageSupport = new MessageSupport();
//
//		RsyncEndMessage rsyncEndMessage = new RsyncEndMessage();
//
//		//4497467200020004 家有汇通道
//		for (MDataMap mDataMap : messageSupport.upSendListBySendSource("4497467200020004")) {
//
//			rsyncEndMessage.upRsyncRequest().setHp_tel(
//					mDataMap.get("msg_receive"));
//			rsyncEndMessage.upRsyncRequest().setContent(
//					mDataMap.get("msg_content"));
//
//			rsyncEndMessage.doRsync();
//
//		}

	}

}
