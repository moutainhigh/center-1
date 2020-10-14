package com.cmall.newscenter.api;

import com.cmall.newscenter.model.SettingCheckUpdateInput;
import com.cmall.newscenter.model.SettingCheckUpdateResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;
/**
 * 设置 - 检查更新
 * @author liqiang
 * date 2014-7-22
 * @version 1.0
 */
public class SettingCheckUpdateApi extends RootApiForManage<SettingCheckUpdateResult, SettingCheckUpdateInput> {

	public SettingCheckUpdateResult Process(SettingCheckUpdateInput inputParam,
			MDataMap mRequestMap) {
		
		SettingCheckUpdateResult result = new SettingCheckUpdateResult();
		
		if(result.upFlagTrue()){
			
			/*版本号*/
			String sVersion = bConfig("newscenter.app_version");
			
			String version = inputParam.getVer();
			
			String has_new = "";
			
			/*url*/
			String url = "";
			
			if(sVersion.toString().trim().compareTo(version)<=0){
				
				has_new = "0";
				
			}else{
				if(inputParam.getPlatform().equals("ios")){
				
					url=bConfig("newscenter.app_iosUrl");
					
				}else {
					
					url=bConfig("newscenter.app_androidUrl");
				}
				
				has_new = "1";
				
			}
			
			
			/*新增功能*/
			String brief = bConfig("newscenter.app_brief");
			
			result.setBrief(brief);
			
			result.setHas_new(has_new);
			
			result.setUrl(url);
			
			result.setVersion(sVersion);
			
		}
		return result;
	}

}
