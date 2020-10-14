package com.cmall.newscenter.beauty.api;

import com.cmall.newscenter.beauty.model.SaveClientInfoInput;
import com.cmall.newscenter.beauty.model.SaveClientInfoResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 惠美丽—采集数据信息Api
 * 
 * @author yangrong date: 2014-12-12
 * @version1.3.0
 */
public class SaveClientInfoApi extends RootApiForManage<SaveClientInfoResult, SaveClientInfoInput> {

	public SaveClientInfoResult Process(SaveClientInfoInput inputParam,MDataMap mRequestMap) {

		SaveClientInfoResult result = new SaveClientInfoResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

			MDataMap mInsertMap = new MDataMap();
			mInsertMap.put("info_code",  WebHelper.upCode("XX"));
			mInsertMap.put("app_code", getManageCode());
			mInsertMap.put("model", inputParam.getModel());
			mInsertMap.put("uniqid", inputParam.getUniqid());
			mInsertMap.put("mac", inputParam.getMac());
			mInsertMap.put("os", inputParam.getOs());
			mInsertMap.put("os_info", inputParam.getOs_info());
			mInsertMap.put("channel_code", inputParam.getChannel_code());
			mInsertMap.put("version_number", inputParam.getVersion_number());
			mInsertMap.put("screen", inputParam.getScreen());
			mInsertMap.put("op", inputParam.getOp());
			mInsertMap.put("net_type", inputParam.getNet_type());

			DbUp.upTable("nc_client_info").dataInsert(mInsertMap);

		}

		return result;
	}

}
