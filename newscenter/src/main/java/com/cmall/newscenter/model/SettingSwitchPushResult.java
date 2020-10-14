package com.cmall.newscenter.model;



import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 设置 - 推送开关输入类
 * @author liqiang
 * date 2014-7-22
 * @version 1.0
 */
public class SettingSwitchPushResult extends RootResultWeb{
	
	@ZapcomApi(value="是否有更新，是-1，否-0",demo="1,0")
	private Config config = new Config();

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	
}
