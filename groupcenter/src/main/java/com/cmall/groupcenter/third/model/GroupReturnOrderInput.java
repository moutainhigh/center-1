package com.cmall.groupcenter.third.model; 

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class GroupReturnOrderInput extends RootInput{

	@ZapcomApi(value = "退货订单编号",remark="微公社创建订单时返回订单编号",demo = "OC141127100121", require = 1)
	String orderCode="";
	
	@ZapcomApi(value = "退货明细列表",remark="微公社退货明细列表",demo = "", require = 1)
	List<GroupReturnOrderDetail> detailList=new ArrayList<GroupReturnOrderDetail>();

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public List<GroupReturnOrderDetail> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<GroupReturnOrderDetail> detailList) {
		this.detailList = detailList;
	}

}
