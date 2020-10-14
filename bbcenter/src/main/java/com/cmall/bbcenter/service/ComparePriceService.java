package com.cmall.bbcenter.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.dbsupport.DbTemplate;

public class ComparePriceService {
	public List<Map<String, Object>> getProductHistoryPrice(String productCode,
			String productName) {
		DbTemplate dt = DbUp.upTable("bc_purchase_detail").upTemplate();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("goods_code", productCode);
		paramMap.put("goods_name", productName);
		if (StringUtils.isNotBlank(productCode)
				&& StringUtils.isNotBlank(productName)) {
			return dt
					.queryForList(
							"select purchaseorder_code,goods_name,goods_code,goods_price,goods_number,create_time from bc_purchase_detail where goods_code = :goods_code and goods_name = :goods_name",
							paramMap);
		}

		if (StringUtils.isNotBlank(productCode)) {
			return dt
					.queryForList(
							"select purchaseorder_code,goods_name,goods_code,goods_price,goods_number,create_time from bc_purchase_detail where goods_code = :goods_code",
							paramMap);
		}

		if (StringUtils.isNotBlank(productName)) {
			return dt
					.queryForList(
							"select purchaseorder_code,goods_name,goods_code,goods_price,goods_number,create_time from bc_purchase_detail where goods_name = :goods_name",
							paramMap);
		}
		return dt
				.queryForList(
						"select purchaseorder_code,goods_name,goods_code,goods_price,goods_number,create_time from bc_purchase_detail",
						paramMap);
	}
}
