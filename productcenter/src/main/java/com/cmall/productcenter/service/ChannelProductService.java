package com.cmall.productcenter.service;

import java.math.BigDecimal;
import java.util.Map;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class ChannelProductService {
	
	
	/**
	 * 根据成本价和供货价比例计算供货价(保留一位小数向上取整)
	 * @param cost_price	商品成本价
	 * @param supply_price_proportion	供货价比例(%)
	 * @return
	 */
	public String getSupplyPrice(String cost_price, String supply_price_proportion) {
		BigDecimal supplyPrice = new BigDecimal(cost_price).multiply(new BigDecimal(supply_price_proportion)).divide(new BigDecimal("100")).add(new BigDecimal(cost_price));
		//BigDecimal setScale = supplyPrice.setScale(2, BigDecimal.ROUND_UP);
		return supplyPrice.setScale(1, BigDecimal.ROUND_UP).toString();
	}
	
	/**
	 * 根据商户编号查询商户类型
	 * @param small_seller_code		商户编号
	 * @return
	 */
	public String getSellerType(String small_seller_code) {
		String sellerType = "";
		if(null == small_seller_code || "".equals(small_seller_code)) {
			
		}else if("SI2003".equals(small_seller_code)) {
			sellerType = "LD系统";
		}else {
			String sql = "SELECT sd.define_name FROM systemcenter.sc_define sd WHERE " + 
					" sd.define_code = (SELECT uc_seller_type FROM usercenter.uc_seller_info_extend u WHERE u.small_seller_code = '"+small_seller_code+"' LIMIT 1) " + 
					" AND sd.parent_code = '449747810005'";
			Map<String, Object> map = DbUp.upTable("uc_seller_info_extend").dataSqlOne(sql, new MDataMap());
			if(map != null) {
				sellerType = (String) map.get("define_name");
			}
		}
		
		return sellerType;
	}
	
}
