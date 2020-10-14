package com.cmall.membercenter.oauth.api;

import com.cmall.membercenter.oauth.model.TokenCodeInput;
import com.cmall.membercenter.oauth.model.TokenCodeResult;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.websupport.OauthSupport;

@ZapcomApi(value = "获取临时令牌")
public class RsyncTokenCode extends
		RootApiForManage<TokenCodeResult, TokenCodeInput> {

	public TokenCodeResult Process(TokenCodeInput inputParam,
			MDataMap mRequestMap) {
		TokenCodeResult tokenCodeResult = new TokenCodeResult();

		tokenCodeResult.setTokenCode(new OauthSupport().createTokenCode(
				inputParam.getBackUrl(), getManageCode(), "1d"));

		tokenCodeResult.setCallUrl(FormatHelper.formatString(
				bConfig("membercenter.oauth_call_url"),
				tokenCodeResult.getTokenCode()));

		return tokenCodeResult;
	}

}
