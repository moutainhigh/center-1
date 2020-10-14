package com.cmall.groupcenter.duohuozhu.job;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.duohuozhu.support.ProductForDuohuozhuSupport;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapcom.topdo.TopConfig;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 定时提交新的商品到多货主系统
 */
public class JobForRsyncProductInfo extends RootJob {

	ProductForDuohuozhuSupport support = new ProductForDuohuozhuSupport();
	
	@Override
	public void doExecute(JobExecutionContext context) {
		RootResult result;
		int rsyncNum;
		List<String> failedList = new ArrayList<String>();
		
		// 查询所有未同步且重试此次未达到上限的
		List<MDataMap> mapList = DbUp.upTable("pc_product_duohz").queryAll("zid,product_code,rsync_num", "", "rsync_flag = 0 AND rsync_num < 5", new MDataMap());
		for(MDataMap m : mapList) {
			result = support.rsyncNewProduct(m.get("product_code"));
			rsyncNum = NumberUtils.toInt(m.get("rsync_num")) + 1;
			
			if(result.getResultCode() == 1) {
				m.put("rsync_flag", "1");
				m.put("rsync_num", rsyncNum+"");
				m.put("update_time", FormatHelper.upDateTime());
				DbUp.upTable("pc_product_duohz").dataUpdate(m, "rsync_flag,rsync_num,update_time", "zid");
			} else {
				m.put("rsync_num", rsyncNum+"");
				m.put("update_time", FormatHelper.upDateTime());
				DbUp.upTable("pc_product_duohz").dataUpdate(m, "rsync_num,update_time", "zid");
			}
			
			// 达到失败次数则发消息通知人员处理
			if(result.getResultCode() != 1 && rsyncNum >= 5) {
				failedList.add(m.get("product_code"));
			}
		}
		
		// 发消息通知
		if(!failedList.isEmpty()) {
			String noticeMail = TopConfig.Instance.bConfig("oneall.count_notice");
			if(StringUtils.isNotBlank(noticeMail)) {
				MailSupport.INSTANCE.sendMail(noticeMail, "提交商品到多货主系统失败", "失败商品编号："+StringUtils.join(failedList,","));
			}
		}
	}

}
