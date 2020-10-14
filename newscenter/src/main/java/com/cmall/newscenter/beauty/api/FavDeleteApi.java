package com.cmall.newscenter.beauty.api;

import com.cmall.newscenter.beauty.model.FavDeleteInput;
import com.cmall.newscenter.beauty.model.FavDeleteResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 惠美丽—删除我的收藏Api
 * 
 * @author yangrong date: 2014-09-11
 * @version1.0
 */
public class FavDeleteApi extends RootApiForToken<FavDeleteResult, FavDeleteInput> {

	public FavDeleteResult Process(FavDeleteInput inputParam,MDataMap mRequestMap) {

		FavDeleteResult result = new FavDeleteResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

			if (inputParam.getIsAll().equals("1")) {

				DbUp.upTable("nc_productfav").delete("app_code",getManageCode(), "member_code", getUserCode());

			} else {

				String[] idsStrings = inputParam.getIds().split(",");

				for (int i = 0; i < idsStrings.length; i++) {

					DbUp.upTable("nc_productfav").delete("app_code",getManageCode(), "member_code", getUserCode(),"product_code", idsStrings[i]);

				}
			}

		}
		return result;
	}

}
