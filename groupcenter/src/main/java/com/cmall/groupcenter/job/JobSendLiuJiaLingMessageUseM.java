package com.cmall.groupcenter.job;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncEndMessageUseM;
import com.cmall.systemcenter.util.SmsUtil;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.websupport.MessageSupport;

/**
 * 刘嘉玲发送短信定时 
 * 
 * @author srnpr
 * 
 */
public class JobSendLiuJiaLingMessageUseM extends RootJob {

	public void doExecute(JobExecutionContext context) {

		MessageSupport messageSupport = new MessageSupport();

		for (MDataMap mDataMap : messageSupport.upSendListBySendSource("4497467200020003")) {
			
			SmsUtil smsUtil=new SmsUtil();
			StringBuffer error= new StringBuffer();
			boolean b=smsUtil.sendSmsForCapp(mDataMap.get("msg_receive"), mDataMap.get("msg_content"),error);
		}

	}

}
