package com.cmall.groupcenter.mlg.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetSingleOrderInfoForMLGInput extends RootInput{
	
	@ZapcomApi(value = "执行命令",remark = "值为：query，表示 查询订单状态",require=1)
	private String handle = "query";
	
	@ZapcomApi(value = "订单号",remark = "",require=1)
	private String order_id = "";

	@ZapcomApi(value = "签名",remark = "query |开始时间| 结束时间| 显示数最|页码|密钥， 拼接后 md5 生成签名",require=1)
	private String signature = "";

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
	

}
