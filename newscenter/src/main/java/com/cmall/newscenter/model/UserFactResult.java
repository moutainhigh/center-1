package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 用户验证真伪记录-输出类
 * @author guz
 * date 2014-11-02
 * @version 1.0
 */
public class UserFactResult extends RootResultWeb{
	
	@ZapcomApi("用户验证真伪记录")
	private List<UserFact> userFact = new ArrayList<UserFact>();

	public List<UserFact> getUserFact() {
		return userFact;
	}

	public void setUserFact(List<UserFact> userFact) {
		this.userFact = userFact;
	}

}
