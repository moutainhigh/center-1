package com.cmall.productcenter.service.api;

import java.math.BigDecimal;
import java.util.Map;

import com.cmall.productcenter.model.api.ApiGetSkuInput;
import com.cmall.productcenter.model.api.ApiGetSkuResult;
import com.cmall.productcenter.service.ProductStoreService;
import com.cmall.systemcenter.service.StoreService;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
/**
 * 根据skucode查询sku简单信息
 * @author jl
 *
 */
public class ApiGetSku extends RootApi<ApiGetSkuResult,ApiGetSkuInput>  {

	public ApiGetSkuResult Process(ApiGetSkuInput api,MDataMap mRequestMap) {
		ApiGetSkuResult result = new ApiGetSkuResult();
		String skuCode=api.getSkuCode();
		MDataMap dataMap=DbUp.upTable("pc_skuinfo").oneWhere("sku_code,sell_price,market_price,stock_num,sku_name,product_code,seller_code", "", "sku_code=:sku_code", "sku_code",skuCode);
		
		if(dataMap != null&&dataMap.size()>0){
			
//			StoreService storeService=BeansHelper.upBean("bean_com_cmall_systemcenter_service_StoreService");
			String product_code=dataMap.get("product_code");
			String product_name="";
			String product_status="";
			BigDecimal product_market_price=BigDecimal.ZERO;
			try {
				Map<String, Object> map=DbUp.upTable("pc_productinfo").dataQuery("product_name,product_status,market_price", "", "product_code=:product_code", new MDataMap("product_code",product_code), 0, 1).get(0);
				product_name=(String)map.get("product_name");
				product_status=(String)map.get("product_status");
				product_market_price=(BigDecimal)map.get("market_price");
				
				if(!"".equals(product_status)){ //汉字代替编码
					product_status = (String)DbUp.upTable("sc_define").dataGet("define_name", "define_code=:define_code and parent_code='449715390006' ", new MDataMap("define_code",product_status));
				}
			} catch (Exception e) {
			}
			
			
//			result.setMarket_price(dataMap.get("market_price"));
			//统一使用商品的市场价
			result.setMarket_price(String.valueOf(product_market_price));
			result.setSku_code(dataMap.get("sku_code"));
			result.setSell_price(dataMap.get("sell_price"));
			result.setSeller_code(dataMap.get("seller_code"));
//			result.setStock_num(dataMap.get("stock_num"));
			result.setProduct_name(product_name);
			result.setProduct_status(product_status);
			//这里查询分库存
//			result.setStock_num(String.valueOf(storeService.getStockNumByStore(skuCode)));
			result.setStock_num(String.valueOf(new ProductStoreService().getStockNumBySku(skuCode)));
			
			result.setSku_name(dataMap.get("sku_name"));
			result.setProduct_code(product_code);
		}
		
		return result;
	}

}
