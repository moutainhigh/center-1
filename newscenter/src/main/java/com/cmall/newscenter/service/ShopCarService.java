package com.cmall.newscenter.service;


import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapdata.dbdo.DbUp;

public class ShopCarService extends BaseClass {


	
	/**
	 *删除购物车中的商品 
	 * 
	 */
	public boolean deleteSkuForShopCart(String buyer_code,String skuCode){
		boolean flag = false;
		try {
			if(buyer_code!=null&&skuCode!=null&&!"".equals(skuCode)&&!"".equals(buyer_code)){
				DbUp.upTable("oc_shopCart").delete("buyer_code",buyer_code,"sku_code",skuCode);
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
}
