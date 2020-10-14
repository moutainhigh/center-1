package com.cmall.productcenter.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 商品检查类
 * 
 * @author srnpr
 * 
 */
public class ProductCheck extends BaseClass {

	/**
	 * 获取商品销售范围 如果返回空则表示该商家禁止销售<br>
	 * 在添加商品和上架商品时调用
	 * 
	 * @param sSellerCode
	 * @return 如果返回空则表示该商家禁止銷售
	 */
	public String upSalesScopeType(String sSellerCode) {
		String sReturn = "";

		if (StringUtils.isNotEmpty(sSellerCode)) {
			MDataMap mUserInfo = DbUp.upTable("uc_sellerinfo").one(
					"seller_code", sSellerCode);

			if (mUserInfo != null
					&& StringUtils.isNotEmpty(mUserInfo.get("seller_type"))) {
				String sSellerTypeString = mUserInfo.get("seller_type");

				// 如果是商家平台返回无限制
				if (sSellerTypeString.equals("449746390001")) {
					sReturn = "449746400001";
				}
				// 如果是手机平台返回手机专用
				else if (sSellerTypeString.equals("449746390002")) {
					sReturn = "449746400002";
				}

			}

			// 判断如果店铺是手机店铺 则校验上架商品不能超过10个
			if (sReturn.equals("449746400002")) {
				int iCount = DbUp.upTable("pc_productinfo").count(
						"product_status", "4497153900060002");

				// 如果大于10个上架商品 则返回空
				if (iCount >= 10) {
					sReturn = "";
				}

			}

		}

		return sReturn;
	}
	
	/** 
	* @Description:从URL中截取商品code
	* @param url
	* @return
	* @author 张海生
	* @date 2015-7-27 上午9:40:23
	* @return String 
	* @throws 
	*/
	public String getProductCode(String url){
		if(StringUtils.isNotBlank(url)){
			return url.substring(url.indexOf("=")+1,url.length()-2);
		}
		return "";
	}
	
	/** 
	* @Description:
	* @param productCode 获取商品相关信息
	* @author 张海生
	* @date 2015-9-19 下午1:34:14
	* @return Map<String,Object> 
	* @throws 
	*/
	public Map<String, Object> getProductRelaInfor(String productCode) {

		String sql = "SELECT pi.uid,pi.product_code,pi.product_name,pi.market_price,pi.cost_price,sd.define_name,"
				+ "pi.tax_rate, pe.gross_profit,pe.md_nm,pe.dlr_id,us.seller_name,si.bank_account,si.branch_name,"
				+ "si.quality_retention_money,si.md_name,fi.define_name purchase_type FROM  productcenter.pc_productinfo pi LEFT JOIN "
				+ "productcenter.pc_productinfo_ext pe ON pi.product_code = pe.product_code LEFT JOIN "
				+ "usercenter.uc_sellerinfo us ON pe.dlr_id=us.small_seller_code LEFT JOIN "
				+ "usercenter.uc_seller_info_extend si ON us.small_seller_code=si.small_seller_code "
				+ "LEFT JOIN systemcenter.sc_define sd ON pe.settlement_type=sd.define_code "
				+ "LEFT JOIN systemcenter.sc_define fi ON pe.purchase_type=fi.define_code "
				+ "where pi.product_code=:product_code";
		Map<String, Object> proMap = DbUp.upTable("pc_productinfo").dataSqlOne(
				sql, new MDataMap("product_code", productCode));
		MDataMap properMap = DbUp.upTable("pc_productproperty").oneWhere(
				"property_value", "", "", "product_code", productCode,
				"property_key", "内联赠品");
		String skuSql = "select count(*) skuNumber, sum(sell_price) sellPrice from pc_skuinfo where product_code =:product_code";
		String gift = "";
		if (properMap != null) {
			gift = properMap.get("property_value");
		}
		if (proMap == null) {
			proMap = new HashMap<String, Object>();
		}
		String grossProfit = "";
		Map<String, Object> skuMap = DbUp.upTable("pc_skuinfo").dataSqlOne(
				skuSql, new MDataMap("product_code", productCode));
		if (skuMap != null && skuMap.get("skuNumber") != null
				&& skuMap.get("sellPrice") != null) {
			BigDecimal skuNum = new BigDecimal(skuMap.get("skuNumber").toString());
			if (proMap.get("cost_price") != null) {
				BigDecimal costPrice = new BigDecimal(proMap.get("cost_price").toString());
				BigDecimal totalCostPrice = costPrice.multiply(skuNum);
				BigDecimal sellPrice = new BigDecimal(skuMap.get("sellPrice").toString());
				if (!"0.00".equals(proMap.get("cost_price").toString())) {
					grossProfit = sellPrice
							.subtract(totalCostPrice)
							.divide(sellPrice, 2, BigDecimal.ROUND_HALF_UP)
							.toString();
				}
			}
		}
		if (proMap.get("cost_price") != null && proMap.get("tax_rate") != null) {
			BigDecimal costPrice = new BigDecimal(proMap.get("cost_price")
					.toString());
			BigDecimal taxRate = new BigDecimal(proMap.get("tax_rate")
					.toString());
			BigDecimal tax = costPrice
					.divide(new BigDecimal(1).add(taxRate), 3,
							BigDecimal.ROUND_HALF_UP).multiply(taxRate)
					.setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal noTaxPrice = costPrice.subtract(tax).setScale(2,
					BigDecimal.ROUND_HALF_UP);
			proMap.put("tax", tax.toString());
			proMap.put("noTaxPrice", noTaxPrice.toString());
		}
		proMap.put("gift", gift);
		proMap.put("grossProfit", grossProfit);
		return proMap;
	}
	
	/**
	 * 获取商品名称
	 * @param productCode
	 * 		商品编号
	 * @return 商品名称
	 */
	public String getProductNameByCode(String productCode){
		
		String productName = "";
		
		if(StringUtils.isNotBlank(productCode)){
			
			MDataMap mDataMap = DbUp.upTable("pc_productinfo").one("product_code", productCode);
			
			if(mDataMap != null){
				
				productName = mDataMap.get("product_name");
				
			}
			
		}
		
		return productName;
		
	}
	
	/** 
	* @Description:
	* @param productCode
	* @author 张海生
	* @date 2015-11-23 下午6:35:35
	* @return List<MDataMap> 
	* @throws 
	*/
//	public List<MDataMap> getSkuInfor(String productCode){
//		List<MDataMap> skuList = new ArrayList<MDataMap>();
//		if(StringUtils.isNotEmpty(productCode)){
//			skuList = DbUp.upTable("pc_skuinfo").queryAll("uid,sku_code,sku_name,sku_keyvalue", "", "", new MDataMap("product_code",productCode));
//			for (MDataMap mDataMap : skuList) {
//				String skuValue = mDataMap.get("sku_keyvalue");
//				String skuarr[] = skuValue.split("&");
//				mDataMap.put("sku_keyvalue", skuarr[0].split("=")[1]+"/"+skuarr[1].split("=")[1]);
//			}
//		}
//		return skuList;
//	}
	/**
	 * 
	 * 方法: getSkuInfor <br>
	 * 描述: 读取sku信息及sku库存 <br>
	 * 作者: 张海宇 zhanghaiyu@huijiayou.cn<br>
	 * 时间: 2016年7月26日 下午4:24:47
	 * 
	 * @param productCode
	 * @return
	 */
	public List<MDataMap> getSkuInfor(String productCode) {
		List<MDataMap> skuList = new ArrayList<MDataMap>();
		if (StringUtils.isNotEmpty(productCode)) {
			List<Map<String, Object>> list = DbUp.upTable("pc_skuinfo").dataSqlList(
					"select ps.uid,ps.sku_code,ps.sku_name,ps.sku_keyvalue,IFNULL(ss.stock_num,0) as stock_num from productcenter.pc_skuinfo as ps LEFT JOIN systemcenter.sc_store_skunum AS ss ON ps.sku_code = ss.sku_code where ps.product_code =:product_code",
					new MDataMap("product_code", productCode));
			for (Map<String, Object> mDataMap : list) {
				String skuValue = mDataMap.get("sku_keyvalue").toString();
				String skuarr[] = skuValue.split("&");
				mDataMap.put("sku_keyvalue", skuarr[0].split("=")[1] + "/" + skuarr[1].split("=")[1]);
				skuList.add(new MDataMap(mDataMap));
			}
		}
		return skuList;
	}
	/** 
	* @Description:检查商品下是否有sku的库存等于预警库存
	* @param productCode
	* @author 张海生
	* @date 2015-12-2 下午2:43:09
	* @return int 
	* @throws 
	*/
	public int checkSkuStore(String productCode){
		List<MDataMap> skuList = DbUp.upTable("pc_skuinfo").queryAll("stock_num,security_stock_num", "", "", new MDataMap("product_code",productCode));
		if(skuList != null && skuList.size() > 0){
			for (MDataMap mDataMap : skuList) {
				BigDecimal stockNum = new BigDecimal(mDataMap.get("stock_num"));
				BigDecimal securityNum  = new BigDecimal(mDataMap.get("security_stock_num"));
				if(securityNum.intValue() != 0 && stockNum.intValue() == securityNum.intValue()){
					return 1;
				}
			}
		}
		return 0;
	}
	
	/** 
	* @Description:查询商品下sku的成本价范围
	* @param productCode
	* @author 张海生
	* @date 2015-12-4 下午3:56:03
	* @return String 
	* @throws 
	*/
	public String getCostPriceRange(String productCode){
		String sSql = "SELECT product_code,MAX(cost_price) max_cost_price,MIN(cost_price) min_cost_price FROM productcenter.pc_skuinfo where product_code=:product_code";
		Map<String, Object> skuMap = DbUp.upTable("pc_skuinfo").dataSqlOne(sSql, new MDataMap("product_code",productCode));
		String costPrice = "";
		if(skuMap != null){
			String minPrice = skuMap.get("min_cost_price").toString();
			String maxPrice = skuMap.get("max_cost_price").toString();
			costPrice = minPrice + "-" + maxPrice;
		}
		return costPrice;
	}
}