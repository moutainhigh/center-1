package com.cmall.productcenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiCheckRepeatSkuProResult  extends RootResult{
	
	@ZapcomApi(value="是否已经存在所选sku属性",remark="0:不重复,1:sku属性编号重复,2:sku属性名称重复")
	private int flag = 0;

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
}
