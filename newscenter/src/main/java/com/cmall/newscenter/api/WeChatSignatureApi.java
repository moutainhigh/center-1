package com.cmall.newscenter.api;

import java.util.Date;

import com.cmall.newscenter.model.WeChatSignatureResult;
import com.cmall.newscenter.model.WeChatsSignatureInput;
import com.cmall.newscenter.util.PastUtil;
import com.srnpr.zapcom.basehelper.SecrurityHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 微信签名
 * 
 * @author shiyz date 2016-03-21
 * 
 */
public class WeChatSignatureApi extends
		RootApiForManage<WeChatSignatureResult, WeChatsSignatureInput> {

	public WeChatSignatureResult Process(WeChatsSignatureInput inputParam,
			MDataMap mRequestMap) {

		WeChatSignatureResult result = new WeChatSignatureResult();

		/*
		 * if (result.upFlagTrue()) {
		 * 
		 * MDataMap map =
		 * PastUtil.getParam(bConfig("newscenter.app_id"),bConfig(
		 * "newscenter.app_secret"),inputParam.getUrl());
		 * 
		 * if(map!=null && !"".equals(map)){ result.setExpires_in(7200);
		 * result.setNonceStr(map.get("nonceStr"));
		 * result.setSignature(map.get("signature"));
		 * result.setTimestamp(map.get("timestamp"));
		 * result.setApp_id(map.get("appid"));
		 * 
		 * } }
		 */

		result.setJsApiList(new String[] { "scanQRCode" });
		result.setAppId(bConfig("newscenter.app_id"));
		result.setNonceStr(WebHelper.upUuid());
		result.setTimestamp(String.valueOf(new Date().getTime()));

		String sTicketString = PastUtil.getJsApiTicket(
				bConfig("newscenter.app_id"), bConfig("newscenter.app_secret"));

		String sSource = "jsapi_ticket=" + sTicketString + "&noncestr="
				+ result.getNonceStr() + "&timestamp=" + result.getTimestamp()
				+ "&url=" + inputParam.getUrl();

		result.setSignature(SecrurityHelper.sha1(sSource));

		return result;
	}
}
