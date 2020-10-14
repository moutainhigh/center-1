package com.cmall.usercenter.template;

import org.quartz.JobExecutionContext;

import com.cmall.usercenter.service.UcShopTemplateService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 更新所有店铺模板（通用模板）
 * 
 * @author srnpr
 * 
 */
public class JobRefreshTemplate extends RootJob {

	public void doExecute(JobExecutionContext context) {

		UcShopTemplateService ucShopTemplateService = new UcShopTemplateService();
		ucShopTemplateService.refreshTemplate();

		for (MDataMap mSellerInfo : DbUp.upTable("uc_sellerinfo").queryAll(
				"seller_code", "", "", new MDataMap())) {
			ucShopTemplateService.updateCommonTemplate(mSellerInfo
					.get("seller_code"));
		}

	}

}
