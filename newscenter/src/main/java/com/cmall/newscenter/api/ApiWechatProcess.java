package com.cmall.newscenter.api;


import com.cmall.newscenter.model.ApiWechatProcessInput;
import com.cmall.newscenter.model.ApiWechatProcessResult;
import com.cmall.ordercenter.alipay.process.WechatProcessRequest;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootApiForMember;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 微信支付(移动端)
 * @author wz
 *
 */
public class ApiWechatProcess extends RootApiForManage<ApiWechatProcessResult, ApiWechatProcessInput>{

	public ApiWechatProcessResult Process(ApiWechatProcessInput inputParam,
			MDataMap mRequestMap) {
		ApiWechatProcessResult apiWechatProcessResult = new ApiWechatProcessResult();
		RootResult rootResult = new RootResult();
		
		String ip = inputParam.getIp();
		String orderCode = inputParam.getOrderCode();
		
		WechatProcessRequest wechatProcess = new WechatProcessRequest();
		MDataMap mDataMap =  wechatProcess.wechatMove(orderCode, ip,rootResult);    //获取微信支付所需参数
		
		if(mDataMap!=null && !"".equals(mDataMap) && mDataMap.size()>0){
			apiWechatProcessResult.setAppid(mDataMap.get("appid"));
			apiWechatProcessResult.setNoncestr(mDataMap.get("noncestr"));
			apiWechatProcessResult.setPackageValue(mDataMap.get("package"));
			apiWechatProcessResult.setPartnerid(mDataMap.get("partnerid"));
			apiWechatProcessResult.setPrepayid(mDataMap.get("prepayid"));
			apiWechatProcessResult.setSign(mDataMap.get("sign"));
			apiWechatProcessResult.setTimestamp(mDataMap.get("timestamp"));
		}
		
		apiWechatProcessResult.setResultCode(rootResult.getResultCode());
		apiWechatProcessResult.setResultMessage(rootResult.getResultMessage());
		
		
		return apiWechatProcessResult;
	}

}
