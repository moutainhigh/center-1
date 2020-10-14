package com.cmall.groupcenter.oauth.api;

import com.cmall.groupcenter.account.api.ApiCreateRelation;
import com.cmall.groupcenter.account.model.CreateRelationInput;
import com.cmall.groupcenter.oauth.model.SetParentMemberInput;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapweb.webmodel.MOauthInfo;
import com.srnpr.zapweb.webmodel.MWebResult;
import com.srnpr.zapweb.websupport.OauthSupport;

public class SetParentMember extends
		RootApiForManage<RootResultWeb, SetParentMemberInput> {

	public RootResultWeb Process(SetParentMemberInput inputParam,
			MDataMap mRequestMap) {

		RootResultWeb result = new RootResultWeb();

		if (result.upFlagTrue()) {

			OauthSupport oauthSupport = new OauthSupport();

			MOauthInfo mOauthInfo = oauthSupport.upOauthInfo(inputParam
					.getAccessToken());

			if (mOauthInfo != null) {

				ApiCreateRelation apiCreateRelation = new ApiCreateRelation();
				CreateRelationInput createRelationInput = new CreateRelationInput();

				createRelationInput.setCreateTime(FormatHelper.upDateTime());
				createRelationInput.setLoginName(mOauthInfo.getLoginName());
				createRelationInput.setParentLoginName(inputParam
						.getParentLoginName());

				result = apiCreateRelation.Process(createRelationInput,
						mRequestMap);

			} else {
				result.inErrorMessage(969905917);
			}

		}
		
		return result;

	}

}
