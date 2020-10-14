package com.cmall.newscenter.beauty.api;

import org.apache.commons.lang.StringUtils;

import com.cmall.newscenter.beauty.model.ProductShareInput;
import com.cmall.newscenter.beauty.model.ProductShareResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 试用商品_分享Api
 * 
 * @author yangrong date: 2014-09-23
 * @version1.0
 */
public class ProductShareApi extends RootApiForToken<ProductShareResult, ProductShareInput> {

	public ProductShareResult Process(ProductShareInput inputParam,MDataMap mRequestMap) {

		ProductShareResult result = new ProductShareResult();
		Boolean flag = true;

		// 设置相关信息
		if (result.upFlagTrue()) {

			MDataMap mWhereMap = new MDataMap();

			mWhereMap.put("app_code", getManageCode());

			mWhereMap.put("operater_code", getUserCode());

			mWhereMap.put("info_code", inputParam.getSku_code());

			mWhereMap.put("share_type", inputParam.getShare_type());

			mWhereMap.put("operate_type", "4497464900030004");

			if (StringUtils.isNotEmpty(inputParam.getEnd_time())) {
				mWhereMap.put("end_time", inputParam.getEnd_time());
			}

			mWhereMap.put("flag", "0");

			MPageData mPageData = DataPaging.upPageData("nc_post_operate", "",
					"", mWhereMap, new PageOption());

			if (mPageData != null) {

				if (mPageData.getListData().size() != 0) {
					result.setResultCode(934205151);
					result.setResultMessage(bInfo(934205151));
					flag = false;
				}
			}
			// 没有分享过才做记录
			if (flag) {

				MDataMap mInsertMap = new MDataMap();

				mInsertMap.put("info_code", inputParam.getSku_code());

				if (!"".equals(inputParam.getShare_type()) || null != inputParam.getShare_type()) {

					mInsertMap.put("share_type", inputParam.getShare_type());
				}

				mInsertMap.put("operate_type", "4497464900030004");

				mInsertMap.put("flag", "0");

				mInsertMap.put("app_code", getManageCode());

				mInsertMap.put("operater_code", getUserCode());

				mInsertMap.put("end_time", inputParam.getEnd_time());

				DbUp.upTable("nc_post_operate").dataInsert(mInsertMap);
			}

		}

		return result;
	}

}
