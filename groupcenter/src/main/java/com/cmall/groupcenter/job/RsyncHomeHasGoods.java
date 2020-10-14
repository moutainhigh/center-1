package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncSyncGoods;
import com.srnpr.zapweb.rootweb.RootJob;

public class RsyncHomeHasGoods extends RootJob {

	public void doExecute(JobExecutionContext context) {
		// 商品同步
		RsyncSyncGoods rsyncSyncGoods = new RsyncSyncGoods();
		rsyncSyncGoods.doRsync();

//		// 同步商品样式颜色
//		RsyncSyncgetSYGoodbyColor syGoodbyColor = new RsyncSyncgetSYGoodbyColor();
//		syGoodbyColor.doRsync();

	}

}
