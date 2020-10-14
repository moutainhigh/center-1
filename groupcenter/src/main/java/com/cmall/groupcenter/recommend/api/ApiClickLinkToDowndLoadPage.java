package com.cmall.groupcenter.recommend.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.cmall.groupcenter.recommend.model.ApiClickLinkToDowndLoadPageInput;
import com.cmall.groupcenter.recommend.model.ApiClickLinkToDowndLoadPageResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 添加推荐记录
 * @author fq
 *
 */
public class ApiClickLinkToDowndLoadPage extends RootApiForManage<ApiClickLinkToDowndLoadPageResult,ApiClickLinkToDowndLoadPageInput>{

	public ApiClickLinkToDowndLoadPageResult Process(
			ApiClickLinkToDowndLoadPageInput inputParam, MDataMap mRequestMap) {
		ApiClickLinkToDowndLoadPageResult result = new ApiClickLinkToDowndLoadPageResult();
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("uqcode",inputParam.getId());
		mDataMap.put("app_code", getManageCode());
		List<MDataMap> is_had = DbUp.upTable("gc_recommend_info").query("uqcode,app_code", "", "uqcode=:uqcode", mDataMap, 0, 1);
		//存在此推荐记录
		if(is_had.size() == 1) {
			MDataMap had = is_had.get(0);
			had.put("is_usable_send_link", "1");
			SimpleDateFormat sysDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			had.put("link_usable_time", sysDateTime.format(new Date()));
			DbUp.upTable("gc_recommend_info").dataUpdate(had,"is_usable_send_link,link_usable_time","uqcode,app_code");
		} else {
			result.setResultCode(918509001);
			result.setResultMessage(bInfo(918509001, "【"+is_had.size()+"】"));
		}
		return result;
	}
	
}
