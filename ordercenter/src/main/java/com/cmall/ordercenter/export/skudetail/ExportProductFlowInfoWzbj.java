package com.cmall.ordercenter.export.skudetail;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webexport.ExportChart;
import com.srnpr.zapweb.webmodel.MPageData;

public class ExportProductFlowInfoWzbj extends ExportChart {

	@Override
	public void exportExcel(String sOperateId, HttpServletRequest request, HttpServletResponse response) {
		super.exportExcel(sOperateId, request, response);
		MPageData pageData = getPageData();
		int productCodeIndex = pageData.getPageField().indexOf("product_code");
		int statusIndex = pageData.getPageField().indexOf("current_status");
		int updateTimeIndex = pageData.getPageField().indexOf("update_time");
		
		MPageData newPageData = new MPageData();
		newPageData.getPageHead().add("商户编号");
		newPageData.getPageHead().add("商户简称");
		newPageData.getPageHead().add("商品编号");
		newPageData.getPageHead().add("商品名称");
		newPageData.getPageHead().add("商品分类");
		newPageData.getPageHead().add("更新时间");
		newPageData.getPageHead().add("当前状态");
		newPageData.getPageHead().add("市场价格");
		newPageData.getPageHead().add("销售价格");
		newPageData.getPageHead().add("毛利率");
		newPageData.getPageHead().add("税率");
		newPageData.getPageHead().add("是否可卖");
		newPageData.getPageHead().add("商品规格");
		newPageData.getPageHead().add("商品品牌");
//		newPageData.getPageHead().add("商品描述");  // 京东商品的描述有可能会造成导出excel表格数据异常，暂时屏蔽
		newPageData.getPageHead().add("关键字");
		newPageData.getPageHead().add("虚拟销售量基数");
		
		newPageData.setPageData(new ArrayList<List<String>>());
		String productCode;
		MDataMap productMap,sellerInfo;
		
		Map<String,String> categoryCodeMap = getCategoryMap();
		
		Map<String,MDataMap> sellerInfoMap = new HashMap<String, MDataMap>();
		for(List<String> list : pageData.getPageData()) {
			productCode = list.get(productCodeIndex);
			productMap = DbUp.upTable("pc_productinfo").one("product_code", productCode);
			sellerInfo = sellerInfoMap.get(productMap.get("small_seller_code"));
			if(sellerInfo == null) {
				sellerInfo = DbUp.upTable("uc_sellerinfo").one("small_seller_code", productMap.get("small_seller_code"));
				sellerInfoMap.put(productMap.get("small_seller_code"), sellerInfo);
			}
			
			newPageData.getPageData().add(Arrays.asList(
					productMap.get("small_seller_code"),
					sellerInfo.get("seller_company_name"),
					productCode,
					productMap.get("product_name"),
					getCategoryName(productCode, categoryCodeMap),
					list.get(updateTimeIndex),
					list.get(statusIndex),
					productMap.get("market_price"),
					getSellPrice(productMap),
					getSkuProfitRate(productCode),
					formatRate(productMap.get("tax_rate")),
					getSkuSaleStatus(productCode),
					getProductProperty(productCode),
					getBrandName(productMap.get("brand_code")),
//					getProductDesc(productCode),
					productMap.get("labels"),
					getFictitiousSales(productCode)
			));
		}
		
		setPageData(newPageData);
	}
	
	private String getSellPrice(MDataMap productMap) {
		if(productMap.get("min_sell_price").equals(productMap.get("max_sell_price"))) {
			return productMap.get("min_sell_price");
		}
		
		return productMap.get("min_sell_price") + "-" + productMap.get("max_sell_price");
	}
	
	private String getSkuProfitRate(String productCode) {
		MDataMap map = DbUp.upTable("pc_skuinfo").oneWhere("MIN(ROUND(ROUND((sell_price - cost_price)/sell_price,2)*100)) minRate,MIN(ROUND(ROUND((sell_price - cost_price)/sell_price,2)*100)) maxRate", "", "cost_price > 0 AND product_code = :product_code", "product_code",productCode);
		
		if(map == null) return "";
		if(map.get("minRate").equals(map.get("maxRate"))) {
			return map.get("minRate")+"%";
		}
		return map.get("minRate")+"%" + "-" + map.get("maxRate")+"%";
	}
	
	private String getSkuSaleStatus(String productCode) {
		if(DbUp.upTable("pc_skuinfo").count("product_code",productCode,"sale_yn","Y") > 0) {
			return "是";
		}
		return "否";
	}
	
	private String getProductProperty(String productCode) {
		List<MDataMap> list = DbUp.upTable("pc_productproperty").queryByWhere("product_code", productCode, "property_type", "449736200004");
		
		StringBuilder builder = new StringBuilder();
		for(MDataMap map : list) {
			builder.append(map.get("property_key")).append(" ").append(map.get("property_value"));
			builder.append("\r\n");
		}
		return builder.toString();
	}
	
	private String getBrandName(String code) {
		if(StringUtils.isBlank(code)) return "";
		String name = (String)DbUp.upTable("pc_brandinfo").dataGet("brand_name", "", new MDataMap("brand_code", code));
		return name == null ? "" : name;
	}
	
	private String getProductDesc(String productCode) {
		String val = (String)DbUp.upTable("pc_productdescription").dataGet("description_info", "", new MDataMap("product_code", productCode));
		return val == null ? "" : val;
	}
	
	private String getFictitiousSales(String productCode) {
		Object val = (Object)DbUp.upTable("pc_productinfo_ext").dataGet("fictitious_sales", "", new MDataMap("product_code", productCode));
		return val == null ? "" : val.toString();
	}
	
	private String getCategoryName(String productCode,Map<String,String> categoryMap) {
		String code = (String)DbUp.upTable("uc_sellercategory_product_relation").dataGet("category_code", "", new MDataMap("product_code", productCode));
		return code == null ? "" : StringUtils.trimToEmpty(categoryMap.get(code));
	}
	
	private String formatRate(String v) {
		DecimalFormat df = new DecimalFormat("#.####");
		return df.format(Double.valueOf(v));
	}
	
	private Map<String,String> getCategoryMap() {
		Map<String,String> map = new HashMap<String, String>();
		String sql = "SELECT s3.category_code, CONCAT(s1.category_name,'->',s2.category_name,'->',s3.category_name) category_name FROM `uc_sellercategory` s3";
			sql += " LEFT JOIN uc_sellercategory s2 ON s3.parent_code = s2.category_code";
			sql += " LEFT JOIN uc_sellercategory s1 ON s2.parent_code = s1.category_code";
			sql += " WHERE s3.`level` = 4";
		List<Map<String, Object>> list = DbUp.upTable("uc_sellercategory").dataSqlList(sql, new MDataMap());
		for(Map<String, Object> m : list) {
			map.put(m.get("category_code")+"", m.get("category_name")+"");
		}
		return map;
	}
	
}
