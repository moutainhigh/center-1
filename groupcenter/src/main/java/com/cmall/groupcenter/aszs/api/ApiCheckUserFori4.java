package com.cmall.groupcenter.aszs.api;

import com.cmall.groupcenter.aszs.input.ApiCheckUserFori4Input;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForMember;
import com.srnpr.zapweb.webapi.RootResultWeb;

/** 
* @ClassName: ApiForCheckUser 
* @Description: 接收爱思助手验证激活用户请求
* @author 张海生
* @date 2016-2-25 上午11:17:48 
*  
*/
public class ApiCheckUserFori4 extends RootApiForMember<RootResultWeb, ApiCheckUserFori4Input>{

	public RootResultWeb Process(ApiCheckUserFori4Input inputParam, MDataMap mRequestMap) {
		RootResultWeb result = new RootResultWeb();
		String idfa = inputParam.getIdfa();
		int count = DbUp.upTable("fh_aisi_appdownload").count("idfa",idfa);
		if(count > 0){
			return result;
		}
		MDataMap insertMap = new MDataMap();
		try {
			insertMap.put("appid", inputParam.getAppid());
			insertMap.put("mac", inputParam.getMac());
			insertMap.put("idfa", inputParam.getIdfa());
			insertMap.put("openudid", inputParam.getOpenudid());
			insertMap.put("os", inputParam.getOs());
			insertMap.put("create_time", DateUtil.getNowTime());
			DbUp.upTable("fh_aisi_appdownload").dataInsert(insertMap);//插入用户激活请求验证信息
		} catch (Exception e) {
			
		}
		return result;
	}
}
