package com.cmall.groupcenter.behavior.api;

import java.util.ArrayList;
import java.util.List;
import com.cmall.groupcenter.behavior.common.StatusEnum;
import com.cmall.groupcenter.behavior.config.BfdLoginConfig;
import com.cmall.groupcenter.behavior.response.BfdLoginResponse;
import com.cmall.groupcenter.behavior.service.BfdLoginInfoService;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basesupport.WebClientSupport;

/**
 * 百分点登陆信息
 * @author pang_jhui
 *
 */
public class ApiBfdLoginInfo {
	
	/**
	 * 百分点解析登录信息
	 * @return
	 */
	public BfdLoginResponse process() {

		BfdLoginConfig bfdLoginConfig = new BfdLoginConfig();
		
		BfdLoginResponse response = new BfdLoginResponse();

		BfdLoginInfoService loginInfoService = new BfdLoginInfoService();

		try {
			
			String returnStr = WebClientSupport.upPost(bfdLoginConfig.getRequestUrl(),
					loginInfoService.getLoginRquestMap(bfdLoginConfig));
			
			List<String> list = new ArrayList<String>();
			
			JsonHelper<List<String>> listHelper = new JsonHelper<List<String>>();
			
			list = listHelper.StringToObj(returnStr, list);
			
			response = loginInfoService.initResponse(response, list);
			
			
		} catch (Exception e) {
			
			response.setResultCode(StatusEnum.FAILURE.getResultCode());
			
			response.setResultMessage(e.getMessage());
			
		}
		
		return response;

	}

}
