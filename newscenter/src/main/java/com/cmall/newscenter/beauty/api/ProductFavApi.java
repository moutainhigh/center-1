package com.cmall.newscenter.beauty.api;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.cmall.newscenter.beauty.model.ProductFavInput;
import com.cmall.newscenter.beauty.model.ProductFavResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 惠美丽商品_收藏Api
 * 
 * @author yangrong date: 2014-09-11
 * @version1.0
 */
public class ProductFavApi extends RootApiForToken<ProductFavResult, ProductFavInput> {

	public ProductFavResult Process(ProductFavInput inputParam,MDataMap mRequestMap) {

		ProductFavResult result = new ProductFavResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

			MDataMap mWhereMap = new MDataMap();

			mWhereMap.put("app_code", getManageCode());

			mWhereMap.put("member_code", getUserCode());

			MPageData mPageData = DataPaging.upPageData("nc_productfav", "","", mWhereMap, new PageOption());

			Boolean flag = true;

			if (mPageData != null) {

				for (MDataMap mDataMap : mPageData.getListData()) {

					if (mDataMap.get("product_code").equals(inputParam.getSku_code())) {

						// 操作时间为当前系统时间
						SimpleDateFormat df = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss"); // 设置日期格式

						mDataMap.put("creat_time", df.format(new Date())); // new
																			// Date()为获取当前系统时间

						flag = false;

						if (mDataMap.get("flag").equals("1")) {

							mDataMap.put("flag", "0");

						} else {

							mDataMap.put("flag", "1");
						}

						DbUp.upTable("nc_productfav").update(mDataMap);

					}
				}
			}

			if (flag) {

				MDataMap mInsertMap = new MDataMap();

				// 创建时间为当前系统时间
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 设置日期格式

				mInsertMap.put("creat_time", df.format(new Date())); // new
																		// Date()为获取当前系统时间

				mInsertMap.put("product_code", inputParam.getSku_code());

				mInsertMap.put("num_fav", "1");

				mInsertMap.put("app_code", getManageCode());

				mInsertMap.put("member_code", getUserCode());

				mInsertMap.put("flag", "1");

				DbUp.upTable("nc_productfav").dataInsert(mInsertMap);
			}

		}

		return result;
	}

}
