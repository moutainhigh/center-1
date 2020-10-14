package com.cmall.groupcenter.mlg.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetOrderInfoForMLGInput extends RootInput{
	
	@ZapcomApi(value = "执行命令",remark = "值为：query，表示 查询订单状态",require=1)
	private String handle = "query";
	
	@ZapcomApi(value = "开始时间",remark = "格式为：yyyyMMddhhmmss",require=1)
	private String starttime = "";
	
	@ZapcomApi(value = "结束时间",remark = "格式为：yyyyMMddhhmmss",require=1)
	private String endtime = "";

	@ZapcomApi(value = "显示数量",remark = "每次显示条数",require=1)
	private int num = 10;
	
	@ZapcomApi(value = "页码",remark = "当前页码",require=1)
	private int page = 1;
	
	@ZapcomApi(value = "签名",remark = "query |开始时间| 结束时间| 显示数最|页码|密钥， 拼接后 md5 生成签名",require=1)
	private String signature = "";

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
	

}
