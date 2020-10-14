package com.cmall.groupcenter.alternickname.api;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.alternickname.model.AlterNickNameOneInput;
import com.cmall.groupcenter.alternickname.model.AlterNickNameOneResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

public class ApiAlterNickNameOne extends RootApiForToken<AlterNickNameOneResult, AlterNickNameOneInput> {

	@Override
	public AlterNickNameOneResult Process(AlterNickNameOneInput inputParam, MDataMap mRequestMap) {
		AlterNickNameOneResult result = new AlterNickNameOneResult();
		String member_code_wo = inputParam.getMember_code_wo();
		String member_code_ta = inputParam.getMember_code_ta();
		String app_code = getManageCode();
		MDataMap insertMap = new MDataMap();
		insertMap.put("account_code_wo", getAccountCode(member_code_wo));
		insertMap.put("account_code_ta", getAccountCode(member_code_ta));
		insertMap.put("app_code", app_code);
		List<MDataMap> wMDataMap = DbUp.upTable("gc_alter_nickname").queryAll("", "", "", insertMap);
		if(wMDataMap==null||wMDataMap.size()==0||wMDataMap.get(0)==null||StringUtils.isEmpty(wMDataMap.get(0).get("nick_name"))) {
			result.setNickname("");
		} else {
			result.setResultCode(1);
			result.setNickname(wMDataMap.get(0).get("nick_name"));
		}
		return result;
	}
	/**
	 * 根据memberCode获取account_code
	 * @param memberCode
	 * @return
	 */
	private String getAccountCode(String memberCode) {
		MDataMap maDataMap = new MDataMap();
		maDataMap.put("memberCode", memberCode);
		Object object = DbUp.upTable("mc_member_info").dataGet("account_code", "member_code=:memberCode", maDataMap);
		return String.valueOf(object);
	}
}
