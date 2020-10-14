package com.cmall.groupcenter.behavior.util;

import com.cmall.groupcenter.behavior.face.IBfdRequest;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;

/**
 * 实体组件
 * @author pang_jhui
 *
 */
public class BeanCompenent {
	
	/**
	 * 百分点请求信息转换
	 * @param request
	 * 		请求信息
	 * @return 百分点请求信息集合
	 */
	public static MDataMap objectTOMap(IBfdRequest request){
		
		JsonHelper<IBfdRequest> requestHelper = new JsonHelper<IBfdRequest>();
		
		String jsonStr = requestHelper.ObjToString(request);
		
		JsonHelper<MDataMap> mapHelper = new JsonHelper<MDataMap>();
		
		MDataMap mDataMap = mapHelper.StringToObj(jsonStr, new MDataMap());
		
		return mDataMap;
		
	}

}
