package com.cmall.groupcenter.behavior.service;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.behavior.common.StatusEnum;
import com.cmall.groupcenter.behavior.config.BfdLoginConfig;
import com.cmall.groupcenter.behavior.request.BfdLoginRequest;
import com.cmall.groupcenter.behavior.response.BfdLoginResponse;
import com.cmall.groupcenter.behavior.util.BeanCompenent;
import com.srnpr.zapcom.basemodel.MDataMap;

/**
 * 登录信息业务实现
 * @author pang_jhui
 *
 */
public class BfdLoginInfoService {
	
	/**
	 * 获取登录信息请求参数
	 * @return
	 */
	public MDataMap getLoginRquestMap(BfdLoginConfig bfdLoginConfig){
		
		BfdLoginRequest request = new BfdLoginRequest();
		
		request.setCid(bfdLoginConfig.getUserName());
		
		request.setPwd(bfdLoginConfig.getUserPwd());
		
		return BeanCompenent.objectTOMap(request);
		
	}
	
	/**
	 * 初始化百分点响应信息
	 * @param response
	 * 		响应信息
	 * @param list
	 * 		返回结果集
	 * @return 响应信息
	 */
	public BfdLoginResponse initResponse(BfdLoginResponse response, List<String> list){
		
		if(list.size() > 0){
			
			String resultCode = String.valueOf(list.get(0));
			
			// 解决GSON把0转成0.0造成对比不相等的问题
			if(new BigDecimal(resultCode).setScale(0, BigDecimal.ROUND_HALF_UP).equals(new BigDecimal(StatusEnum.SUCCESS.getCode()))){
				
				if(list.size() == 3){
					
					response.setResultCode(StatusEnum.SUCCESS.getResultCode());
					
					response.setSessionKey(list.get(2));
					
				}					
				
			}else{
				
				if(list.size() == 2){
					
					response.setResultCode(StatusEnum.FAILURE.getResultCode());
					
					response.setResultMessage(list.get(1));
					
				}
				
			}
			
		}
		
		return response;
		
		
	}

}
