package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * @author srnpr 关联账号信息
 */
public class MemberOther {

	@ZapcomApi(value = "名称")
	private String name = "";
	@ZapcomApi(value = "类型")
	private String type = "";
	@ZapcomApi(value = "APPID")
	private String app_id = "";
	@ZapcomApi(value = "APPKEY")
	private String app_key = "";
	@ZapcomApi(value = "过期时间")
	private String expires = "";

}
