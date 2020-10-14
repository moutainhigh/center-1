package com.cmall.newscenter.beauty.api;

import com.cmall.newscenter.beauty.model.Province;
import com.cmall.newscenter.beauty.model.ProvinceInput;
import com.cmall.newscenter.beauty.model.ProvinceResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 惠美丽-获取省API
 * 
 * @author yangrong date 2014-9-11
 * @version 1.0
 */
public class ProvinceApi extends RootApiForManage<ProvinceResult, ProvinceInput> {

	public ProvinceResult Process(ProvinceInput inputParam, MDataMap mRequestMap) {

		ProvinceResult result = new ProvinceResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

			// 查询省市区信息
			MDataMap mWhereMap = new MDataMap();

			MPageData mPageData = DataPaging.upPageData("sc_gov_district", "","", mWhereMap, new PageOption());

			if (mPageData != null) {

				for (MDataMap mDataMap : mPageData.getListData()) {

					// 过滤省
					if (mDataMap.get("code").substring(2, 6).equals("0000")) {

						Province province = new Province();

						province.setId(mDataMap.get("code"));
						province.setName(mDataMap.get("name"));

						result.getProvinces().add(province);

					}

				}
			}

		}
		return result;

	}

}
