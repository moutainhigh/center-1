package com.cmall.groupcenter.alternickname.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.cmall.groupcenter.alternickname.model.AlterNickNameInput;
import com.cmall.groupcenter.alternickname.model.AlterNickNameResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

public class ApiAlterNickNameFinish extends RootApiForToken<AlterNickNameResult, AlterNickNameInput> {
	
	public AlterNickNameResult Process(AlterNickNameInput inputParam, MDataMap mRequestMap) {
		AlterNickNameResult result = new AlterNickNameResult();
		String app_code = getManageCode();
		String nick_name = inputParam.getNick_name();
		String member_code_wo = inputParam.getMember_code_wo();
		String member_code_ta = inputParam.getMember_code_ta();
		
		String account_code_wo = getAccountCode(member_code_wo);
		String account_code_ta = getAccountCode(member_code_ta);
		
		MDataMap insertMap = new MDataMap();
		insertMap.put("account_code_wo", account_code_wo);
		insertMap.put("account_code_ta", account_code_ta);
		insertMap.put("app_code", app_code);
		int num = DbUp.upTable("gc_alter_nickname").count("account_code_wo", account_code_wo, "account_code_ta", account_code_ta, "app_code", app_code);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(num==0) {
			insertMap.put("nick_name", nick_name);
			insertMap.put("create_time", format.format(new Date()));
			insertMap.put("update_time", format.format(new Date()));
			String uuid = DbUp.upTable("gc_alter_nickname").dataInsert(insertMap);
			Map<String, Object> oneMap = DbUp.upTable("gc_alter_nickname").dataSqlOne("select * from gc_alter_nickname where uid= '"+uuid+"'", new MDataMap());
			result.setOneMap(oneMap);
		} else {
			insertMap.put("nick_name", nick_name);
			insertMap.put("update_time", format.format(new Date()));
			DbUp.upTable("gc_alter_nickname").dataUpdate(insertMap, "nick_name,update_time", "account_code_wo,account_code_ta,app_code");
		}
		result.setResultCode(1);
		result.setResultMessage("修改成功！");
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
