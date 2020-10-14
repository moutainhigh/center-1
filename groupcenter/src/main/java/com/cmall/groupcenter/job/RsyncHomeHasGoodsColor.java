package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncSyncgetSYGoodbyColor;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 同步商品属性
 * @author jlin
 *
 */
public class RsyncHomeHasGoodsColor  extends RootJob {

	public void doExecute(JobExecutionContext context) {

		// 同步商品样式颜色
		RsyncSyncgetSYGoodbyColor syGoodbyColor = new RsyncSyncgetSYGoodbyColor();
		syGoodbyColor.doRsync();

	}

}
