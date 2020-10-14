package com.cmall.newscenter.young.api;


import com.cmall.newscenter.young.model.GetJSCodeInput;
import com.cmall.newscenter.young.model.GetJSCodeResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/*  
 * 获取视频播放JS代码API
 * @author  huangs
 * 
 */
public class GetJSCodeApi extends RootApiForManage<GetJSCodeResult,GetJSCodeInput>{
 
	 
	public GetJSCodeResult Process(GetJSCodeInput inputParam, MDataMap mRequestMap) {
		GetJSCodeResult result=new GetJSCodeResult();
		if(result.upFlagTrue()){
			String  code=inputParam.getRecreation_code();
			MDataMap map = DbUp.upTable("nc_recreation").one("recreation_code",code);
			if(map!=null){
				result.setJs_code(map.get("js_code"));
			}
		}
		return result;
	}

}
