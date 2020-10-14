package com.cmall.groupcenter.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.alibaba.fastjson.JSONArray;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 统计每天库存变更记录
 * @author 李国杰
 *
 */
public class TotalStockChange extends RootJob{

	public void doExecute(JobExecutionContext context) {
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String querySql = "select SUBSTRING(t.create_time,1,10) as change_time, t.code as sku_code," +
        		"sum(t.change_stock) as change_stock,t.change_type as change_type from lc_stockchange t " +
        		" where CURRENT_DATE() = SUBSTRING(t.create_time,1,10)"+
        		" group by SUBSTRING(t.create_time,1,10),t.code,change_type " +
        		" order by t.code,change_type";
        List<Map<String, Object>>list = DbUp.upTable("lc_stockchange").dataSqlList(querySql,null);
      //  System.out.println( JSONArray.toJSONString(list));
        for (int i = 0; i < list.size(); i++) {
        	String sFields = "security_stock_num,product_code,stock_num";
        	MDataMap map = DbUp.upTable("pc_skuinfo").oneWhere(sFields, "",  "", "sku_code", list.get(i).get("sku_code").toString());
            list.get(i).put("create_time", format.format(new Date()));
            list.get(i).put("create_user",  "job定时任务"); 
            MDataMap mDataMap = new MDataMap(list.get(i));
            mDataMap.put("security_stock_num", map.get("security_stock_num"));
            mDataMap.put("product_code", map.get("product_code"));
            mDataMap.put("now_stock", map.get("stock_num"));
            DbUp.upTable("lc_stockchange_info").dataInsert(mDataMap);
		}
	}
}
