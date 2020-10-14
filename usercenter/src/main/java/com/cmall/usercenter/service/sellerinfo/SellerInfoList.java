package com.cmall.usercenter.service.sellerinfo;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.topapi.RootResult;

public class SellerInfoList extends RootResult
{
	private List<SellerInfo> list = new ArrayList<SellerInfo>();

	public List<SellerInfo> getList()
	{
		return list;
	}

	public void setList(List<SellerInfo> list)
	{
		this.list = list;
	}
	
	
    
}
