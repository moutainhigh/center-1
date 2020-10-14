package com.cmall.newscenter.api;

import com.cmall.newscenter.model.ProductUnFavInput;
import com.cmall.newscenter.model.ProductUnFavResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 商品_取消收藏Api
 * 
 * @author lqiang date: 2014-07-10
 * @version1.0
 */
public class ProductUnFavApi extends
		RootApiForToken<ProductUnFavResult, ProductUnFavInput> {

	public ProductUnFavResult Process(ProductUnFavInput inputParam,
			MDataMap mRequestMap) {
		ProductUnFavResult result = new ProductUnFavResult();
		// 设置相关信息
		if (result.upFlagTrue()) {
			/* 查询商品收藏信息 */
			MDataMap mDataMap = DbUp.upTable("nc_num").one("num_code",
					inputParam.getProduct(), "member_code", getUserCode(),
					"num_type", "4497464900030005");

			/* 查询商品统计表有多少人收藏过 */
			MDataMap ncMap = DbUp.upTable("nc_productfav").one("product_code",
					inputParam.getProduct());

			if (mDataMap != null) {
				mDataMap.put("flag_enable", "0");
				/* 更新数据 */
				DbUp.upTable("nc_num").dataUpdate(mDataMap, "flag_enable",
						"zid");

				ncMap.put("num_fav", String.valueOf(Integer.valueOf(ncMap.get("num_fav"))-1));
				
				DbUp.upTable("nc_productfav").dataUpdate(ncMap, "num_fav",
						"zid");
				
				result.setFaved("0");
				if (ncMap != null) {
					result.setFav_count(Integer.valueOf(ncMap.get("num_fav")));
				} else {
					result.setFav_count(0);
				}
			}
		}
		return result;
	}

}
