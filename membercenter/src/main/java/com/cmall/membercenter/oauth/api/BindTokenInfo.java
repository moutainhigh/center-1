package com.cmall.membercenter.oauth.api;

import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.membercenter.oauth.model.BindTokenInfoInput;
import com.cmall.membercenter.oauth.model.BindTokenInfoResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webmodel.MOauthInfo;
import com.srnpr.zapweb.webmodel.MOauthScope;
import com.srnpr.zapweb.websupport.OauthSupport;

public class BindTokenInfo extends
		RootApiForToken<BindTokenInfoResult, BindTokenInfoInput> {

	public BindTokenInfoResult Process(BindTokenInfoInput inputParam,
			MDataMap mRequestMap) {

		BindTokenInfoResult result = new BindTokenInfoResult();

		if (result.upFlagTrue()) {

			MDataMap mDataMap = DbUp
					.upTable("za_ocode")
					.oneWhere(
							"",
							"",
							"token_code=:token_code and flag_enable=1 and expires_time>=:expires_time",
							"token_code", inputParam.getTokenCode(),
							"expires_time", FormatHelper.upDateTime());

			if (mDataMap != null && mDataMap.size() > 0) {
				
				
				 MOauthInfo mOauthInfo= getOauthInfo();
				
				OauthSupport oauthSupport = new OauthSupport();

				// 设置授权类型
				MOauthScope mOauthScope = new MOauthScope();

				mOauthScope.setManageCode(getManageCode());
				mOauthScope.setScopeType("oauth");

				String sAccessToken=oauthSupport.insertOauth(
						getUserCode(), getManageCode(),
						mOauthInfo.getLoginName(),
						MemberConst.OAUTH_EXPIRESS_TIME,
						oauthSupport.scopeToJson(mOauthScope));
				
				
				result.setBackUrl(mDataMap.get(""));
				

			} else {
				result.inErrorMessage(969905917);
			}

		}

		return result;

	}

}
