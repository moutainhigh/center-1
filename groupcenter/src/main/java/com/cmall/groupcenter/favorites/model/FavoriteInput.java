package com.cmall.groupcenter.favorites.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 收藏输入类
 * @author 
 *
 */
public class FavoriteInput extends RootInput{
	@ZapcomApi(value = "收藏编号",require=0, remark = "JL123456")
	private String cId = "";
	
	@ZapcomApi(value = "是否全部清空", require=1,remark = "0为删除某一个，1为清空全部，默认为0")
	private int isEmpty = 0;
	

	public String getcId() {
		return cId;
	}

	public void setcId(String cId) {
		this.cId = cId;
	}

	public int getIsEmpty() {
		return isEmpty;
	}

	public void setIsEmpty(int isEmpty) {
		this.isEmpty = isEmpty;
	}
	
}
