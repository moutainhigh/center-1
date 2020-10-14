package com.cmall.newscenter.service;

import java.util.List;


import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.dbface.ITxService;

public class ProductCommentService extends BaseClass implements ITxService {

/**
 * 判断某个用户是否评论过某件商品
 * @param userCode
 * @param skuCode
 * @param appCode
 * @param orderCode
 * @return
 */
	public boolean isCommented(String orderCode,String userCode,String skuCode,String appCode){
		
		MDataMap mWhereMap = new MDataMap();
		mWhereMap.put("order_skuid",skuCode);
		mWhereMap.put("order_name",userCode);
		mWhereMap.put("manage_code", appCode);
		mWhereMap.put("order_code", orderCode);
		List<MDataMap> list =DbUp.upTable("nc_order_evaluation").queryAll("", "", "", mWhereMap);

		if(list.size()!=0){
			return true;
		}else {
			return false;
		}
	}
	

}
