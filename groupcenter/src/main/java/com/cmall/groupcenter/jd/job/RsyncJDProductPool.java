package com.cmall.groupcenter.jd.job;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.srnpr.xmassystem.homehas.RsyncJingdongSupport;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * decription：定时京东全部商品池商品信息
 */

public class RsyncJDProductPool {

	static Log log = LogFactory.getLog(RsyncJDProductPool.class);
	
	static MDataMap catNameMap = new MDataMap();
	
	public void doRsyncProduct() {
		List<String> poolList = getPoolList();
		for(String pool : poolList) {
			List<String> skuList = getPoolSkuList(pool);
			for(String sku : skuList) {
				try {
					rsyncSkuInfo(sku);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 查询商品池编号列表
	 * @return
	 */
	public List<String> getPoolList() {
		List<String> poolList = new ArrayList<String>();
		Map<String, Object> params = new HashMap<String, Object>();
		String restulStr = RsyncJingdongSupport.callGateway("biz.product.PageNum.query", params);
		if(StringUtils.isBlank(restulStr) || !restulStr.contains("biz_product_PageNum_query_response")) {
			log.warn("调取京东商品池编号失败："+restulStr);
			return poolList;
		}
		
		JSONObject obj = new JSONObject(restulStr);
		JSONArray result = obj.optJSONObject("biz_product_PageNum_query_response").optJSONArray("result");
		
		if(result != null) {
			for(int i = 0; i < result.length(); i++) {
				poolList.add(result.optJSONObject(i).optString("page_num"));
			}
		}
		
		return poolList;
	}
	
	/**
	 * 取商品池里面的商品列表
	 * @param pool
	 * @return
	 */
	public List<String> getPoolSkuList(String pool) {
		List<String> skuList = new ArrayList<String>();
		
		Map<String, Object> params = new HashMap<String, Object>();
		String restulStr;
		int page = 1;
		
		while(true) {
			params.put("pageNum", pool);
			params.put("pageNo", page+"");
			params.put("logIgnore", "Y");
			restulStr = RsyncJingdongSupport.callGateway("jd.biz.product.getSkuByPage", params);
		
			if(StringUtils.isBlank(restulStr) || !restulStr.contains("jd_biz_product_getSkuByPage_response")) {
				log.warn("调取京东商品池商品失败："+restulStr);
				break;
			}
			
			JSONObject obj = new JSONObject(restulStr);
			JSONObject result = obj.optJSONObject("jd_biz_product_getSkuByPage_response").optJSONObject("result");
			
			if(result == null) {
				break;
			}
			
			int pageCount = result.optInt("pageCount");
			JSONArray skuIds = result.optJSONArray("skuIds");
			
			if(skuIds != null) {
				for(int i = 0; i < skuIds.length(); i++) {
					skuList.add(skuIds.optString(i));
				}
			}
			
			if(pageCount <= page) {
				break;
			}
			
			page++;
		}
		
		return skuList;
	}
	
	/**
	 * 同步具体SKU信息
	 * @param sku
	 */
	public void rsyncSkuInfo(String sku) {
		MDataMap dataInfo = DbUp.upTable("pc_jingdong_product").one("sku_code",sku);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("sku", sku);
		//params.put("isShow", "true");
		params.put("logIgnore", "Y");
		String restulStr = RsyncJingdongSupport.callGateway("biz.product.detail.query", params);
		
		if(StringUtils.isBlank(restulStr) || !restulStr.contains("biz_product_detail_query_response")) {
			log.warn("调取京东商品详情失败："+restulStr);
			return;
		}
		
		MDataMap skuInfo = new MDataMap();
		
		// 商品基本信息
		JSONObject obj = new JSONObject(restulStr);
		JSONObject productDetail = obj.optJSONObject("biz_product_detail_query_response").optJSONObject("result");
		skuInfo.put("sku_code", sku);
		skuInfo.put("sku_state", productDetail.optString("state", ""));
		skuInfo.put("product_name", productDetail.optString("name", ""));
		skuInfo.put("main_url", "http://img13.360buyimg.com/n0/"+productDetail.optString("imagePath", ""));
		skuInfo.put("brand", productDetail.optString("brandName", ""));
		skuInfo.put("category", getFullCategoryName(productDetail.optString("category", "")));
		
		// 商品spu_code编号
		if(dataInfo == null) {
			params.clear();
			params.put("set", Arrays.asList(sku));
			params.put("logIgnore", "Y");
			restulStr = RsyncJingdongSupport.callGateway("jd.kpl.open.shopinfo.sku", params);
			
			if(StringUtils.isBlank(restulStr) || !restulStr.contains("jd_kpl_open_shopinfo_sku_response")) {
				log.warn("调取京东商品集失败："+restulStr);
				return;
			}
			
			obj = new JSONObject(restulStr);
			JSONArray ja = obj.optJSONObject("jd_kpl_open_shopinfo_sku_response").optJSONObject("result").optJSONArray(sku);
			if(ja == null) {
				log.warn("调取京东jd.kpl.open.shopinfo.sku接口失败：["+sku+"] "+restulStr);
				return;
			}
			
			JSONObject skuDetail = ja.optJSONObject(0);
			skuInfo.put("spu_code", skuDetail.optString("erpPid",""));
		}
		
		params.clear();
		params.put("skuIds", sku);
		params.put("logIgnore", "Y");
		restulStr = RsyncJingdongSupport.callGateway("biz.product.sku.check", params);
		
		if(StringUtils.isBlank(restulStr) || !restulStr.contains("biz_product_sku_check_response")) {
			log.warn("调取京东商品品可售验证接口失败："+restulStr);
			return;
		}
		
		// 商品可售验证
		obj = new JSONObject(restulStr);
		JSONObject skuCheck = obj.optJSONObject("biz_product_sku_check_response").optJSONArray("result").optJSONObject(0);
		skuInfo.put("sale_state", skuCheck.optString("saleState"));
		
		params.clear();
		params.put("sku", sku);
		params.put("logIgnore", "Y");
		restulStr = RsyncJingdongSupport.callGateway("biz.price.sellPrice.get", params);
		if(StringUtils.isBlank(restulStr)) {
			log.warn("调取京东查询价格接口失败："+restulStr);
			return;
		}
		
		if(!restulStr.contains("biz_price_sellPrice_get_response")) {
			skuInfo.put("sale_state", "0");
		} else {
			JSONArray resultArr = new JSONObject(restulStr).optJSONObject("biz_price_sellPrice_get_response").optJSONArray("result");
			if(resultArr != null) {
				JSONObject skuPriceInfo = resultArr.optJSONObject(0);
				if(skuPriceInfo != null) {
					skuInfo.put("cost_price", skuPriceInfo.optString("price"));
					skuInfo.put("jd_price", skuPriceInfo.optString("jdPrice"));
				} else {
					skuInfo.put("sale_state", "0");
				}
			}
		}
		
		skuInfo.put("update_time", FormatHelper.upDateTime());
		if(dataInfo != null) {
			skuInfo.put("zid", dataInfo.get("zid"));
			skuInfo.put("uid", dataInfo.get("uid"));
			DbUp.upTable("pc_jingdong_product").update(skuInfo);
		} else {
			// 商品池只保存可售数据
			if("1".equals(skuInfo.get("sale_state"))) {
				DbUp.upTable("pc_jingdong_product").dataInsert(skuInfo);
			}
		}
	}
	
	public String getFullCategoryName(String categoryId) {
		if(catNameMap.isEmpty()) {
			List<MDataMap> list = DbUp.upTable("pc_jingdong_category").queryAll("cat_id,name", "", "", null);
			for(MDataMap map : list) {
				catNameMap.put(map.get("cat_id"), map.get("name"));
			}
		}
		
		if(StringUtils.isBlank(categoryId)) return "";
		
		String[] vs = categoryId.split(";");
		List<String> list = new ArrayList<String>();
		for(String v : vs) {
			list.add(StringUtils.trimToEmpty(catNameMap.get(v)));
		}
		return StringUtils.join(list,"->");
	}
	
	// 同步商品分类,手动跑后存入数据库
	public void rsyncCategory() throws Exception {
		List<String> catList = new ArrayList<String>();
		TreeMap<String, Object> treeMap = new TreeMap<String, Object>();
		int start = 1;
		int error = 0;
		while(true) {
			treeMap.put("pageNo", start);
			treeMap.put("pageSize", 5000);
			treeMap.put("logIgnore", "Y");
			String returnStr = RsyncJingdongSupport.callGateway("jd.biz.product.getcategorys", treeMap);
			JSONObject result = null;
			try {
				result = new JSONObject(returnStr).getJSONObject("jd_biz_product_getcategorys_response");
				JSONArray categorys = result.optJSONObject("result").optJSONArray("categorys");
				if(categorys != null) {
					JSONObject o;
					for(int i = 0; i < categorys.length(); i++) {
						 o = categorys.optJSONObject(i);
						catList.add(o.optString("catId")+","+o.optString("parentId")+","+o.optString("name")+","+o.optString("catClass")+","+o.optString("state"));
					}
				}
				
				if (categorys == null || categorys.length() < 5000) {
					break;
				}
			} catch (Exception e) {
				error++;
				start--;
				
				// 连续失败3次则终止
				if(error >= 3) {
					LogFactory.getLog(getClass()).warn("rsyncCategory error ! => " + result);
					return;
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				continue;
			}
			
			// 重置连续失败计数
			error = 0;
			
			
			start++;
		}
		
		FileUtils.writeLines(new File("d:\\cat.txt"), catList);
	}
}
