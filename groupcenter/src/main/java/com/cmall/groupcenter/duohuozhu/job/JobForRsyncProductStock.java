package com.cmall.groupcenter.duohuozhu.job;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.duohuozhu.support.ProductForDuohuozhuSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapcom.topdo.TopConfig;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 定时同步库存
 */
public class JobForRsyncProductStock extends RootJob {

	ProductForDuohuozhuSupport support = new ProductForDuohuozhuSupport();
	
	@Override
	public void doExecute(JobExecutionContext context) {
		RootResult result;
		List<String> failedList = new ArrayList<String>();
		
		// 查询所有未同步且重试此次未达到上限的
		List<MDataMap> mapList = DbUp.upTable("pc_product_duohz").queryAll("product_code", "", "rsync_flag = 1", new MDataMap());
		for(MDataMap m : mapList) {
			result = support.rsyncProductStock(m.get("product_code"));
			
			if(result.getResultCode() != 1) {
				failedList.add("["+m.get("product_code")+":"+result.getResultMessage()+"]");
			}
		}
		
		// 发消息通知
		if(!failedList.isEmpty()) {
			String noticeMail = TopConfig.Instance.bConfig("oneall.count_notice");
			if(StringUtils.isNotBlank(noticeMail)) {
				MailSupport.INSTANCE.sendMail(noticeMail, "同步多货主商品库存失败", "失败商品编号："+StringUtils.join(failedList,","));
			}
		}
	}

}
