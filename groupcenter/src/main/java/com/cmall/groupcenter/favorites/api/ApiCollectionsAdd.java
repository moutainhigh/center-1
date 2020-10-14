package com.cmall.groupcenter.favorites.api;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.favorites.model.ApiCollectionsAddInput;
import com.cmall.groupcenter.favorites.model.ApiCollectionsAddResult;
import com.cmall.groupcenter.recommend.model.ApiChangePostCollectBrowseAndShareNumInput;
import com.cmall.groupcenter.recommend.model.ApiChangePostCollectBrowseAndShareNumResult;
import com.cmall.groupcenter.txservice.TxNcPostChangeService;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 添加帖子收藏接口
 * 
 * @author guz
 *
 */
public class ApiCollectionsAdd extends
		RootApiForToken<ApiCollectionsAddResult, ApiCollectionsAddInput> {

	public ApiCollectionsAddResult Process(ApiCollectionsAddInput inputParam,
			MDataMap mRequestMap) {
		ApiCollectionsAddResult result = new ApiCollectionsAddResult();
		//曾经的收藏状态
		String everCollectFlag = "";

		MDataMap memberInfo = DbUp.upTable("mc_member_info").oneWhere(
				"account_code", "", "", "member_code", getUserCode());
		if (memberInfo == null) {
			result.inErrorMessage(915805334);
		}

		// 锁定账户编号
		String sLockCode = WebHelper.addLock(100,
				memberInfo.get("account_code"));

		if (!StringUtils.isEmpty(sLockCode)) {
			if (result.upFlagTrue()) {
				MDataMap ptData = DbUp.upTable("nc_post").one("pid",
						inputParam.getPost_id());
				if (ptData == null) {
					result.inErrorMessage(918519016);
				} else {
					MDataMap dataMap = null;
					MDataMap ncMap = DbUp.upTable("nc_collections").one(
							"post_id", inputParam.getPost_id(), "member_code",
							getUserCode(), "app_code", getManageCode());
					if (null != ncMap) {
						everCollectFlag = ncMap.get("flag");
						dataMap = new MDataMap();
						dataMap.put("uid", ncMap.get("uid"));
						dataMap.put("flag", inputParam.getFlag());
						dataMap.put("collection_time", DateHelper.upNow());
						try {
							DbUp.upTable("nc_collections").dataUpdate(dataMap,
									"flag,collection_time", "uid");
						} catch (Exception e) {
							result.setResultCode(-1);
							result.setResultMessage("收藏失败！");
						}
					} else {// insert
						dataMap = new MDataMap();
						if (!StringUtils.isEmpty(ptData.get("pid"))) {
							dataMap.put("post_id", ptData.get("pid"));
						}
						if (!StringUtils.isEmpty(ptData.get("p_title"))) {
							dataMap.put("post_title", ptData.get("p_title"));
						}
						if (!StringUtils.isEmpty(ptData.get("img_url"))) {
							dataMap.put("img_url", ptData.get("img_url"));
						}
						dataMap.put("flag", inputParam.getFlag());
						dataMap.put("app_code", getManageCode());
						dataMap.put("member_code", getUserCode());
						dataMap.put("collection_id", WebHelper.upCode("SC"));
						dataMap.put("collection_time", DateHelper.upNow());
						try {
							DbUp.upTable("nc_collections").dataInsert(dataMap);
						} catch (Exception e) {
							result.setResultCode(-1);
							result.setResultMessage("收藏失败！");
						}
					}
				}
			}
			// 解鎖
			WebHelper.unLock(sLockCode);
			
			if (result.upFlagTrue()) {
				result.setFlag(inputParam.getFlag());

				// 更新帖子收藏数量 @gaozx
				TxNcPostChangeService txNcPostChangeService = BeansHelper
						.upBean("bean_com_cmall_groupcenter_txservice_TxNcPostChangeService");
				ApiChangePostCollectBrowseAndShareNumInput collectNuminputParam = new ApiChangePostCollectBrowseAndShareNumInput();
				collectNuminputParam.setChangeCollectNum("0");
				//4497472000020001可用4497472000020002移除
				//曾经的是收藏，现在是移除
				if(everCollectFlag.equals("4497472000020001") && inputParam.getFlag().equals("4497472000020002")) {
					collectNuminputParam.setChangeCollectNum("-1");
				//曾经是移除或者空，现在是收藏
				} else if((everCollectFlag.equals("4497472000020002") || everCollectFlag.equals("")) 
						&& inputParam.getFlag().equals("4497472000020001")) {
					collectNuminputParam.setChangeCollectNum("1");
				}
				collectNuminputParam.setPid(inputParam.getPost_id());
				ApiChangePostCollectBrowseAndShareNumResult changeNumresult =
						txNcPostChangeService.changeNcPostSomeNum(collectNuminputParam,
						getManageCode());
				result.setCollectNum(changeNumresult.getChangedCollectNum() + "");
			}
		}
		return result;
	}
}
