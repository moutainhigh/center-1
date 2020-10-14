package com.cmall.groupcenter.job;

import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.quartz.JobExecutionContext;

import com.cmall.productcenter.support.AutoSelectProductSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJobForExclusiveLock;

/**
 * 每日定时更新自动选品缓存
 */
public class JobForProductXuanpinCache extends RootJobForExclusiveLock {
	
	public void doExecute(JobExecutionContext context) {
		List<MDataMap> mapList = DbUp.upTable("pc_product_xuanpin").queryByWhere("delete_flag", "0");
		
		AutoSelectProductSupport autoSelectProductSupport = new AutoSelectProductSupport();
		
		int productNum, maxSize, dayNum, totalPage;
		for(MDataMap map : mapList) {
			maxSize = NumberUtils.toInt(map.get("max_size"));
			dayNum = NumberUtils.toInt(map.get("day_num"));
			productNum = autoSelectProductSupport.refreshXuanpinCache(map.get("xp_code"));
			totalPage = productNum / maxSize;
			
			// 如果当前已经是最后一页则从第一页重新开始
			if(dayNum >= totalPage) {
				dayNum = 1;
			} else {
				dayNum ++;
			}
			
			map.put("day_num", dayNum+"");
			DbUp.upTable("pc_product_xuanpin").dataUpdate(map, "day_num", "zid");
		}
	}

}
