package com.cmall.newscenter.beauty.api;

import org.apache.commons.lang.StringUtils;

import com.cmall.newscenter.beauty.model.ProductShareStatusInput;
import com.cmall.newscenter.beauty.model.ProductShareStatusResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 商品_分享状态查询Api
 * 
 * @author yangrong date: 2014-09-23
 * @version1.0
 */
public class ProductShareStatusApi extends RootApiForToken<ProductShareStatusResult, ProductShareStatusInput> {

	public ProductShareStatusResult Process(ProductShareStatusInput inputParam,MDataMap mRequestMap) {

		ProductShareStatusResult result = new ProductShareStatusResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

			MDataMap mWhereMap = new MDataMap();

			mWhereMap.put("app_code", getManageCode());

			mWhereMap.put("operater_code", getUserCode());

			mWhereMap.put("operate_type", "4497464900030004");

			mWhereMap.put("info_code", inputParam.getSku_code());

			mWhereMap.put("flag", "0");
			if (StringUtils.isNotEmpty(inputParam.getEnd_time())) {
				mWhereMap.put("end_time", inputParam.getEnd_time());
			}

			MPageData mPageData = DataPaging.upPageData("nc_post_operate", "","", mWhereMap, new PageOption());

			if (mPageData != null) {

				if (mPageData.getListData().size() == 0) {

					result.setStatus("0");
				} else {
					result.setStatus("1");
				}
			} else {

				result.setStatus("0");
			}

		}

		return result;
	}

}
