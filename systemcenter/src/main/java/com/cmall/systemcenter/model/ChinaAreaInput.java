package com.cmall.systemcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ChinaAreaInput extends RootInput {
	@ZapcomApi(value = "查询省市", require = 1, remark = "输入code获得省市的信息")
	public String code = "";
	@ZapcomApi(value = "省市判断", require = 1, remark = "输入省市的code获得省市信息")
	public String area = "city";

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}
}