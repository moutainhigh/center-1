package com.cmall.systemcenter.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 根据商品编号，用户编码获取优惠券信息
 * 
 * @author Angel Joy
 *
 */
public class CouponService extends BaseClass {

	/**
	 * 根据商品编号校验是否有券
	 * @param productCode
	 * @return true :有符合条件的优惠券，false ：没有符合条件的优惠券
	 */
	public boolean couponList(String productCode) {
		Map<String, Map<String, Object>> couponTypeCodesMap = new HashMap<String, Map<String, Object>>();
		Map<String, String> productCode_brandCodeMap = new HashMap<String, String>(); // 商品与品牌对应map
		List<MDataMap> brandMapList = DbUp.upTable("pc_productinfo").queryAll("product_code,brand_code", "", "product_code in ('" + productCode + "')", null);
		for (MDataMap mDataMap : brandMapList) {
			productCode_brandCodeMap.put(mDataMap.get("product_code"), mDataMap.get("brand_code"));
		}
		Map<String, String> productCode_categoryCodeMap = new HashMap<String, String>(); // 订单中的商品与分类对应map(如若为多个分类，分类中间用英文逗号隔开)
		// 优惠券类型对象map信息,第一轮筛选
		String couponCodeTypeSql = "select ot.uid uid, ot.surplus_money surplus_money, ot.limit_scope limit_scope ,ot.limit_explain limit_explain, ot.coupon_type_code coupon_type_code,ot.coupon_type_name coupon_type_name,ot.activity_code activity_code,ot.money money,ot.limit_money limit_money,ot.start_time start_time,ot.end_time end_time ,ot.money_type money_type,ot.limit_condition limit_condition from oc_coupon_type ot "
				+ "LEFT JOIN oc_activity oa ON ot.activity_code = oa.activity_code  where ot.start_time <= sysdate() and ot.end_time > sysdate() and ot.produce_type = '4497471600040001' and oa.begin_time <= sysdate() and oa.end_time > sysdate() and oa.provide_type = '4497471600060002' and oa.flag = 1 and oa.is_detail_show = '449748350002'";

		List<Map<String, Object>> couponTypeMapList = DbUp.upTable("oc_coupon_type").dataSqlList(couponCodeTypeSql,
				null);
		for (Map<String, Object> couponTypeMap : couponTypeMapList) {
			couponTypeCodesMap.put(couponTypeMap.get("coupon_type_code").toString(), couponTypeMap);
		}

		List<MDataMap> categoryMapList = DbUp.upTable("uc_sellercategory_product_relation")
				.queryAll("category_code,product_code", "", "product_code = '" + productCode + "'", null);

		for (MDataMap mDataMap : categoryMapList) {
			String categoryCode = mDataMap.get("category_code");
			if (StringUtils.isNotEmpty(productCode_categoryCodeMap.get(productCode))) {
				categoryCode += ("," + productCode_categoryCodeMap.get(productCode));
			}
			productCode_categoryCodeMap.put(productCode, categoryCode);
		}
		List<Map<String, Object>> showList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : couponTypeMapList) {
			String limit_condition = map.get("limit_condition") != null ? map.get("limit_condition").toString() : "";
			if ("4497471600070001".equals(limit_condition)) {// 4497471600070001 限制条件为无限制，不需要校验限制规则
				showList.add(map);
				continue;
			}
			MDataMap couponTypeLimit = DbUp.upTable("oc_coupon_type_limit").one("coupon_type_code",
					map.get("coupon_type_code").toString());
			String category_codes = couponTypeLimit.get("category_codes");
			String category_limit = couponTypeLimit.get("category_limit");
			String except_category = couponTypeLimit.get("except_category");
			String brand_limit = couponTypeLimit.get("brand_limit");
			String except_brand = couponTypeLimit.get("except_brand");
			String brand_codes = couponTypeLimit.get("brand_codes");
			String product_limit = couponTypeLimit.get("product_limit");
			String except_product = couponTypeLimit.get("except_product");
			String product_codes = couponTypeLimit.get("product_codes");
			boolean flagUse = true;
			// 检查品牌限制
			if (flagUse && "4497471600070002".equals(brand_limit)) {
				if ("0".equals(except_brand) && (StringUtils.isEmpty(brand_codes) || null == productCode_brandCodeMap
						|| StringUtils.isEmpty(productCode_brandCodeMap.get(productCode)))) {
					// 指定品牌限制非除外，品牌限制列表为空或者传入商品所属品牌为空时商品不可用
					flagUse = false;
				} else if ("1".equals(except_brand)
						&& (StringUtils.isEmpty(brand_codes) || null == productCode_brandCodeMap
								|| StringUtils.isEmpty(productCode_brandCodeMap.get(productCode)))) {
					// 指定品牌限制为除外，品牌限制列表为空或者传入商品所属品牌为空时商品全部可用
				} else {
					boolean limitBrand = true;
					for (String brandCodeLimit : brand_codes.split(",")) {
						if ("0".equals(except_brand)
								&& brandCodeLimit.equals(productCode_brandCodeMap.get(productCode))) {
							limitBrand = false;
						} else if ("1".equals(except_brand)
								&& brandCodeLimit.equals(productCode_brandCodeMap.get(productCode))) {
							// 指定除外限制的品牌中包含该商品，表示该商品不可使用此优惠券，结束循环
							flagUse = false;
							break;
						}
					}
					// 指定限制的品牌中不包含该商品，表示该商品不可使用此优惠券
					if (limitBrand && "0".equals(except_brand)) {
						flagUse = false;
					}
				}
			}

			// 检查商品限制
			if (flagUse && "4497471600070002".equals(product_limit)) {
				if ("0".equals(except_product) && (StringUtils.isEmpty(product_codes))) {
					// 指定商品限制非除外，商品限制列表为空时商品不可用
					flagUse = false;
				} else if ("1".equals(except_product) && StringUtils.isEmpty(product_codes)) {
					// 指定商品限制为除外，商品限制列表为空时商品全部可用
				} else {
					boolean limitProduct = true;
					for (String productCodeLimit : product_codes.split(",")) {
						if ("0".equals(except_product) && productCodeLimit.equals(productCode)) {
							limitProduct = false;
						} else if ("1".equals(except_product) && productCodeLimit.equals(productCode)) {
							// 指定除外限制的商品中包含该商品，表示该商品不可使用此优惠券，结束循环
							flagUse = false;
							break;
						}
					}
					// 指定限制的商品中不包含该商品，表示该商品不可使用此优惠券
					if (limitProduct && "0".equals(except_product)) {
						flagUse = false;
					}
				}
			}
			// 检查分类限制
			if (flagUse && "4497471600070002".equals(category_limit)) {
				if ("0".equals(except_category)
						&& (StringUtils.isEmpty(category_codes) || null == productCode_categoryCodeMap
								|| StringUtils.isEmpty(productCode_categoryCodeMap.get(productCode)))) {
					// 指定分类限制非除外，分类限制列表为空或传入商品的所属分类为空时商品不可用
					flagUse = false;
				} else if ("1".equals(except_category)
						&& (StringUtils.isEmpty(category_codes) || null == productCode_categoryCodeMap
								|| StringUtils.isEmpty(productCode_categoryCodeMap.get(productCode)))) {
					// 指定分类限制为除外，分类限制列表为空或传入商品的所属分类为空时商品全部可用
				} else {
					boolean limitCategory = true;
					for (String categoryCodeLimit : category_codes.split(",")) {
						if ("0".equals(except_category)) {
							for (String categoryCode : productCode_categoryCodeMap.get(productCode).split(",")) {
								if (categoryCodeLimit.equals(categoryCode)) {
									limitCategory = false;
									break;
								}
							}
						} else if ("1".equals(except_category)) {
							// 指定分类限制为除外，分类限制列表为空时商品全部可用
							if (StringUtils.isEmpty(category_codes)) {
								break;
							}
							for (String categoryCode : productCode_categoryCodeMap.get(productCode).split(",")) {
								if (categoryCodeLimit.equals(categoryCode)) {
									// 指定除外限制的分类中包含该商品，表示该商品不可使用此优惠券，结束循环
									flagUse = false;
									break;
								}
							}
							if (!flagUse) {
								break;
							}
						}
					}
					// 指定限制的品牌中不包含该商品，表示该商品不可使用此优惠券
					if (limitCategory && "0".equals(except_category)) {
						flagUse = false;
					}
				}
			}
			if (flagUse) {// flagUse 为true的时候，则在页面展示
				showList.add(map);
			}
		}

		if(showList.size()>0) {
			return true;
		}
		return false;
	}
}
