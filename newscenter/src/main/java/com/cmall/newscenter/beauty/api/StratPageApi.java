package com.cmall.newscenter.beauty.api;

import com.cmall.newscenter.beauty.model.StratPage;
import com.cmall.newscenter.beauty.model.StratPageInput;
import com.cmall.newscenter.beauty.model.StratPageResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 惠美丽—启动页Api
 * 
 * @author yangrong date: 2014-09-10
 * @version1.0
 */
public class StratPageApi extends RootApiForManage<StratPageResult, StratPageInput> {

	public StratPageResult Process(StratPageInput inputParam,MDataMap mRequestMap) {

		StratPageResult result = new StratPageResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

			// 查询启动页信息
			MDataMap mWhereMap = new MDataMap();

			mWhereMap.put("app_code", getManageCode());

			MPageData mPageData = DataPaging.upPageData("nc_startpage", "", "",mWhereMap, new PageOption());

			if (mPageData != null) {

				for (MDataMap mDataMap : mPageData.getListData()) {

					StratPage stratPage = new StratPage();

					stratPage.setUrl(mDataMap.get("photo_url"));
					stratPage.setStart_time(mDataMap.get("start_time"));
					stratPage.setEnd_time(mDataMap.get("end_time"));

					result.getStratPage().add(stratPage);
				}
			}

		}
		return result;
	}

}
