package com.cmall.usercenter.model.api;

import java.util.ArrayList;
import java.util.List;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;

/**
 * ClassName:部门下用户<br/>
 * Date:     2013-10-21 下午1:34:42 <br/>
 * @author   jack
 * @version  1.0
 */
public class UserOrganizationResult extends RootResult {
	private List<MDataMap> list =new ArrayList<MDataMap>();

	/**
	 * 获取list.
	 * @return  list
	 */
	public List<MDataMap> getList() {
		return list;
	}

	/**
	 * 设置list.
	 * @param   list
	 */
	public void setList(List<MDataMap> list) {
		this.list = list;
	}

}

