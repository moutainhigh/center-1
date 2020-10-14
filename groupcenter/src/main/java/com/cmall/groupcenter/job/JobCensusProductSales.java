package com.cmall.groupcenter.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/***
 * 统计商品销量
 * 
 * @author ligj
 *
 */
public class JobCensusProductSales extends RootJob {

	
	public void doExecute(JobExecutionContext context) {
		
		StringBuffer censusSql = new StringBuffer();  
		censusSql.append(" select od.product_code as product_code , ");											//product_code
		censusSql.append(" (select SUM(od1.sku_num) from oc_orderdetail od1,oc_orderinfo oi ");					//近30天的销量
        censusSql.append(" where od1.order_code = oi.order_code " );
		censusSql.append(" and oi.create_time > date_sub(curdate(), INTERVAL 30 DAY) ");							
		censusSql.append(" and od1.product_code = od.product_code)  as thirty_day, ");
		censusSql.append(" (select SUM(od2.sku_num) from oc_orderdetail od2,oc_orderinfo oi ");					//上月的销量
		censusSql.append(" where od2.order_code = oi.order_code ");
		censusSql.append(" and date_format(oi.create_time,'%Y-%m') = date_format(date_sub(curdate(), interval 1 month),'%Y-%m') ");
		censusSql.append(" and od2.product_code  = od.product_code) as last_month, ");	
		censusSql.append(" SUM(od.sku_num) as total_all, ");														//总销量
		censusSql.append(" info.seller_code as seller_code ");																	//seller_code
        censusSql.append(" from oc_orderdetail od,oc_orderinfo info where od.order_code=info.order_code ");
		censusSql.append(" group by od.product_code ");
		
		 List<Map<String, Object>> list=DbUp.upTable("oc_orderinfo").dataSqlList(censusSql.toString(),null);
		 
		//获取销量统计表中所有的product_code,用以判断是否存在销量统计表中.
		List<MDataMap> salesMapList = DbUp.upTable("oc_product_salesCount").queryAll("product_code,uid", "", "", null);
		Map<String,String> salesMap = new HashMap<String, String>();
		for (MDataMap mDataMap : salesMapList) {
			salesMap.put(mDataMap.get("product_code"), mDataMap.get("uid"));
		}
	    if(list!=null&&list.size()>0){
			 for (Map<String, Object> map : list) {
				String product_code=String.valueOf((map.get("product_code") == null ? "" : map.get("product_code")));
				String thirty_day=String.valueOf((map.get("thirty_day") == null ? "0" : map.get("thirty_day")));
				String last_month=String.valueOf((map.get("last_month") == null ? "0" : map.get("last_month")));
				String total_all=String.valueOf((map.get("total_all") == null ? "0" : map.get("total_all")));
				String seller_code=String.valueOf((map.get("seller_code") == null ? "" : map.get("seller_code")));
				
				MDataMap mDataMap = new MDataMap();
				mDataMap.put("product_code", product_code);
				mDataMap.put("thirty_day", thirty_day);
				mDataMap.put("last_month", last_month);
				mDataMap.put("total_all", total_all);
				mDataMap.put("seller_code", seller_code);
				
				
				if (StringUtils.isNotEmpty(salesMap.get(product_code))) {
					mDataMap.put("uid", salesMap.get(product_code));
					DbUp.upTable("oc_product_salesCount").dataUpdate(mDataMap,"thirty_day,last_month,total_all,seller_code","uid");
				}else{
					DbUp.upTable("oc_product_salesCount").dataInsert(mDataMap);
				}
			}
		 }
	}
}
