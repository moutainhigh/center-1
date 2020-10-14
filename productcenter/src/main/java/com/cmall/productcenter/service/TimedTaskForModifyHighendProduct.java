package com.cmall.productcenter.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 定时生更新高端商品价格
 * @author GaoYang
 *
 */
public class TimedTaskForModifyHighendProduct extends RootJob{

	public void doExecute(JobExecutionContext context) {
		//获取所有可用的高端商品
		List<MDataMap> hiCodeList = new ArrayList<MDataMap>();
		hiCodeList = DbUp.upTable("pc_highend_productinfo").queryAll("product_code", "", "flag_usable = 1", new MDataMap());
		for(int i = 0;i<hiCodeList.size();i++){
			String tempCode = hiCodeList.get(i).get("product_code");
			if(StringUtils.isNotBlank(tempCode)){
				//根据高端商品编码获取 该商品下的所有SKU价格（条件：商品表中flag_sale = 1）
				//用最低的SKU价格来更新高端商品表中的价格
				updateHighendProduct(tempCode);
			}
		}
	}
	
	/**
	 * 更新高端商品价格
	 * @param tempCode 高端商品编码
	 */
	private void updateHighendProduct(String tempCode) {
		
		//获取商品表中的可售商品
		List<MDataMap> saleProductList = new ArrayList<MDataMap>();
		String strWhere = "flag_sale = 1 and product_code = '" +  tempCode + "'";
		saleProductList = DbUp.upTable("pc_productinfo").queryAll("product_code", "", strWhere, new MDataMap());
		
		if(saleProductList != null && saleProductList.size() > 0){
			//SKU最低价格
			double minPrice = Double.MAX_VALUE;
			//SKU临时最低
			double tempMin = 0.00;
			//SKU列表
			List<MDataMap> tempSkuList = new ArrayList<MDataMap>();
			
			String tempWhere = "product_code = '" +  tempCode + "'";
			tempSkuList = DbUp.upTable("pc_skuinfo").queryAll("sku_code,sell_price", "", tempWhere, new MDataMap());
			
			//获取最低的SKU价格
			for(int i = 0;i<tempSkuList.size();i++){
				tempMin = Double.parseDouble(tempSkuList.get(i).get("sell_price"));
				if(tempMin < minPrice){
					minPrice = tempMin;
				}
			}
			
			//将该商品最低的SKU价格更新到高端商品表中
			updatePrice(tempCode,minPrice);
			
		} else{
			//将flag_sale<>1的高端商品flag_usable设定为0:不可用，
			//已达到过滤效果
			updateFlag(tempCode);
		}
	}

	/**
	 * 更新高端商品的价格
	 * @param tempCode
	 * @param minPrice SKU的最低价格
	 */
	private void updatePrice(String tempCode, double minPrice) {
		MDataMap insMap = new MDataMap();
		insMap.put("min_sell_price", String.valueOf(minPrice));
		insMap.put("product_code", tempCode);
		//以"高端商品编码"为单位更新
		DbUp.upTable("pc_highend_productinfo").dataUpdate(insMap, "min_sell_price", "product_code");
	}

	/**
	 * 高端商品flag_usable设定为0
	 * @param tempCode
	 */
	private void updateFlag(String tempCode) {
		MDataMap insMap = new MDataMap();
		insMap.put("flag_usable", "0");
		insMap.put("product_code", tempCode);
		//以"高端商品编码"为单位更新
		DbUp.upTable("pc_highend_productinfo").dataUpdate(insMap, "flag_usable", "product_code");
	}

}
