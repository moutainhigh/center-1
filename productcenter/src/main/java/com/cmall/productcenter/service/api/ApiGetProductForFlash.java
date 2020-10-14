package com.cmall.productcenter.service.api;

import java.util.List;

import com.cmall.productcenter.model.api.ApiGetProductForFlashInput;
import com.cmall.productcenter.model.api.ApiGetProductForFlashResult;
import com.cmall.productcenter.service.ProductStoreService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 添加闪购的商品信息
 * @author jlin
 *
 */
public class ApiGetProductForFlash extends RootApi<ApiGetProductForFlashResult,ApiGetProductForFlashInput> {
	
	public ApiGetProductForFlashResult Process(ApiGetProductForFlashInput input, MDataMap mRequestMap) {
		
		ApiGetProductForFlashResult result = new ApiGetProductForFlashResult();
		String product_code=input.getProduct_code();
		
		MDataMap productMap=DbUp.upTable("pc_productinfo").one("product_code",product_code);
		if(productMap==null){
			result.setResultCode(941901018);
			result.setResultMessage(bInfo(941901018, product_code));
			return result;
		}
		
		result.setProduct_code(product_code);
		result.setProduct_name(productMap.get("product_name"));
		result.setProduct_price(productMap.get("market_price"));
		result.setProduct_status(DbUp.upTable("sc_define").one("parent_code","449715390006","define_code",productMap.get("product_status")).get("define_name"));
		
		List<MDataMap> skuList=DbUp.upTable("pc_skuinfo").queryAll("sku_code,sell_price,market_price", "", "product_code=:product_code and flag_enable=1 and sale_yn=:sale_yn", new MDataMap("product_code",product_code,"sale_yn","Y"));
		
		if(skuList!=null&&skuList.size()>0){
			
			ProductStoreService productStoreService = new ProductStoreService();
			
			for (MDataMap mDataMap : skuList) {
				String sku_code=mDataMap.get("sku_code");
				String sell_price=mDataMap.get("sell_price");
				
				int stock=productStoreService.getStockNumBySku(sku_code);
				if(result.getProduct_stock()>0){
					if(result.getProduct_stock()>stock){
						//使用最小库存并且使用最小库存的sku 的售价
						result.setProduct_stock(stock);
						result.setSell_price(sell_price);
					}
				}else{
					//使用最小库存并且使用最小库存的sku 的售价
					result.setProduct_stock(stock);
					result.setSell_price(sell_price);
				}
			}
			
		}
		
		return result;
	}
}
