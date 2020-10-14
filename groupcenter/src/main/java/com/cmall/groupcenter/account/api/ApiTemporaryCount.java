package com.cmall.groupcenter.account.api;

import org.apache.commons.logging.LogFactory;

import com.cmall.groupcenter.account.model.ApiTemporaryCountResult;
import com.cmall.groupcenter.account.model.TemporaryCountInput;
import com.cmall.groupcenter.util.StringHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForVersion;

public class ApiTemporaryCount extends RootApiForVersion<ApiTemporaryCountResult, TemporaryCountInput>{

	public ApiTemporaryCountResult Process(TemporaryCountInput inputParam,
			MDataMap mRequestMap) {
		ApiTemporaryCountResult rootResultWeb=new ApiTemporaryCountResult();
		try {
			DbUp.upTable("lc_count_data").insert("content",StringHelper.deleteEmoji(inputParam.getDataString()),"manage_code",getManageCode(),"create_time",FormatHelper.upDateTime());
		} catch (Exception e) {
			LogFactory.getLog(getClass()).warn("[ApiTemporaryCountResult error !]"+inputParam.getDataString(),e);
		}
		rootResultWeb.setPeriod("30");
		return rootResultWeb;
	}

}
