package com.cmall.groupcenter.alternickname.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class AlterNickNameListResult extends RootResultWeb {
	
	@ZapcomApi(value="备注信息",remark="备注信息")
	private List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}
}