package com.cmall.groupcenter.job;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.quartz.JobExecutionContext;

import com.cmall.productcenter.service.ProductStoreService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 定时把分库中的库存更新到sku表的库存中，用于不实时查询库存的查询中的使用
 * @author jl
 *
 */
public class JobUpdateSkuStock extends RootJob {

	public void doExecute(JobExecutionContext context) {
		// 查询实时库存跟SKU表里面的库存不一致的数据，限定只查询SI2003的商品，其他商品不再使用所以不再更新
		String sql = "SELECT s.sku_code, s.stock_num oldNum, SUM(o.stock_num) newNum FROM `pc_productinfo` p, pc_skuinfo s, systemcenter.sc_store_skunum o ";
			   sql+= " WHERE p.product_status = '4497153900060002' AND p.seller_code = 'SI2003'";
			   sql+= " AND p.product_code = s.product_code";
			   sql+= " AND s.sku_code = o.sku_code";
			   sql+= " GROUP BY s.sku_code HAVING oldNum != newNum";
			   
			   
		//List<Map<String, Object>> list=DbUp.upTable("pc_skuinfo").dataSqlList("SELECT sku_code from pc_skuinfo WHERE product_code in (SELECT DISTINCT product_code from pc_productinfo where product_status='4497153900060002') ", null);
	   List<Map<String, Object>> list=DbUp.upTable("pc_skuinfo").dataSqlList(sql, null);
		
		if(list==null||list.size()<1){
			return ;
		}
		
		//ProductStoreService storeService = new ProductStoreService();
		
		for (Map<String, Object> map : list) {
			String sku_code = (String)map.get("sku_code");
			
			//int stock_num = storeService.getStockNumBySku(sku_code);
			int stock_num = NumberUtils.toInt(map.get("newNum")+"");
			//modify by zht.The second statement leads to a NullPointerException.
			DbUp.upTable("pc_skuinfo").dataExec("update pc_skuinfo set stock_num="+stock_num+" where sku_code='"+sku_code+"'", new MDataMap());
			//DbUp.upTable("pc_skuinfo").dataExec("update pc_skuinfo set stock_num="+stock_num+" where sku_code='"+sku_code+"'", null);
		}
		
	}
	
}
