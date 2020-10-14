package com.cmall.newscenter.api;

import java.security.SecureRandom;

import com.cmall.newscenter.model.ProductFavInput;
import com.cmall.newscenter.model.ProductFavResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 商品_收藏Api
 * 
 * @author lqiang date: 2014-07-10
 * @version1.0
 */
public class ProductFavApi extends
		RootApiForToken<ProductFavResult, ProductFavInput> {

	public ProductFavResult Process(ProductFavInput inputParam,
			MDataMap mRequestMap) {
		ProductFavResult result = new ProductFavResult();
		
		SecureRandom random = new SecureRandom();  
		
		// 设置相关信息
		if (result.upFlagTrue()) {

			/* 查询商品收藏信息 */
			MDataMap mDataMap = DbUp.upTable("nc_num").one("num_code",
					inputParam.getProduct(), "member_code", getUserCode(),"num_type","4497464900030005");

			/* 查询商品统计表有多少人收藏过 */
			MDataMap ncMap = DbUp.upTable("nc_productfav").one("product_code",
					inputParam.getProduct());

			if (mDataMap != null) {//该商品收藏过
				
				if(!mDataMap.get("flag_enable").equals("1")){//当前状态是未收藏
					mDataMap.put("flag_enable", "1");
					/* 更新数据 */
					DbUp.upTable("nc_num").dataUpdate(mDataMap, "flag_enable",
							"zid");
					
					int num = Integer.valueOf(ncMap.get("num_fav"));
					
					ncMap.put("num_fav", String.valueOf(num+random.nextInt(5)+1));
					
					DbUp.upTable("nc_productfav").dataUpdate(ncMap, "num_fav",
							"zid");
					
				}
				result.setFaved("1");
				// 返回收藏人数
				if (ncMap != null) {
					result.setFav_count(Integer.valueOf(ncMap
							.get("num_fav")));
				} else {
					result.setFav_count(0);
				}
				
			} else {//该商品未收藏过

				result.setFaved("1");
				MDataMap insertMap = new MDataMap();
				/* 将数据放入map中 */
				insertMap.inAllValues("num_code", inputParam.getProduct(),
						"member_code", getUserCode(), "create_time",
						FormatHelper.upDateTime(), "flag_enable", "1",
						"num_type", "4497464900030005");
				/* 将用户收藏记录数据插入表中 */
				DbUp.upTable("nc_num").dataInsert(insertMap);
				
				int num = 0;//当前收藏的人数
				
				if (ncMap != null) {
					num = Integer.valueOf(ncMap.get("num_fav"));
					ncMap.put("num_fav", String.valueOf(num+random.nextInt(5)+1));
					DbUp.upTable("nc_productfav").dataUpdate(ncMap, "num_fav",
							"zid");
					
					result.setFav_count(Integer.valueOf(ncMap.get("num_fav")));// 返回收藏人数
					
				}else{
					MDataMap proMap = new MDataMap();
					proMap.inAllValues("product_code",inputParam.getProduct(),"num_fav","1","app_code",getManageCode());
					DbUp.upTable("nc_productfav").dataInsert(proMap);
					
					result.setFav_count(Integer.valueOf(proMap.get("num_fav")));// 返回收藏人数
				}
				
				
			}

		}
		return result;
	}

}
