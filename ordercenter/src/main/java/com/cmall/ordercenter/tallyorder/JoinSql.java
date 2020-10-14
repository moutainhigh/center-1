package com.cmall.ordercenter.tallyorder;

import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.srnpr.zapcom.basemodel.MDataMap;
/**
 * sql拼接
 * @author zmm
 *
 */
public class JoinSql {
	/**
	 * 拼接订单order_code in()
	 * 
	 * @param map(key:returnCode, value:orderCode)
	 * @return
	 */
	public static String getJoinOrderCode(MDataMap map) {
		String sql = "";
		if (map != null && !map.isEmpty()) {
			Iterator<String> iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				String code = iterator.next().toString();
				if ("".equals(sql) && code != null && !"".equals(code.toString().trim())) {
						sql = " in ('" + code + "'";
				} else if (code != null && !"".equals(code.toString().trim())) {
					sql += ",'" + code + "'";
				}
			}
			if (!"".equals(sql)) {
				sql += ")";
			}
		}
		return sql;
	}
	
	public static String getJoinOrderCodeForReturn(MDataMap map) {
		String sql = "";
		if (map != null && !map.isEmpty()) {
			Iterator<String> iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				String code = iterator.next().toString();
				if ("".equals(sql) && code != null && !"".equals(code.toString().trim())) {
					String orderCode = map.get(code);
					if(StringUtils.isNotBlank(orderCode)) {
						sql = " in ('" + orderCode + "'";
					}
				} else if (code != null && !"".equals(code.toString().trim())) {
					String orderCode = map.get(code);
					sql += ",'" + orderCode + "'";
				}
			}
			if (!"".equals(sql)) {
				sql += ")";
			}
		}
		return sql;
	}
	
	public static String getJoinReturnCode(MDataMap map){
		String sql = "";
		if (map != null && !map.isEmpty()) {
			Iterator<String> iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				String returnCode = iterator.next().toString();
//				String returnCode = map.get(orderCode).split(",")[0];
				if ("".equals(sql) && returnCode != null
						&& !"".equals(returnCode.toString().trim())) {
					sql = " in ('" + returnCode + "'";
				} else if (returnCode != null && !"".equals(returnCode.toString().trim())) {
					sql += ",'" + returnCode + "'";
				}
			}
			if (!"".equals(sql)) {
				sql += ")";
			}
		}
		return sql;
	}
	
	
	public static String getJoinOrderCodeReturn(MDataMap map) {
		String sql = "";
		if (map != null && !map.isEmpty()) {
			Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, String> entry = iterator.next();
				String small_seller_code = entry.getKey().toString();
				String code = iterator.next().toString();
				if ("".equals(sql) && code != null
						&& !"".equals(code.toString().trim())) {
					sql = " in ('" + code + "'";
				} else if (code != null && !"".equals(code.toString().trim())) {
					sql += ",'" + code + "'";
				}
			}
			if (!"".equals(sql)) {
				sql += ")";
			}
		}
		return sql;
	}

	/**
	 * 拼接product_code
	 * 
	 * @param map
	 * @return
	 */
	public static String getJoinProductCode(MDataMap map) {
		String sql = "";
		if (map != null && !map.isEmpty()) {
			Iterator<String> iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				String product_code = iterator.next().toString();
				if ("".equals(sql) && product_code != null
						&& !"".equals(product_code.toString().trim())) {
					sql = " in ('" + product_code + "'";
				} else if (product_code != null
						&& !"".equals(product_code.toString().trim())) {
					sql += ",'" + product_code + "'";
				}
			}
			if (!"".equals(sql)) {
				sql += ")";
			}
		}
		return sql;
	}

	/**
	 * 拼接small_seller_code
	 * 
	 * @param map
	 * @return
	 */
	public static String getJoinSmallCode(MDataMap map) {
		String sql = "";
		if (map != null && !map.isEmpty()) {
			Iterator<String> iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				String small_seller_code = iterator.next().toString();
				if ("".equals(sql) && small_seller_code != null
						&& !"".equals(small_seller_code.toString().trim())) {
					sql = " in ('" + small_seller_code + "'";
				} else if (small_seller_code != null
						&& !"".equals(small_seller_code.toString().trim())) {
					sql += ",'" + small_seller_code + "'";
				}
			}
			if (!"".equals(sql)) {
				sql += ")";
			}
		}
		return sql;
	}
}
