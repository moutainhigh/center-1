package com.cmall.productcenter.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.service.StoreService;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuInfo;
import com.srnpr.xmassystem.support.PlusSupportProduct;
import com.srnpr.xmassystem.support.PlusSupportStock;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 商品库存
 * 
 * @author jlin
 *
 */
public class ProductStoreService extends StoreService {

	/**
	 * 根据sku_code 查询库存信息<br>
	 * 适用APP：惠家有、家有汇、惠美丽、小时代
	 * 
	 * @param sku_code
	 * @return
	 */
	public int getStockNumBySku(String sku_code) {

		// 查询sku信息
		List<Map<String, Object>> skuL = DbUp.upTable("pc_skuinfo").dataQuery(
				"seller_code,product_code", "", "sku_code=:sku_code",
				new MDataMap("sku_code", sku_code), 0, 1);
		if (skuL.size() < 1) {
			return 0;
		}
		int stockNum = 0;
		Map<String, Object> skuMap = skuL.get(0);
		String seller_code = (String) skuMap.get("seller_code");
		String product_code = (String) skuMap.get("product_code");

		if (AppConst.MANAGE_CODE_CAPP.equals(seller_code)) {// 惠美丽的库存
			return getStockNumByMaxFor7(sku_code);
		} else if (AppConst.MANAGE_CODE_CYOUNG.equals(seller_code)) {
			return getStockNumByMaxFor13(sku_code);
		}
		 //******** 这段代码是下面注掉那一大段代码的替身 begin
		else if (AppConst.MANAGE_CODE_HPOOL.equals(seller_code)){
			// 判断是否为虚拟商品
			List<Map<String, Object>> productL = DbUp.upTable("pc_productinfo")
					.dataQuery("validate_flag,seller_code,small_seller_code",
							"", "product_code=:product_code",
							new MDataMap("product_code", product_code), 0, 1);
			if (productL.size() < 1) {
				return 0;
			}
			Map<String, Object> productMap = productL.get(0);
			String validate_flag = (String) productMap.get("validate_flag");
			if ("Y".equals(validate_flag)) {// 虚拟商品 查看C18的库存
				stockNum = getStockNumByStore("C18", sku_code);
			} else {// 非虚拟商品
				// 判断是否为一地入库
				List<Map<String, Object>> extL = DbUp.upTable(
						"pc_productinfo_ext").dataQuery("prch_type,oa_site_no",
						"", "product_code=:product_code",
						new MDataMap("product_code", product_code), 0, 1);
				if (extL.size() < 1) {
					return 0;
				}
				Map<String, Object> extMap = extL.get(0);
				String prch_type = (String) extMap.get("prch_type");
				String oa_site_no = (String) extMap.get("oa_site_no");

				if ("10".equals(prch_type) || "20".equals(prch_type)) {// 一地入库，
					stockNum = getStockNumByStore(oa_site_no, sku_code);
				} else if ("00".equals(prch_type)||"30".equals(prch_type)) {// 四地入库，查询库存总和
					stockNum = getStockNumByStore(sku_code);
				}
			}
		}else if (AppConst.MANAGE_CODE_HOMEHAS.equals(seller_code)
				|| AppConst.MANAGE_CODE_CDOG.equals(seller_code)) {
			stockNum = new PlusSupportStock().upAllStock(sku_code); 
		}
		//下面注掉了一大段代码，因为惠家有跟沙皮狗我都掉缓存了。
//		else if (AppConst.MANAGE_CODE_HPOOL.equals(seller_code)
//				|| AppConst.MANAGE_CODE_HOMEHAS.equals(seller_code)
//				|| AppConst.MANAGE_CODE_CDOG.equals(seller_code)) {// 惠家有和家有汇的获取库存方式一致
//
//			// 判断是否为虚拟商品
//			List<Map<String, Object>> productL = DbUp.upTable("pc_productinfo")
//					.dataQuery("validate_flag,seller_code,small_seller_code",
//							"", "product_code=:product_code",
//							new MDataMap("product_code", product_code), 0, 1);
//			if (productL.size() < 1) {
//				return 0;
//			}
//
//			Map<String, Object> productMap = productL.get(0);
//			String validate_flag = (String) productMap.get("validate_flag");
//			String small_seller_code = (null == productMap
//					.get("small_seller_code")) ? "" : productMap.get(
//					"small_seller_code").toString();
//			String sellerCode = productMap.get("seller_code").toString();
//			if ("Y".equals(validate_flag)) {// 虚拟商品 查看C18的库存
//				// 第三方商户商品走TDS1库
//				if (AppConst.MANAGE_CODE_HOMEHAS.equals(sellerCode)
//						&& !AppConst.MANAGE_CODE_HOMEHAS
//								.equals(small_seller_code)) {
//					stockNum = new PlusSupportProduct()
//							.upSkuAllStockForInt(sku_code);
//				} else if (AppConst.MANAGE_CODE_CDOG.equals(sellerCode)) {		//沙皮狗走申通库存
//					stockNum = getStockNumByStore(AppConst.CDOG_STORE_CODE, sku_code);
//				}else {
//					stockNum = getStockNumByStore("C18", sku_code);
//				}
//			} else {// 非虚拟商品
//
//				// 判断是否为一地入库
//				List<Map<String, Object>> extL = DbUp.upTable(
//						"pc_productinfo_ext").dataQuery("prch_type,oa_site_no",
//						"", "product_code=:product_code",
//						new MDataMap("product_code", product_code), 0, 1);
//				if (extL.size() < 1) {
//					return 0;
//				}
//
//				Map<String, Object> extMap = extL.get(0);
//				String prch_type = (String) extMap.get("prch_type");
//				String oa_site_no = (String) extMap.get("oa_site_no");
//
//				if ("10".equals(prch_type) || "20".equals(prch_type)) {// 一地入库，
//					stockNum = getStockNumByStore(oa_site_no, sku_code);
//				} else if ("00".equals(prch_type)) {// 四地入库，查询库存总和
//					stockNum = getStockNumByStore(sku_code);
//				}
//			}
//		}
		// 惠美丽跟小时代没有走到这一步
//		if (VersionHelper.checkServerVersion("3.5.72.55")
//				&& AppConst.MANAGE_CODE_HOMEHAS.equals(seller_code)) {
//			PlusSupportProduct plus = new PlusSupportProduct();
//			stockNum = stockNum - plus.upLockStock(sku_code);
//		}
		//***************end
		return stockNum < 0 ? 0 : stockNum;
	}

	/**
	 * 查询商品所在地的库存
	 * 
	 * @param sku_code
	 * @param district_code
	 * @return
	 */
	public int getStockNumBySku(String sku_code, String district_code) {

		// 查询sku信息
		List<Map<String, Object>> skuL = DbUp.upTable("pc_skuinfo").dataQuery(
				"seller_code,product_code", "", "sku_code=:sku_code",
				new MDataMap("sku_code", sku_code), 0, 1);
		if (skuL.size() < 1) {
			return 0;
		}
		int stockNum = 0;
		Map<String, Object> skuMap = skuL.get(0);
		String seller_code = (String) skuMap.get("seller_code");
		String product_code = (String) skuMap.get("product_code");

		if (AppConst.MANAGE_CODE_CAPP.equals(seller_code)) {// 惠美丽的库存
			return getStockNumByMaxFor7(sku_code);
		} else if (AppConst.MANAGE_CODE_CYOUNG.equals(seller_code)) {
			return getStockNumByMaxFor13(sku_code);
		} else if (AppConst.MANAGE_CODE_HOMEHAS.equals(seller_code)
				|| AppConst.MANAGE_CODE_HPOOL.equals(seller_code) || AppConst.MANAGE_CODE_CDOG.equals(seller_code)) {// 惠家有和家有汇的获取库存方式一致

			// 判断是否为虚拟商品
			List<Map<String, Object>> productL = DbUp.upTable("pc_productinfo")
					.dataQuery("validate_flag,seller_code,small_seller_code",
							"", "product_code=:product_code",
							new MDataMap("product_code", product_code), 0, 1);
			if (productL.size() < 1) {
				return stockNum;
			}

			Map<String, Object> productMap = productL.get(0);
			String validate_flag = (String) productMap.get("validate_flag");
			String small_seller_code = (null == productMap
					.get("small_seller_code")) ? "" : productMap.get(
					"small_seller_code").toString();
			if ("Y".equals(validate_flag)) {// 虚拟商品 查看C18的库存
				// 第三方商户商品走TDS1库
				if (AppConst.MANAGE_CODE_HOMEHAS.equals(productMap.get(
						"seller_code").toString())
						&& !AppConst.MANAGE_CODE_HOMEHAS
								.equals(small_seller_code)) {
					// stockNum = getStockNumByStore(AppConst.THIRD_STORE_CODE,
					// sku_code);

					stockNum = new PlusSupportProduct()
							.upSkuAllStockForInt(sku_code);
				} else if (AppConst.MANAGE_CODE_CDOG.equals(productMap.get(
						"seller_code").toString())) {
					stockNum = getStockNumByStore(AppConst.CDOG_STORE_CODE, sku_code);
				}else{
					stockNum = getStockNumByStore("C18", sku_code);
				}
			} else {// 非虚拟商品

				// 判断是否为一地入库
				List<Map<String, Object>> extL = DbUp.upTable(
						"pc_productinfo_ext").dataQuery("prch_type,oa_site_no",
						"", "product_code=:product_code",
						new MDataMap("product_code", product_code), 0, 1);
				if (extL.size() < 1) {
					return 0;
				}

				Map<String, Object> extMap = extL.get(0);
				String prch_type = (String) extMap.get("prch_type");
				String oa_site_no = (String) extMap.get("oa_site_no");

				if ("10".equals(prch_type) || "20".equals(prch_type)) {// 一地入库，

					// 判断一下所在的区域是否
					if (getStores(district_code).contains(oa_site_no)) {
						stockNum = getStockNumByStore(oa_site_no, sku_code);
					} else {
						return 0;
					}

				} else if ("00".equals(prch_type)) {// 四地入库，查询库存总和
					// return getStockNumByStore(sku_code);
					stockNum = getStockNumByDistrict(district_code, sku_code);
				}
			}
		}
		// 惠美丽跟小时代没有走到这一步
		if (VersionHelper.checkServerVersion("3.5.72.55")
				&& AppConst.MANAGE_CODE_HOMEHAS.equals(seller_code)) {
			PlusSupportProduct plus = new PlusSupportProduct();
			stockNum = stockNum - plus.upLockStock(sku_code);
		}
		return stockNum < 0 ? 0 : stockNum;
	}

	/**
	 * 查询product是否有库存，有库存时返回Map中该product对应的值为1，否则为空(惠家有，家有汇)
	 * 
	 * @param seller_code
	 * @param product_code
	 *            两个参数不能都为空
	 * @return
	 */
	public Map<String, Integer> getStockNumAll(String seller_code,
			String product_code) {

		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		// 两个参数不能都为空
		if (StringUtils.isEmpty(seller_code)
				&& StringUtils.isEmpty(product_code)) {
			return resultMap;
		}
		String productWhere = "";
		if (StringUtils.isNotEmpty(seller_code)) {
			productWhere += " and pc.seller_code='" + seller_code + "' ";
		}
		if (StringUtils.isNotEmpty(product_code)) {
			productWhere += " and pc.product_code in ('"
					+ product_code.replace(",", "','") + "') ";
		}
		String productSql = " select pc.product_code as product_code,pc.validate_flag as validate_flag, "
				+ " pce.prch_type as prch_type,pce.oa_site_no as oa_site_no from pc_productinfo pc,pc_productinfo_ext pce "
				+ "where pc.product_code = pce.product_code and pc.product_status='4497153900060002' "
				+ productWhere;
		List<Map<String, Object>> productInfoListMap = DbUp.upTable(
				"pc_productinfo").dataSqlList(productSql, null);

		StringBuffer productCodesBuffer = new StringBuffer();
		String productCodesStr = "";
		for (Map<String, Object> map : productInfoListMap) {
			productCodesBuffer.append(map.get("product_code"));
			productCodesBuffer.append(",");
		}
		productCodesStr = productCodesBuffer.toString();
		if (productCodesStr.length() > 1) {
			productCodesStr = (productCodesStr.substring(0,
					productCodesStr.length() - 1).replace(",", "','"));
		}
		String skuSql = "select ps.sku_code,ps.product_code from pc_skuinfo ps where ps.product_code in ('"
				+ productCodesStr + "') and ps.sale_yn='Y' ";
		List<Map<String, Object>> skuInfoListMap = DbUp.upTable("pc_skuinfo")
				.dataSqlList(skuSql, null);

		for (Map<String, Object> skuMap : skuInfoListMap) {
			String skuCode = skuMap.get("sku_code").toString();
			String skuProductCode = skuMap.get("product_code").toString();

			for (Map<String, Object> productMap : productInfoListMap) {
				if (StringUtils.isEmpty(skuProductCode)
						|| null == productMap.get("product_code")
						|| null != resultMap.get(skuProductCode)) {
					continue;
				}
				if (skuProductCode.equals(productMap.get("product_code")
						.toString())) {
					int stock = 0;
					String prch_type = (String) productMap.get("prch_type");
					String oa_site_no = (String) productMap.get("oa_site_no");
					if ("Y".equals(productMap.get("validate_flag"))) {// 虚拟商品
																		// 查看C18的库存

						/*
						 * //第三方商品查询库存 if
						 * ("20".equals(prch_type)&&AppConst.THIRD_STORE_CODE
						 * .equals(oa_site_no)) { stock =
						 * this.getStockNumByStore(oa_site_no, skuCode); }else{
						 * stock = this.getStockNumByStore("C18", skuCode); }
						 */
						stock = new PlusSupportProduct()
								.upSkuAllStockForInt(skuCode);

						if (stock > 0) {
							resultMap.put(skuProductCode, 1); // 有库存，插入到Map
							continue;
						}
					} else {// 非虚拟商品
							// 判断是否为一地入库

						if ("10".equals(prch_type) || "20".equals(prch_type)) {// 一地入库，
							stock = getStockNumByStore(oa_site_no, skuCode);
						} else if ("00".equals(prch_type)) {// 四地入库，查询库存总和
							stock = getStockNumByStore(skuCode);
						}
						if (stock > 0) {
							resultMap.put(skuProductCode, 1); // 有库存，插入到Map
							continue;
						}
					}
				}
			}
		}
		return resultMap;
	}

	/**
	 * 查询product是否有库存，有库存时返回Map中该product对应的值为1，否则为空(惠家有，家有汇)<br>
	 * 商品剩余总库存
	 * 
	 * @param seller_code
	 * @param product_code
	 *            两个参数不能都为空
	 * @param version
	 *            传入1，标志重载此方法的实现
	 * @return  商品剩余总库存
	 * @author ligj  
	 */
	public Map<String, Integer> getStockNumAll(String seller_code,
			String product_code, int version) {

		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		// 两个参数不能都为空
		if (StringUtils.isEmpty(seller_code)
				&& StringUtils.isEmpty(product_code)) {
			return resultMap;
		}
		
		String[] codes = product_code.split(",");
		for(String code : codes) {
			if(StringUtils.isNotBlank(code)) {
				// 修改为直接判断总库存
				if(new PlusSupportStock().upAllStockForProduct(code) > 0) {
					resultMap.put(code, 1);
				}
			}
		}
		
//		String productWhere = "";
//		if (StringUtils.isNotEmpty(seller_code)) {
//			productWhere += " and pc.seller_code='" + seller_code + "' ";
//		}
//		if (StringUtils.isNotEmpty(product_code)) {
//			productWhere += " and pc.product_code in ('"
//					+ product_code.replace(",", "','") + "') ";
//		}
//		String productSql = " select pc.product_code as product_code,pc.validate_flag as validate_flag, "
//				+ " pce.prch_type as prch_type,pce.oa_site_no as oa_site_no from pc_productinfo pc,pc_productinfo_ext pce "
//				+ "where pc.product_code = pce.product_code and pc.product_status='4497153900060002' "
//				+ productWhere;
//		List<Map<String, Object>> productInfoListMap = DbUp.upTable(
//				"pc_productinfo").dataSqlList(productSql, null);
//
//		StringBuffer productCodesBuffer = new StringBuffer();
//		String productCodesStr = "";
//
//		Map<String, Map<String, Object>> productInfoMap = new HashMap<String, Map<String, Object>>();
//		for (Map<String, Object> map : productInfoListMap) {
//			productCodesBuffer.append(map.get("product_code"));
//			productCodesBuffer.append(",");
//			productInfoMap.put(map.get("product_code").toString(), map);
//		}
//		productCodesStr = productCodesBuffer.toString();
//		if (productCodesStr.length() > 1) {
//			productCodesStr = (productCodesStr.substring(0,
//					productCodesStr.length() - 1).replace(",", "','"));
//		}
//		String skuSql = "select ps.sku_code,ps.product_code from pc_skuinfo ps where ps.product_code in ('"
//				+ productCodesStr + "') and ps.sale_yn='Y' ";
//		List<Map<String, Object>> skuInfoListMap = DbUp.upTable("pc_skuinfo")
//				.dataSqlList(skuSql, null);
//
//		Map<String,Integer> mExistStockMap = new HashMap<String,Integer>();
//		List<String> skuCodeArr = new ArrayList<String>();
//		for (Map<String, Object> map : skuInfoListMap) {
//			skuCodeArr.add(map.get("sku_code").toString());
//		}
//		PlusSupportStock plusSupportStock = new PlusSupportStock();
//		PlusSupportProduct support = new PlusSupportProduct();
//		
//		if (!skuCodeArr.isEmpty()) {
//			for (String skuCode : skuCodeArr) {
//				mExistStockMap.put(skuCode,plusSupportStock.upAllStock(skuCode));
//			}
//		}
//
//		for (int i = 0, j = skuInfoListMap.size(); i < j; i++) {
//			Map<String, Object> skuMap = skuInfoListMap.get(i);
//			String skuCode = skuMap.get("sku_code").toString();
//			String skuProductCode = skuMap.get("product_code").toString();
//			if (StringUtils.isEmpty(skuProductCode)
//					|| null != resultMap.get(skuProductCode)) {
//				continue;
//			}
//			if (!mExistStockMap.containsKey(skuCode) || mExistStockMap.get(skuCode) <= 0) {	
//				//第一步判断是否有总库存，如果没有则继续下个sku，否则就进行是否有促销活动的判断
//				continue;
//			} else {
//				PlusModelSkuInfo skuSuppore = support.upSkuInfoBySkuCode(skuCode,"");
//				if (StringUtils.isNotEmpty(skuSuppore.getEventCode())) {
//					//参加活动时判断促销库存
//					if ((int)skuSuppore.getLimitStock() > 0) {
//						resultMap.put(skuProductCode, 1);
//						continue;
//					}
//				}else{
//					//不参加活动时判断普通库存
//					if (plusSupportStock.upSalesStock(skuCode) > 0) {
//						resultMap.put(skuProductCode, 1);
//						continue;
//					}
//				}
//			}
//		}
		return resultMap;
	}
	/**
	 * sum库存数（直接取数据库里面）
	 * @param sku_code
	 * @return
	 */
	public int getStockNumBySkuBySum(String sku_code) {
		// 直接取SKU的库存数，不再判断sku_code_old字段，解决商品商品迁移后库存数显示错误问题
		//MDataMap map  = DbUp.upTable("pc_skuinfo").one("sku_code",sku_code);
		//if(map!=null&&!map.isEmpty()&&StringUtils.isNotBlank(map.get("sku_code_old"))){
		//	sku_code=map.get("sku_code_old");
		//}
		MDataMap stockNumMap = DbUp.upTable("sc_store_skunum").oneWhere("SUM(stock_num) as stock_num", "", "", "sku_code",sku_code);
		if (null == stockNumMap || stockNumMap.isEmpty()) {
			return 0;
		}
		return Integer.parseInt(StringUtils.isEmpty(stockNumMap.get("stock_num"))?  "0" : stockNumMap.get("stock_num") );
	}
}
