package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.weixin.WeiXinUtil;
import com.srnpr.zapweb.rootweb.RootJob;

/***
 * 微信accessToken更新
 * 
 * @author panwei
 *
 */
public class JobWxUpdateAccessToken extends RootJob {

	
	public void doExecute(JobExecutionContext context) {
		
		WeiXinUtil wxUtil=new WeiXinUtil();
		wxUtil.updateAccessToken();
	}
}
