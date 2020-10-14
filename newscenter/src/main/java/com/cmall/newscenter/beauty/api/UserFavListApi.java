package com.cmall.newscenter.beauty.api;

import java.util.List;
import java.util.Map;

import com.cmall.newscenter.beauty.model.SaleProduct;
import com.cmall.newscenter.beauty.model.UserFavListInput;
import com.cmall.newscenter.beauty.model.UserFavListResult;
import com.cmall.newscenter.model.PageResults;
import com.cmall.productcenter.model.PcProductPrice;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 用户收藏列表Api
 * 
 * @author yangrong date: 2014-09-16
 * @version1.0
 */
public class UserFavListApi extends RootApiForToken<UserFavListResult, UserFavListInput> {

	public UserFavListResult Process(UserFavListInput inputParam,MDataMap mRequestMap) {

		UserFavListResult result = new UserFavListResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

			// 查询收藏信息
			MDataMap mWhereMap = new MDataMap();

			mWhereMap.put("app_code", getManageCode());

			mWhereMap.put("member_code", getUserCode());

			mWhereMap.put("flag", "1");

			List<MDataMap> mPageDataList = DbUp.upTable("nc_productfav").queryAll("", "-creat_time", "", mWhereMap);

			// 收藏总数
			int totalNum = mPageDataList.size();
			int offset = inputParam.getPaging().getOffset();// 起始页 1
			int limit = inputParam.getPaging().getLimit();// 每页条数 10
			int startNum = limit * offset;// 开始条数 10
			int endNum = startNum + limit;// 结束条数 20
			int more = 1;// 有更多数据
			Boolean flag = true;
			if (startNum < totalNum) {
				flag = false;
			}
			if (endNum >= totalNum) {
				endNum = totalNum;
				more = 0;
			}

			// 分页信息
			PageResults pageResults = new PageResults();
			pageResults.setTotal(totalNum);
			pageResults.setCount(endNum - startNum);
			pageResults.setMore(more);
			result.setPaged(pageResults);

			List<MDataMap> subList = mPageDataList.subList(startNum, endNum);

			for (MDataMap mDataMap : subList) {

				ProductService productService = new ProductService();

				List<Map<String, Object>> productsList = productService.getSkuInfoForSkus(mDataMap.get("product_code"),getManageCode());

				if (!flag) {

					for (int i = 0; i < productsList.size(); i++) {

						PcProductinfo productinfo = (PcProductinfo) productsList.get(i).get("productInfo");
						ProductSkuInfo skuInfo = (ProductSkuInfo) productsList.get(i).get("skuInfo");

						SaleProduct sp = new SaleProduct();

						sp.setId(skuInfo.getSkuCode());
						sp.setPhoto(skuInfo.getSkuPicUrl());

						sp.setProductType(String.valueOf(productService.getSkuActivityType(skuInfo.getSkuCode(),getManageCode())));

						// 试用类型用普通商品类型展示 试用商品没有收藏功能
						if (productService.getSkuActivityType(skuInfo.getSkuCode(), getManageCode()) == 2) {
							sp.setProductType("0");

						}
						String[] labls = productinfo.getLabels().split(",");

						if (labls != null) {

							for (int j = 0; j < labls.length; j++) {
								sp.getLabels().add(labls[j]);
							}
						}
						sp.setTitle(skuInfo.getSkuName());

						PcProductPrice productPrice = productService.getSkuProductPrice(skuInfo.getSkuCode(),getManageCode());

						sp.setMarket_price(productPrice.getMarketPrice().toString());

						// 有活动价格显示活动价格 没有活动价格显示销售价
						if (("").equals(productPrice.getVipPrice())|| null == productPrice.getVipPrice()) {

							sp.setSell_price(productPrice.getSellPrice().toString());
						} else {

							sp.setSell_price(productPrice.getVipPrice());
						}
						sp.setBuy_count(String.valueOf(skuInfo.getSellCount()));

						result.getProducts().add(sp);
					}
				}

			}

		}
		return result;
	}
}
