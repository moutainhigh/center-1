package com.cmall.groupcenter.account.api;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;

/**
 * 
 * @author xiegj
 * 校验状态和库存
 */
public class ApiForCheckStockAndStatusResult extends RootResult {

	@ZapcomApi(value = "List<MDataMap>", remark = "flagSell:商品是否在售  0:下架，1:上架; flagStock:库存是否足够  0:库存不足，1:库存足够;limitNum:限购数量;")
	private List<MDataMap> list = new ArrayList<MDataMap>();

	public List<MDataMap> getList() {
		return list;
	}

	public void setList(List<MDataMap> list) {
		this.list = list;
	}
	
	@ZapcomApi(value = "是否内购",remark = "1：内购。0：不是内购" ,demo= "0")
	String flag = "0";

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
}
