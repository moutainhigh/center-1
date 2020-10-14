package com.cmall.groupcenter.alternickname.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.groupcenter.alternickname.model.AlterNickNameListInput;
import com.cmall.groupcenter.alternickname.model.AlterNickNameListResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

public class ApiAlterNickNameList extends RootApiForToken<AlterNickNameListResult, AlterNickNameListInput> {

	@Override
	public AlterNickNameListResult Process(AlterNickNameListInput inputParam, MDataMap mRequestMap) {
		AlterNickNameListResult result = new AlterNickNameListResult();
		String account_code_wo = inputParam.getAccount_code_wo();
		List<String> account_code_ta = inputParam.getAccount_code_ta();
		result.setResultCode(1);
		if(account_code_ta==null || account_code_ta.size()==0) {
			result.setList(new ArrayList<Map<String,Object>>());
			return result;
		}
		String account_code_ta_list = "";//好友列表的account_code
		for(int index=0; index<account_code_ta.size(); index++) {
			account_code_ta_list += ("'"+account_code_ta.get(index)+"'"+",");
		}
		account_code_ta_list = account_code_ta_list.substring(0, account_code_ta_list.lastIndexOf(","));
		List<Map<String, Object>> listMaps = DbUp.upTable("gc_alter_nickname").dataSqlList("select nick_name,account_code_ta from gc_alter_nickname where account_code_wo='"+account_code_wo+"' and account_code_ta in("+account_code_ta_list+")", new MDataMap());
		result.setList(listMaps);
		return result;
	}
}