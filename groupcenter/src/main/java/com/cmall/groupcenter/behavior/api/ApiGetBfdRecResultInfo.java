package com.cmall.groupcenter.behavior.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.behavior.common.StatusEnum;
import com.cmall.groupcenter.behavior.config.BfdRecResultConfig;
import com.cmall.groupcenter.behavior.request.BfdRecResultRequest;
import com.cmall.groupcenter.behavior.response.BfdRecResultResponse;
import com.cmall.groupcenter.behavior.service.BfdRecResultInfoService;
import com.cmall.groupcenter.behavior.util.BeanCompenent;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;

/**
 * 获取百分点提供的推荐结果数据
 * @author pang_jhui
 *
 */
public class ApiGetBfdRecResultInfo {
	
	/**
	 * 百分点推荐信息解析
	 * @param uid
	 * 		客户唯一号
	 * @return 百分点推荐结果响应信息
	 */
	public BfdRecResultResponse process(String uid,String iid,String operFlag,String appkey){
		return process(uid, iid, operFlag, appkey, "");
	}
	
	/**
	 * 百分点推荐信息解析
	 * @param uid
	 * 		客户唯一号
	 * @return 百分点推荐结果响应信息
	 */
	public BfdRecResultResponse process(String uid,String iid,String operFlag,String appkey, String cat){
		BfdRecResultInfoService service = new BfdRecResultInfoService();
		BfdRecResultConfig config = new BfdRecResultConfig();
		
		BfdRecResultRequest request = service.initBfdRecResultRquest(uid, iid,operFlag,appkey,config);
		request.setCat(StringUtils.trimToEmpty(cat));
		
		MDataMap mDataMap = BeanCompenent.objectTOMap(request);
		
		BfdRecResultResponse response = new BfdRecResultResponse();
		
		try {
			String returnMsg = WebClientSupport.upPost(config.getRequestPath(), mDataMap);
			List<Object> list = new ArrayList<Object>();
			JsonHelper<List<Object>> listHelper = new JsonHelper<List<Object>>();
			list = listHelper.StringToObj(returnMsg, list);
			response = service.initBfdRecResultResponse(response, list);
		} catch (Exception e) {
			response.setResultCode(StatusEnum.FAILURE.getResultCode());
			response.setResultMessage(e.getMessage());
			
		}
		
		return response;
	}

}
