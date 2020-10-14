package com.cmall.membercenter.api;

import com.cmall.membercenter.model.ValiOriginalPasswordInput;
import com.cmall.membercenter.support.MemberInfoSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 验证原始密码是否正确
 * 
 * @author wangzx
 * 
 */
public class ValiOriginalPassword extends
		RootApiForToken<RootResultWeb, ValiOriginalPasswordInput> {

	public RootResultWeb Process(ValiOriginalPasswordInput inputParam,
			MDataMap mRequestMap) {
		MemberInfoSupport memberInfoSupport = new MemberInfoSupport();
		return memberInfoSupport.valiOriginalPassword(getUserCode(), inputParam);
	}

}
