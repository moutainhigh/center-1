package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.productcenter.service.ProductSkuPriceService;
import com.srnpr.zapweb.rootweb.RootJob;

public class PcSkuPriceChangeTimeJob  extends RootJob {
	public void doExecute(JobExecutionContext context) {
		new ProductSkuPriceService().updateSkupriceTimeScopeNew();
	}
}
