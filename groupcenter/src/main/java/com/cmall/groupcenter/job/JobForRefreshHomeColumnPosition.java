package com.cmall.groupcenter.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.quartz.JobExecutionContext;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 定时刷新首页自动选品的商品顺序
 */
public class JobForRefreshHomeColumnPosition extends RootJob {
	
	@Override
	public void doExecute(JobExecutionContext context) {
		List<Map<String,Object>> columnMapList = getColumnList();
		
		for(Map<String,Object> map : columnMapList) {
			randomPosition(map.get("column_code").toString());
		}
	}

	// 查询需要更新位置的栏目： 所有设置了自动选品的栏目
	private List<Map<String,Object>> getColumnList() {
		String sql = "SELECT c.column_code FROM `fh_apphome_nav` n, fh_apphome_column c "
				+ " WHERE n.nav_code = c.nav_code AND n.release_flag = '01' AND n.is_delete = '02' AND n.end_time > NOW() "
				+ " AND c.is_delete = '449746250002' AND c.release_flag = '449746250001' AND c.end_time > NOW() "
				+ " AND c.product_maintenance = '44975017002'";
		
		return DbUp.upTable("fh_apphome_nav").dataSqlList(sql, new MDataMap());
	}
	
	private void randomPosition(String columnCode) {
		// 先查询出所以的栏目内容
		List<MDataMap> itemList = DbUp.upTable("fh_apphome_column_content").queryAll("zid", "", "", new MDataMap("column_code", columnCode));
		
		// 填充一个包含全部位置序号的列表
		List<String> positionList = new ArrayList<String>();
		for(int i = 0; i < itemList.size(); i++) {
			positionList.add((i + 1)+"");
		}
		
		String position;
		for(MDataMap item : itemList) {
			position = positionList.remove(RandomUtils.nextInt(positionList.size()));
			item.put("position", position);
			
			// 更新顺序
			DbUp.upTable("fh_apphome_column_content").dataUpdate(item, "position", "zid");
		}
	}
	
}
