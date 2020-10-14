package com.cmall.groupcenter.job;

import java.util.List;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.weixin.WebchatConstants;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/***
 * 开发平台beta环境同步appKey
 * 
 * @author panwei
 *
 */
public class JobWopenSyncAppKey extends RootJob {

	
	public void doExecute(JobExecutionContext context) {
		
		List<MDataMap> appList=DbUp.upTable("gc_wopen_appmanage").query("", "", "app_code not in (select manage_code from zapdata.za_apiauthorize)", 
				new MDataMap(), 0, 0);
		for(MDataMap app:appList){
			DbUp.upTable("za_apiauthorize").
			insert("api_key",app.get("test_apikey"),
				"api_pass",app.get("test_apipassword"),
				"api_able","com",
				"remark",app.get("app_name"),
				"api_roles","469923200004,469923200005",
				"manage_code",app.get("app_code"));
		}
	}
}
