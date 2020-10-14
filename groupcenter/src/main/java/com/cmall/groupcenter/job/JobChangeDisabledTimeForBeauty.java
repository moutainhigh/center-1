package com.cmall.groupcenter.job;


import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.cmall.productcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/***
 * 
 *  失效的化妆品  状态改为已过期(惠美丽)
 * 
 *  满足条件：失效日期小于当前日期的    每天00:00执行一次
 * 
 * @author yangrong
 * 
 */
public class JobChangeDisabledTimeForBeauty extends RootJob {

	public void doExecute(JobExecutionContext context) {

		//查出所有状态是非过期的
		String sql = "SELECT * FROM nc_cosmetic_bag WHERE status not in ('449747120002')";
		List<Map<String, Object>> list = DbUp.upTable("nc_cosmetic_bag").dataSqlList(sql, null);
		
		if(list!=null && list.size()!=0){
			for (int i = 0; i < list.size(); i++) {
				
				Map<String, Object> map = list.get(i);
				
				if(!(map.get("disabled_time").toString()).equals("")){         //失效日期为空的不做修改
					
					int flag = DateUtil.getSysDateString().compareTo(map.get("disabled_time").toString());
					
					//失效日期小于当前日期  状态改为已过期
					if(flag>0){
						
						MDataMap mDataMap = new MDataMap();
						
						mDataMap.put("uid", map.get("uid").toString());
						
						mDataMap.put("status", "449747120002");                        //已过期
						
						DbUp.upTable("nc_cosmetic_bag").dataUpdate(mDataMap,"status","uid");
						
					}
				}
				
			}
		}


	}

	// 测试专用
	public static void main(String[] args) {
		JobChangeDisabledTimeForBeauty changeDisabledTime = new JobChangeDisabledTimeForBeauty();
		changeDisabledTime.doExecute(null);
	}
}

