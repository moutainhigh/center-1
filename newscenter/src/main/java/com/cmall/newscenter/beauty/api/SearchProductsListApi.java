package com.cmall.newscenter.beauty.api;

import java.util.List;
import java.util.Map;

import com.cmall.newscenter.beauty.model.SearchProductsListInput;
import com.cmall.newscenter.beauty.model.SearchProductsListResult;
import com.cmall.newscenter.beauty.model.SaleProduct;
import com.cmall.productcenter.model.PcProductPrice;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.PicInfo;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 搜索商品 API
 * 
 * @author yangrong date 2014-9-20
 * @version 1.0
 */
public class SearchProductsListApi extends RootApiForManage<SearchProductsListResult, SearchProductsListInput> {

	public SearchProductsListResult Process(SearchProductsListInput inputParam,MDataMap mRequestMap) {

		SearchProductsListResult result = new SearchProductsListResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

			ProductService productService = new ProductService();

			List<Map<String, Object>> productsList = productService.getSkuInfoForFuzzy(inputParam.getKeyword(),getManageCode());

			// 返回界面商品列表
			for (int i = 0; i < productsList.size(); i++) {

				PcProductinfo productinfo = (PcProductinfo) productsList.get(i).get("productInfo");
				ProductSkuInfo skuInfo = (ProductSkuInfo) productsList.get(i).get("skuInfo");

				SaleProduct sp = new SaleProduct();

				sp.setId(skuInfo.getSkuCode());

				PicInfo pic = productService.getPicInfo(Integer.valueOf(inputParam.getPicWidth()),skuInfo.getSkuPicUrl());

				sp.setPhoto(pic.getPicNewUrl());

				String[] labls = productinfo.getLabels().split(",");

				if (labls != null) {

					for (int j = 0; j < labls.length; j++) {
						sp.getLabels().add(labls[j]);
					}
				}
				sp.setTitle(skuInfo.getSkuName());

				ProductService product = new ProductService();

				PcProductPrice productPrice = product.getSkuProductPrice(skuInfo.getSkuCode(), getManageCode());

				sp.setMarket_price(productPrice.getMarketPrice().toString());

				// 有活动价格显示活动价格 没有活动价格显示销售价
				if (("").equals(productPrice.getVipPrice())|| null == productPrice.getVipPrice()) {

					sp.setSell_price(productPrice.getSellPrice().toString());
				} else {

					sp.setSell_price(productPrice.getVipPrice());
				}

				sp.setBuy_count(String.valueOf(skuInfo.getSellCount()));

				sp.setProductType(String.valueOf(productService.getSkuActivityType(skuInfo.getSkuCode(),getManageCode())));

				result.getProducts().add(sp);
			}
		}

		return result;
	}
}
