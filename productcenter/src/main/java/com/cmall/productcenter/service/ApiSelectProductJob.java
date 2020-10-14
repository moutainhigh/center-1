package com.cmall.productcenter.service;

import org.quartz.JobExecutionContext;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;


/**
 * 促销系统商品选择控件定时
 * @author zhouguohui
 *
 */
public class ApiSelectProductJob extends RootJob{

	public void doExecute(JobExecutionContext context) {
		try{
			
			DbUp.upTable("sc_select_product").dataExec("delete from systemcenter.sc_select_product", new MDataMap());
			String sql="INSERT INTO systemcenter.sc_select_product(sku_code,sell_price,market_price,sku_name,product_code,product_name,seller_code,small_seller_code,product_status,stock_num) SELECT"+
							" si.sku_code AS sku_code,si.sell_price AS sell_price,pi.market_price AS market_price,si.sku_name AS sku_name,pi.product_code AS product_code,pi.product_name AS product_name,"+
							"pi.seller_code AS seller_code,pi.small_seller_code AS small_seller_code,pi.product_status AS product_status,"+
							"(SELECT ifnull(sum(ss.stock_num), 0) FROM systemcenter.sc_store_skunum ss WHERE ss.sku_code = si.sku_code) AS stock_num"+
							" FROM productcenter.pc_productinfo pi LEFT JOIN productcenter.pc_skuinfo si ON 	si.product_code = pi.product_code "+
							" WHERE si.flag_enable = '1' AND si.sale_yn = 'Y'";
			DbUp.upTable("sc_select_product").dataExec(sql, new MDataMap());
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
