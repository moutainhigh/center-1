package com.cmall.newscenter.beauty.api;

import com.cmall.newscenter.beauty.model.ProductListInput;
import com.cmall.newscenter.beauty.model.ProductListResult;
import com.cmall.newscenter.beauty.model.SaleProduct;
import com.cmall.newscenter.model.PageResults;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.PicInfo;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.model.ProductSkuInfoPage;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 商品列表Api
 * 
 * @author yangrong date: 2014-09-17
 * @version1.0
 */
public class ProductsListApi extends RootApiForManage<ProductListResult, ProductListInput> {

	public ProductListResult Process(ProductListInput inputParam,MDataMap mRequestMap) {

		ProductListResult result = new ProductListResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

			ProductService productService = new ProductService();

			ProductSkuInfoPage skuinfoList = productService.getSkuInfoForC(inputParam.getCategory(), inputParam.getSort(),getManageCode(), inputParam.getPaging().getOffset(),inputParam.getPaging().getLimit());

			PageResults pageResults = new PageResults();
			pageResults.setTotal(skuinfoList.getTotal());
			pageResults.setCount(skuinfoList.getCount());
			pageResults.setMore(skuinfoList.getMore());
			result.setPaged(pageResults);

			for (int i = 0; i < skuinfoList.getPcSkuinfoList().size(); i++) {

				SaleProduct sp = new SaleProduct();

				PcProductinfo procuctinfo = (PcProductinfo) skuinfoList.getPcSkuinfoList().get(i).get("productInfo");

				ProductSkuInfo skuinfo = (ProductSkuInfo) skuinfoList.getPcSkuinfoList().get(i).get("skuInfo");

				sp.setId(skuinfo.getSkuCode());

				PicInfo pic = productService.getPicInfo(Integer.valueOf(inputParam.getPicWidth()),skuinfo.getSkuPicUrl());

				sp.setPhoto(pic.getPicNewUrl());
				sp.setSell_price(skuinfo.getSellPrice().toString());
				sp.setTitle(skuinfo.getSkuName());
				sp.setMarket_price(procuctinfo.getMarketPrice().toString());
				sp.setBuy_count(String.valueOf(skuinfo.getSellCount()));

				String[] labls = procuctinfo.getLabels().split(",");

				if (labls != null) {

					for (int j = 0; j < labls.length; j++) {
						sp.getLabels().add(labls[j]);
					}
				}

				result.getProducts().add(sp);
			}

		}
		return result;
	}
}
