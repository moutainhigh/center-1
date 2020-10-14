package com.cmall.membercenter.oauth.api;

import org.apache.commons.lang.StringUtils;

import com.cmall.membercenter.oauth.model.TokenCodeInfoInput;
import com.cmall.membercenter.oauth.model.TokenCodeInfoResult;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.websupport.OauthSupport;

@ZapcomApi(value = "获取返回地址")
public class GetTokenInfoByTokenCode extends
	RootApiForManage<TokenCodeInfoResult, TokenCodeInfoInput> {

	
	public TokenCodeInfoResult Process(TokenCodeInfoInput inputParam,
			MDataMap mRequestMap) {
		
		TokenCodeInfoResult tokenCodeInfoResult = new TokenCodeInfoResult();
		
		String backUrl = new OauthSupport().getOauthInfoByTokenCode(inputParam.getTokenCode(), getManageCode());
		if(StringUtils.isEmpty(backUrl)){
			tokenCodeInfoResult.inErrorMessage(934105142);
		}
		if(tokenCodeInfoResult.upFlagTrue()){
			tokenCodeInfoResult.setBackUrl(backUrl);
		}
		return tokenCodeInfoResult;
	}
}
