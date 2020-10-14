package com.cmall.groupcenter.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.helpers.LogLog;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.ordercenter.alipay.util.JsonUtil;
import com.cmall.productcenter.common.SkuCommon;
import com.cmall.productcenter.model.PcProductdescription;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.PcProductinfoExt;
import com.cmall.productcenter.model.PcProductpic;
import com.cmall.productcenter.model.PcProductproperty;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.service.ProductService;
import com.cmall.systemcenter.common.DateUtil;
import com.cmall.systemcenter.service.FlowBussinessService;
import com.srnpr.xmasorder.model.jingdong.ProductCheckRepVo;
import com.srnpr.xmasorder.service.TeslaOrderServiceJD;
import com.srnpr.xmassystem.Constants;
import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.xmassystem.homehas.RsyncJingdongSupport;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topdo.TopConfig;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;
import com.srnpr.zapweb.webwx.WxGateSupport;


/*
 * 同步京东商品方法
 * */
public class RsyncJDService extends BaseClass {

	final static String imageURLHead = "https://img13.360buyimg.com/n12/";// 图片地址拼接头（无京东水印图）
	final static String successCode = "0000";// 查询成功返回编码
	final static int queryNum = 100; // 批量查询一次最大查询量
	final static String JD_Pro_Head = "8016";
	final static String JD_Sku_Head = "8019";
	final static String storeNum = "99999";
	public final static String SKU_KEY_COLOR = "4497462000010001"; // 颜色属性
	public final static String SKU_KEY_STYLE = "4497462000020001"; // 款式属性

	public final static String authority_logo_sevenday = "e860613a9d1d4d029ddd9d53b18a794c";
	public final static String authority_logo_sevenday_no = "cdf85db537b64983a7ba2cd75299ba66";
	
	// 默认最低毛利率，大于0小于1的小数
	final static BigDecimal minProfitRate = new BigDecimal("0.05");

//	// 同步京东商品数据
//	public static void rsynData(Map<String, String> cateMap, Set<String> skuCodeSet, Set<String> allSkuCodeSet) {
//
//		if (cateMap != null) {
//			// 1.对skuId进行排序重整,最小的skuId为product
//			List<List<JSONObject>> ll = sortSku(cateMap, allSkuCodeSet);
//			// 1.1对比确定商品所有sku在推送范围内
//			skuCodeSet.retainAll(allSkuCodeSet);
//			List<String> relSkuList = new ArrayList<String>(skuCodeSet);
//			// 2.对图片进行批量查询,提高接口调用性能
//			Map<String, JSONArray> picMap = batchQuerySkuPic(relSkuList);
//			// 3.对价格进行批量查询,提高接口调用性能
//			Map<String, JSONObject> priceMap = batchQuerySkuPrice(relSkuList);
//			// 4.可售状态
//			Map<String, String> saleMap = batchQuerySkuIfSale(relSkuList);
//			// 5.上下架状态
//			Map<String, String> stateMap = batchQuerySkuState(relSkuList);
//			for (List<JSONObject> list : ll) {
//				// 6.封装完整京东商品数据并开启同步
//				setIntegralJDProInfo(list, picMap, priceMap, saleMap, stateMap);
//			}
//		}
//	}

	public static Map<String, String> batchQuerySkuState(List<String> skuCodelist) {

		Map<String, String> resultMap = new HashMap<String, String>();
		Map<String, Object> paramtMap = new HashMap<String, Object>();

		int size = skuCodelist.size();
		List<String> nextList = new ArrayList<String>();
		if (size > queryNum) {
			nextList = skuCodelist.subList(queryNum, size);
			String skus = StringUtils.join(skuCodelist.subList(0, queryNum), ",");
			paramtMap.put("sku", skus);
			JSONObject jsonValues = JsonUtil.getJsonValues(RsyncJingdongSupport.callGateway("biz.product.state.query", paramtMap));
			if(jsonValues.toString().contains("biz_product_state_query_response")) {
				JSONObject stateResult = JsonUtil.getJsonValues(jsonValues.get("biz_product_state_query_response").toString());
				if (successCode.equals(stateResult.get("resultCode"))) {
					JSONArray array = JSON.parseArray(RsyncJDService.getJsonValue(stateResult.toString(), "result"));
					if (array != null && array.size() > 0) {
						for (int i = 0; i < array.size(); i++) {
						    resultMap.put(RsyncJDService.getJsonValue(array.get(i).toString(), "sku"),RsyncJDService.getJsonValue(array.get(i).toString(), "state"));
						}
					}
				}
			}
			else {
				LogLog.error("biz.product.state.query错误返回值为"+jsonValues.toString());
			}

		} else {
			String skus = StringUtils.join(skuCodelist, ",");
			paramtMap.put("sku", skus);
			JSONObject jsonValues = JsonUtil.getJsonValues(RsyncJingdongSupport.callGateway("biz.product.state.query", paramtMap));
			if(jsonValues.toString().contains("biz_product_state_query_response")) {
			JSONObject stateResult = JsonUtil.getJsonValues(jsonValues.get("biz_product_state_query_response").toString());
			if (successCode.equals(stateResult.get("resultCode"))) {
				JSONArray array = JSON.parseArray((RsyncJDService.getJsonValue(stateResult.toString(), "result")));
				if (array != null && array.size() > 0) {
					for (int i = 0; i < array.size(); i++) {
						resultMap.put(RsyncJDService.getJsonValue(array.get(i).toString(), "sku"),RsyncJDService.getJsonValue(array.get(i).toString(), "state"));
					}
				}
			}
		 }
			else {
				LogLog.error("biz.product.state.query错误返回值为"+jsonValues.toString());
			}
		}
		// 递归调用
		if (!nextList.isEmpty()) {
			resultMap.putAll(batchQuerySkuState(nextList));
		}
		return resultMap;
	}

	public static Map<String, String> batchQuerySkuIfSale(List<String> skuCodelist) {

		Map<String, String> resultMap = new HashMap<String, String>();
		Map<String, Object> paramtMap = new HashMap<String, Object>();
		int size = skuCodelist.size();
		List<String> nextList = new ArrayList<String>();
		if (size > queryNum) {
			nextList = skuCodelist.subList(queryNum, size);
			String skus = StringUtils.join(skuCodelist.subList(0, queryNum), ",");
			paramtMap.put("skuIds", skus);
			String returnStr = RsyncJingdongSupport.callGateway("biz.product.sku.check", paramtMap);
			if(returnStr.contains("biz_product_sku_check_response")) {
				JSONObject priceResult = JsonUtil.getJsonValues(RsyncJDService.getJsonValue(returnStr,"biz_product_sku_check_response").toString());
				if (successCode.equals(priceResult.get("resultCode"))) {
					JSONArray array = JSON.parseArray((RsyncJDService.getJsonValue(priceResult.toString(), "result")));
					if (array != null && array.size() > 0) {
						for (int i = 0; i < array.size(); i++) {
							resultMap.put(RsyncJDService.getJsonValue(array.get(i).toString(), "skuId"),RsyncJDService.getJsonValue(array.get(i).toString(), "saleState"));
						}
					}
				}	
			}
			else {
				LogLog.error("biz.product.sku.check错误返回值为"+returnStr);
			}
		
		} else {
			String skus = StringUtils.join(skuCodelist, ",");
			paramtMap.put("skuIds", skus);
			String returnStr = RsyncJingdongSupport.callGateway("biz.product.sku.check", paramtMap);
			if(returnStr.contains("biz_product_sku_check_response")) {
			JSONObject priceResult = JsonUtil.getJsonValues(RsyncJDService.getJsonValue(returnStr,"biz_product_sku_check_response").toString());
			if (successCode.equals(priceResult.get("resultCode"))) {
				JSONArray array = JSON.parseArray((RsyncJDService.getJsonValue(priceResult.toString(), "result")));
				if (array != null && array.size() > 0) {
					for (int i = 0; i < array.size(); i++) {
						resultMap.put(RsyncJDService.getJsonValue(array.get(i).toString(), "skuId"),RsyncJDService.getJsonValue(array.get(i).toString(), "saleState"));
					}
				}
			  }
			}
			else {
				LogLog.error("biz.product.sku.check错误返回值为"+returnStr);
			}
			
		}
		// 递归调用
		if (!nextList.isEmpty()) {
			resultMap.putAll(batchQuerySkuIfSale(nextList));
		}
		return resultMap;
	}

	private static Map<String, JSONObject> batchQuerySkuPrice(List<String> skuCodelist) {

		Map<String, JSONObject> resultMap = new HashMap<String, JSONObject>();
		Map<String, Object> paramtMap = new HashMap<String, Object>();
		int size = skuCodelist.size();
		List<String> nextList = new ArrayList<String>();

		if (size > queryNum) {
			nextList = skuCodelist.subList(queryNum, size);
			String skus = StringUtils.join(skuCodelist.subList(0, queryNum), ",");
			paramtMap.put("sku", skus);
			String returnStr = RsyncJingdongSupport.callGateway("biz.price.sellPrice.get", paramtMap);
			if(returnStr.contains("biz_price_sellPrice_get_response")) {
				JSONObject priceResult = JsonUtil.getJsonValues(RsyncJDService.getJsonValue(returnStr,"biz_price_sellPrice_get_response"));
				if (successCode.equals(priceResult.get("resultCode"))) {
					JSONArray array = JSON.parseArray((RsyncJDService.getJsonValue(priceResult.toString(), "result")));
					if (array != null && array.size() > 0) {
						for (int i = 0; i < array.size(); i++) {
							resultMap.put(RsyncJDService.getJsonValue(array.get(i).toString(), "skuId"),JsonUtil.getJsonValues(array.get(i).toString()));
						}
					}
				}
			}
			else {
				LogLog.error("biz.price.sellPrice.get错误返回值为"+returnStr);
			}
		
		} else {
			String skus = StringUtils.join(skuCodelist, ",");
			paramtMap.put("sku", skus);
			String returnStr =RsyncJingdongSupport.callGateway("biz.price.sellPrice.get", paramtMap);
			if(returnStr.contains("biz_price_sellPrice_get_response")) {
				JSONObject priceResult = JsonUtil.getJsonValues(RsyncJDService.getJsonValue(returnStr,"biz_price_sellPrice_get_response").toString());
				if (successCode.equals(priceResult.get("resultCode"))) {
					JSONArray array = JSON.parseArray((RsyncJDService.getJsonValue(priceResult.toString(), "result")));
					if (array != null && array.size() > 0) {
						for (int i = 0; i < array.size(); i++) {
							resultMap.put(RsyncJDService.getJsonValue(array.get(i).toString(), "skuId"),JsonUtil.getJsonValues(array.get(i).toString()));
						}
					}
				}
			}
			else {
				LogLog.error("biz.price.sellPrice.get错误返回值为"+returnStr);
			}
			
		}
		// 递归调用
		if (!nextList.isEmpty()) {
			resultMap.putAll(batchQuerySkuPrice(nextList));
		}
		return resultMap;
	}

	private static Map<String, JSONArray> batchQuerySkuPic(List<String> skuCodelist) {

		Map<String, JSONArray> resultMap = new HashMap<String, JSONArray>();
		Map<String, Object> paramtMap = new HashMap<String, Object>();
		int size = skuCodelist.size();
		List<String> nextList = new ArrayList<String>();
		if (size > queryNum) {
			nextList = skuCodelist.subList(queryNum, size);
			String skus = StringUtils.join(skuCodelist.subList(0, queryNum), ",");
			paramtMap.put("sku", skus);
			String restulStr = RsyncJingdongSupport.callGateway("biz.product.skuImage.query", paramtMap);
			if(restulStr.contains("biz_product_skuImage_query_response")) {
				JSONObject picResult = JsonUtil.getJsonValues(RsyncJDService.getJsonValue(restulStr,"biz_product_skuImage_query_response"));
				if (successCode.equals(picResult.get("resultCode"))) {
					com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject((RsyncJDService.getJsonValue(picResult.toString(), "result")));
					if (jsonObject.keySet().size() > 0) {
						for (String set : jsonObject.keySet()) {
							JSONArray picArray = jsonObject.getJSONArray(set);resultMap.put(set, picArray);
						}
					}
				}
			}
			else {
				LogLog.error("biz.product.skuImage.query错误返回值为"+restulStr);
			}

		} else {
			String skus = StringUtils.join(skuCodelist, ",");
			paramtMap.put("sku", skus);
			String restulStr = RsyncJingdongSupport.callGateway("biz.product.skuImage.query", paramtMap);
			if(restulStr.contains("biz_product_skuImage_query_response")) {
				net.sf.json.JSONObject picResult = RsyncJDService.getJSONStrVal(restulStr,"biz_product_skuImage_query_response");
				if (successCode.equals(picResult.get("resultCode"))) {
					com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject((RsyncJDService.getJsonValue(picResult.toString(), "result")));
					if (jsonObject.keySet().size() > 0) {
						for (String set : jsonObject.keySet()) {
							JSONArray picArray = jsonObject.getJSONArray(set);
							resultMap.put(set, picArray);
						}
					}
				}
			}
			else {
				LogLog.error("biz.product.skuImage.query错误返回值为"+restulStr);
			}
	
		}
		// 递归调用
		if (!nextList.isEmpty()) {
			resultMap.putAll(batchQuerySkuPic(nextList));
		}
		return resultMap;
	}

	private static void setIntegralJDProInfo(String erpPid,List<JSONObject> list, Map<String, JSONArray> picMap,
			Map<String, JSONObject> priceMap, Map<String, String> saleMap, Map<String, String> stateMap) {

		try {
			// 一个商品的完成载体
			List<Map<String, JSONObject>> listMap = new ArrayList<Map<String, JSONObject>>();
			for (JSONObject jsonObject : list) {
				Map<String, JSONObject> subMap = new HashMap<String, JSONObject>();
				// 1.关联基础数据
				subMap.put("baseInfo", jsonObject);
				// 2.商品属性
				JSONObject skuProperties = getSkuProperties(jsonObject.get("skuId").toString());
				subMap.put("skuProperties", skuProperties);
				// 3.详情数据
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("sku", jsonObject.get("skuId").toString());
				paramMap.put("isShow", "true");
				paramMap.put("queryExts","taxInfo");
				JSONObject detailResult = JsonUtil.getJsonValues(RsyncJingdongSupport.callGateway("biz.product.detail.query", paramMap));
				if(detailResult.toString().contains("biz_product_detail_query_response")) {
					JSONObject responResult = detailResult.getJSONObject("biz_product_detail_query_response");
					if (successCode.equals(responResult.get("resultCode"))) {
						subMap.put("detailInfo", responResult.getJSONObject("result"));
						// 4.移动端样式数据（根据详情返回的数据判断是否需要查询）
						if (responResult.getJSONObject("result").get("introduction").toString().contains("skudesign")) {
							Map<String, Object> subParamMap = new HashMap<String, Object>();
							subParamMap.put("sku", jsonObject.get("skuId").toString());
							JSONObject jsonValues = JsonUtil.getJsonValues(RsyncJingdongSupport.callGateway("jd.kpl.open.item.getmobilewarestyleandjsbywareid", subParamMap));
							try {
								if(jsonValues.toString().contains("jd_kpl_open_item_getmobilewarestyleandjsbywareid_response")) {
									
									String returnResult = jsonValues.get("jd_kpl_open_item_getmobilewarestyleandjsbywareid_response").toString();
									// 此字段接口给的不明确，根据具体返回来判断
									com.alibaba.fastjson.JSONObject jsonObj = JSON.parseObject(returnResult);
									String returnCode = jsonObj.get("code").toString();
									if ("0".equals(returnCode) && !jsonObj.keySet().contains("error")) {
										JSONObject resultJsonObj = JsonUtil.getJsonValues(JSON.parseObject(returnResult).get("detail").toString());
										String detailContent = resultJsonObj.get("cssContent").toString();
										JSONObject picContent = filterForPic(detailContent, true);
										if (StringUtils.isBlank(picContent.get("mobilDetailPic").toString())) {
											picContent = filterForPic(responResult.getJSONObject("result").get("introduction").toString(), false);
										}
										subMap.put("mobilDetailPic", picContent);
									} else {
										// 为防止此时同步的移动端样式数据中图片信息为空，把详情接口中的图片同步过来
										JSONObject picContent = filterForPic(responResult.getJSONObject("result").get("introduction").toString(), false);
										subMap.put("mobilDetailPic", picContent);
									}
								}
								
								else {
									LogLog.error("jd.kpl.open.item.getmobilewarestyleandjsbywareid错误返回值为"+jsonValues);
								}
							} catch (Exception e) {
								// 为防止此时同步的移动端样式数据中图片信息为空，把详情接口中的图片同步过来
								JSONObject picContent = filterForPic(responResult.getJSONObject("result").get("introduction").toString(), false);
								subMap.put("mobilDetailPic", picContent);
							}
						} else {
							// 为防止此时同步的移动端样式数据中图片信息为空，把详情接口中的图片同步过来
							JSONObject picContent = filterForPic(responResult.getJSONObject("result").get("introduction").toString(), false);
							subMap.put("mobilDetailPic", picContent);
						}
					}		
				}
				else {
					LogLog.error("biz.product.detail.query错误返回值为"+detailResult.toString());
				}
				listMap.add(subMap);
			}
			// 京东商品数据同步入惠家有库
			rsynProInfoToDataBase(erpPid,listMap, picMap, priceMap, saleMap, stateMap);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 获取sku的属性信息
	private static JSONObject getSkuProperties(String skuId) {
		// 封装参数为：颜色,款式(实际为颜色以外的拼装属性),sku主图(imagePath)
		JSONObject resultObj = new JSONObject();
		resultObj.put("saleValue", "");
		resultObj.put("color", "");
		resultObj.put("imagePath", "");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("skuId", skuId);
		String reResult = RsyncJingdongSupport.callGateway("jd.biz.product.getSimilarSku", paramMap);
		if (reResult.contains("jd_biz_product_getSimilarSku_response")) {
			try {
				JSONObject jsonValues = JsonUtil.getJsonValues(reResult);
				String returnResult = jsonValues.get("jd_biz_product_getSimilarSku_response").toString();
				com.alibaba.fastjson.JSONObject relObj = JSON.parseObject(returnResult);
				if (successCode.equals(relObj.get("resultCode").toString())) {
					JSONArray resultArray = relObj.getJSONArray("result");
					if (resultArray != null && resultArray.size() > 0) {
						StringBuffer pinzhuangSB = new StringBuffer();
						LOOP: for (Object object : resultArray) {
							net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(object.toString());
							String dim = jsonObject.get("dim").toString();
							// String saleName = jsonObject.getString("saleName");
							if ("1".equals(dim)) {
								// 颜色属性封装
								net.sf.json.JSONArray subJsonArray = jsonObject.getJSONArray("saleAttrList");
								for (Object object2 : subJsonArray) {
									net.sf.json.JSONObject subJsonObject = net.sf.json.JSONObject.fromObject(object2.toString());
									net.sf.json.JSONArray ssjsonArray = subJsonObject.getJSONArray("skuIds");
									if(ssjsonArray.contains(Long.parseLong(skuId))) {
										String color = subJsonObject.get("saleValue").toString().replace("&", " ");
										String imagePath = subJsonObject.get("imagePath").toString();
										resultObj.put("color", color);
										resultObj.put("imagePath", imageURLHead + imagePath);
										continue LOOP;
									}
								}

							} else {
								// 其他属性封装
								net.sf.json.JSONArray subJsonArray = jsonObject.getJSONArray("saleAttrList");
								for (Object object2 : subJsonArray) {
									net.sf.json.JSONObject subJsonObject = net.sf.json.JSONObject.fromObject(object2.toString());
									net.sf.json.JSONArray ssjsonArray = subJsonObject.getJSONArray("skuIds");
									if(ssjsonArray.contains(Long.parseLong(skuId))) {
										String saleValue = subJsonObject.get("saleValue").toString().replace("&", " ");
										pinzhuangSB.append(saleValue + " ");
										continue LOOP;
									}
								}

							}
						}
						resultObj.put("saleValue",
								StringUtils.isNotBlank(pinzhuangSB.toString().trim()) ? pinzhuangSB.toString().trim() : "");
					}

				}
			} catch (Exception e) {
				LogLog.error("京东商品属性接口jd.biz.product.getSimilarSku返回报错值为:" + reResult);
			}
		}
		return resultObj;
	}

	// 过滤cssContent中的图片作为详情图片
	private static JSONObject filterForPic(String detailContent, boolean flag) {
		StringBuffer picSB = new StringBuffer();
		if (!StringUtils.isBlank(detailContent) && flag) {
			Pattern p = Pattern.compile("background-image:url[(](.*?)[)]");
			Matcher mm = p.matcher(detailContent);
			while (mm.find()) {
				if (StringUtils.isNotBlank(mm.group(1).trim())) {
					if (picSB.length() > 0) {
						picSB.append("|");
					}
					if (mm.group(1).contains("http")) {
						picSB.append(mm.group(1));
					} else {
						picSB.append("http:" + mm.group(1));
					}
				}
			}
		} else {
			Pattern p = Pattern.compile("src=\"([^\"]+)\"");
			Matcher mm = p.matcher(detailContent);
			while (mm.find()) {
				if (StringUtils.isNotBlank(mm.group(1).trim())) {
					if (picSB.length() > 0) {
						picSB.append("|");
					}
					if (mm.group(1).contains("http")) {
						picSB.append(mm.group(1));
					} else {
						picSB.append("http:" + mm.group(1));
					}
				}
			}
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("mobilDetailPic", picSB);
		return jsonObject;
	}

	private static List<List<JSONObject>> sortSku(Map<String, String> cateMap, Set<String> allSkuCodeSet) {
		List<List<JSONObject>> ll = new ArrayList<>();
		for (String proId : cateMap.keySet()) {
			List<JSONObject> subList = new ArrayList<JSONObject>();
			org.json.JSONArray ja = new org.json.JSONArray(cateMap.get(proId));
			for(int i = 0; i < ja.length(); i++) {
				subList.add(ja.getJSONObject(i));
			}
			
			Collections.sort(subList, new Comparator<JSONObject>() {
				@Override
				public int compare(JSONObject o1, JSONObject o2) {
					String skuId1 = o1.get("skuId").toString();
					String skuId2 = o2.get("skuId").toString();
					return skuId1.compareTo(skuId2);
				}
			});

			ll.add(subList);
		}

		return ll;
	}

	private static MWebResult rsynProInfoToDataBase(String erpPid,List<Map<String, JSONObject>> listMap,
			Map<String, JSONArray> picMap, Map<String, JSONObject> priceMap, Map<String, String> saleMap,
			Map<String, String> stateMap) {
		MWebResult result = new MWebResult();
		try {
			PcProductinfo productinfo = new PcProductinfo();
			
			//实时接口查询
			/*String pId = proMap.get("baseInfo").get("erpPid").toString();
			String sqlWhere = "product_code_old = :product_code_old AND small_seller_code = :small_seller_code";
			List<MDataMap> productMapList = DbUp.upTable("pc_productinfo").queryAll("*", "", sqlWhere,
					new MDataMap("product_code_old", pId, "small_seller_code", Constants.SMALL_SELLER_CODE_JD));*/
			// 对应erpPid的商品已经存在则忽略
			int count = DbUp.upTable("pc_productinfo").count("product_code_old", erpPid, "small_seller_code",Constants.SMALL_SELLER_CODE_JD);
			
			if (count==0 && !listMap.isEmpty()) {
				// 如果一个商品下有任一个sku已经存在则不再走新增商品流程，走后续同步SKU的流程
				List<String> skuIdList = new ArrayList<String>();
				for (Map<String, JSONObject> map : listMap) {
					skuIdList.add("'"+map.get("baseInfo").get("skuId").toString()+"'");
				}
				String sSql = "select count(1) num from pc_skuinfo s,pc_productinfo p where s.product_code = p.product_code and p.small_seller_code = :small_seller_code and s.sell_productcode in("+StringUtils.join(skuIdList,",")+")";
				String subCount = DbUp.upTable("pc_skuinfo").dataSqlOne(sSql, new MDataMap("small_seller_code",Constants.SMALL_SELLER_CODE_JD)).get("num")+"";
				
				if("0".equals(subCount)) {
					
					setProductInfo(erpPid,productinfo, listMap, picMap, priceMap, saleMap, stateMap);
					if(productinfo.getProductSkuInfoList().size() > 0) {
						ProductService productService = new ProductService();
						StringBuffer error = new StringBuffer();
						int resultCode = productService.AddProductTx(productinfo, error, "");
						if (resultCode != 1) {
							LogLog.error("保存京东商品接口com.cmall.groupcenter.service.RsyncJDService报错");
						}
						rsyncJDProducToFlowMain(productinfo);
					}
				}

			} else {
				// 修改
		/*		for (MDataMap productMap : productMapList) {
					if (ArrayUtils.contains(new String[] { "4497153900060002", "4497153900060003" },
							productMap.get("product_status"))) {
						// 判断京东的商品名称是否变更
						if (!productMap.get("product_shortname")
								.equalsIgnoreCase(proMap.get("baseInfo").get("pname").toString())) {
							productMap.put("product_shortname", proMap.get("baseInfo").get("pname").toString());
							DbUp.upTable("pc_productinfo").dataUpdate(productMap, "product_shortname",
									"zid,product_code");
						}
					}
				}*/
			}
		} catch (Exception e) {
			result.inErrorMessage(918519034, "同步商品出错");
			e.printStackTrace();
		}
		return result;
	}

	private static void setProductInfo(String erpPid,PcProductinfo productinfo, List<Map<String, JSONObject>> listMap,
			Map<String, JSONArray> picMap, Map<String, JSONObject> priceMap, Map<String, String> saleMap,
			Map<String, String> stateMap) {
		// 数据封装
		JSONObject baseInfo = listMap.get(0).get("baseInfo");
		// JSONObject skuProperties = listMap.get(0).get("skuProperties");
		JSONObject detailInfo = listMap.get(0).get("detailInfo");
		JSONArray picArray = picMap.get(baseInfo.get("skuId"));
		JSONObject detailPic = listMap.get(0).get("mobilDetailPic");
		String price = "0";
		String jdPrice = "0";
		if (priceMap.keySet().contains(baseInfo.get("skuId"))) {
			price = priceMap.get(baseInfo.get("skuId")).get("price").toString();
			jdPrice = priceMap.get(baseInfo.get("skuId")).get("jdPrice").toString();
		}
		// 1.1商品属性封装
		String uid = UUID.randomUUID().toString().replace("-", "");
		String productName = (baseInfo.get("pname") == null ? ""
				: baseInfo.get("pname").toString().replaceAll("</?[^>]+>", ""));// 过滤html标签
		productinfo.setProductCode(WebHelper.upCode(JD_Pro_Head));
		productinfo.setUid(uid);
		productinfo.setSellProductcode(erpPid);
		productinfo.setProductCodeOld(erpPid);
		productinfo.setProductShortname(productName);
		productinfo.setBrandName(detailInfo.get("brandName") == null ? "" : detailInfo.get("brandName").toString());
		productinfo.setProdutName(productName);

		// 价格相关属
		productinfo.setMaxSellPrice(BigDecimal.valueOf(Double.valueOf(jdPrice)));
		productinfo.setMinSellPrice(BigDecimal.valueOf(Double.valueOf(jdPrice)));
		productinfo.setCostPrice(BigDecimal.valueOf(Double.valueOf(price)));
		productinfo.setMarketPrice(BigDecimal.valueOf(Double.valueOf(jdPrice)));

		// 税率固定
		String rate = detailInfo.get("taxInfo").toString()==null?"13":detailInfo.get("taxInfo").toString();
		 productinfo.setTaxRate(BigDecimal.valueOf(Double.valueOf(rate)/100));
		// 运费模板字段:0：惠家有包邮 1：不包邮 字段不确定
		productinfo.setTransportTemplate("0");

		// 主图地址在两个接口中有，详情接口和关联接口(对比取舍)
		productinfo.setMainPicUrl(imageURLHead + (detailInfo.get("imagePath") == null ? "" : detailInfo.get("imagePath").toString()));
		// productinfo.setMainPicUrl((baseInfo.getString("image_path")==null?"":baseInfo.getString("image_path")));
		productinfo.setSellerCode(MemberConst.MANAGE_CODE_HOMEHAS);
		productinfo.setSmallSellerCode(Constants.SMALL_SELLER_CODE_JD);
		// 商品待上架状态
		productinfo.setProductStatus("4497153900060001");
		productinfo.setValidate_flag("Y");// 新增字段，是否是虚拟商品
		productinfo.setProductWeight(new BigDecimal(Double.valueOf(baseInfo.get("weight") == null ? "" : baseInfo.get("weight").toString())));

		// 2.轮播图获取：单sku取全部图片,多sku拉所有
		// 2.1单sku商品
		if (listMap.size() == 1) {
			for (int i = 0; i < picArray.size(); i++) {
				PcProductpic pp = new PcProductpic();
				pp.setUid(UUID.randomUUID().toString().replace("-", ""));
				pp.setProductCode(productinfo.getProductCode());
				pp.setPicUrl(imageURLHead + picArray.getJSONObject(i).get("path").toString());
				productinfo.getPcPicList().add(pp);
			}
		}
		// 2.2多sku商品,拉所有
		else {
			for (Map<String, JSONObject> map : listMap) {
				JSONObject subBaseInfo = map.get("baseInfo");
				JSONArray subPicArray = picMap.get(subBaseInfo.get("skuId"));
				for (int i = 0; i < subPicArray.size(); i++) {
					PcProductpic pp = new PcProductpic();
					pp.setUid(UUID.randomUUID().toString().replace("-", ""));
					pp.setProductCode(productinfo.getProductCode());
					pp.setPicUrl(imageURLHead + subPicArray.getJSONObject(i).get("path").toString());
					productinfo.getPcPicList().add(pp);
				}
			}
		}

		// 4.商品属性(可直接在sku中操作完成)
		// setPcProductproperty(productinfo,skuProperties);

		// 4.描述封装
		PcProductdescription productdescription = new PcProductdescription();
		productdescription.setUid(uid);
		productdescription.setProductCode(productinfo.getProductCode());
		String descriptionInfo = (detailInfo.get("param") == null ? "" : detailInfo.get("param"))
				.toString().replaceAll("(<script).*(script>)", "")+(detailInfo.get("introduction") == null ? "" : detailInfo.get("introduction"))
				.toString().replaceAll("(<script).*(script>)", "")+"<br>"+(detailInfo.get("wareQD") == null ? "" : detailInfo.get("wareQD"))
				.toString().replaceAll("(<script).*(script>)", "");// 过滤js
		productdescription.setDescriptionInfo(descriptionInfo);

		// 4.1.取主sku的详情图片
		//productdescription.setDescriptionPic(detailPic.get("mobilDetailPic").toString());
		productdescription.setKeyword("");
		
		BigDecimal sellPrice,minSellPrice = null,maxSellPrice = null;
		for (Map<String, JSONObject> map : listMap) {
			JSONObject subBaseInfo = map.get("baseInfo");
			String subSkuId = subBaseInfo.get("skuId").toString();
			// JSONObject subDetailInfo = map.get("detailInfo");
			JSONObject subSkuProperties = map.get("skuProperties");
			
			// 4.2.取所同一类所有sku详情图片,然后拼接(数据图片太长太长,待议.)
			
			JSONObject subDetailPic = map.get("mobilDetailPic");
			String pic = subDetailPic.get("mobilDetailPic").toString();
			pic = StringUtils.trimToEmpty(pic).replace(":///", "://");
			if (StringUtils.isBlank(productdescription.getDescriptionPic())) {
				productdescription.setDescriptionPic(pic);
			} else {
				productdescription.setDescriptionPic(
						productdescription.getDescriptionPic() + "|" + pic);
			}
		  
			String subPrice = "0";
			String subJdPrice = "0";
			if (!priceMap.containsKey(subSkuId)) {
				continue;
			}
			subPrice = priceMap.get(subSkuId).get("price").toString();
			subJdPrice = priceMap.get(subSkuId).get("jdPrice").toString();

			// 5.sku信息封装
			ProductSkuInfo productSkuInfo = new ProductSkuInfo();
			String scode = WebHelper.upCode(JD_Sku_Head);
			productSkuInfo.setSkuCode(scode);
			// productSkuInfo.setSkuCodeOld(subSkuId);
			productSkuInfo.setProductCode(productinfo.getProductCode());
			// 接口中sku属性是不固定的,把查询出来的所属商品的固定属性进行sku属性封装（价格商品，sku同用）
			productSkuInfo.setSellPrice(BigDecimal.valueOf(Double.valueOf((subJdPrice))));
			productSkuInfo.setMarketPrice(BigDecimal.valueOf(Double.valueOf((subJdPrice))));
			productSkuInfo.setCostPrice(BigDecimal.valueOf(Double.valueOf((subPrice))));
			productSkuInfo.setSkuPicUrl(
					subBaseInfo.get("image_path") == null ? "" : subBaseInfo.get("image_path").toString());
			productSkuInfo.setSkuName(subBaseInfo.get("name") == null ? "" : subBaseInfo.get("name").toString());
			// 设置外部商品id改为skuId
			productSkuInfo.setSellProductcode(subSkuId);
			productSkuInfo.setStockNum(Integer.parseInt(storeNum));
			productSkuInfo.setFlagEnable("1");// 是否可用为可用
			productSkuInfo.setSellerCode(MemberConst.MANAGE_CODE_HOMEHAS);
			// 有京东sku上下架&可售状态共同决定惠家有sku的可售状态
			if (saleMap.get(subSkuId).equals("1") && stateMap.get(subSkuId).equals("1")) {
				productSkuInfo.setSaleYn("Y");
			} else {
				productSkuInfo.setSaleYn("N");
			}
			
			sellPrice = getSellPrice(productSkuInfo.getCostPrice(),productSkuInfo.getSellPrice());
			productSkuInfo.setSellPrice(sellPrice);
			
			// 计算商品最小销售价
			if(minSellPrice == null || minSellPrice.compareTo(sellPrice) > 0) {
				minSellPrice = sellPrice;
			}
			
			// 计算商品最大销售价
			if(maxSellPrice == null || maxSellPrice.compareTo(sellPrice) < 0) {
				maxSellPrice = sellPrice;
			}
			
			// 如果SKU匹配规格失败则忽略当前SKU
			if(!setPcProductproperty(productinfo, subSkuProperties, productSkuInfo)) {
				continue;
			}
			
			//5.1sku库存数入库
			MDataMap store = new MDataMap();
			store.put("store_code", "TDS1");
			store.put("sku_code", scode);
			store.put("stock_num", storeNum);
			DbUp.upTable("sc_store_skunum").dataInsert(store);
		}
		productinfo.setDescription(productdescription);
		
		if(minSellPrice != null && maxSellPrice != null) {
			productinfo.setMinSellPrice(minSellPrice);
			productinfo.setMaxSellPrice(maxSellPrice);
		}

		// 6.扩展信息封装
		PcProductinfoExt pcProductinfoExt = new PcProductinfoExt();
		pcProductinfoExt.setProductCodeOld(erpPid);
		pcProductinfoExt.setProductCode(productinfo.getProductCode());
		pcProductinfoExt.setPrchType("20");
		// 6.1无仓库编号
		pcProductinfoExt.setOaSiteNo("TDS1");
		pcProductinfoExt.setDlrId(Constants.SMALL_SELLER_CODE_JD);
		// 6.2供应商名称
		String sSql = "select seller_name from uc_sellerinfo where  small_seller_code=:small_seller_code";
		Map<String, Object> map = DbUp.upTable("uc_sellerinfo").dataSqlOne(sSql,
				new MDataMap("small_seller_code", Constants.SMALL_SELLER_CODE_JD));
		pcProductinfoExt.setDlrNm(map == null ? "" : map.get("seller_name").toString());
		pcProductinfoExt.setValidateFlag("Y");
		// 6.3贸易类型:一般贸易
		pcProductinfoExt.setProductTradeType(String.valueOf(2));
		pcProductinfoExt.setSettlementType("4497471600110002");// 特殊结算
		// 6.4采购类型：代销4497471600160001
		pcProductinfoExt.setPurchaseType("4497471600160001");
		productinfo.setPcProductinfoExt(pcProductinfoExt);

		// 7.商品保障数据
		MDataMap subMap = new MDataMap();
		subMap.put("product_code", productinfo.getProductCode());
		// 七天包退换(接口中暂相关数据,暂按能退换进行)
		TeslaOrderServiceJD teslaOrderServiceJD = new TeslaOrderServiceJD();
		List<ProductCheckRepVo> saleState = teslaOrderServiceJD.getSaleState(baseInfo.get("skuId").toString(),null);
		if(saleState!=null&&saleState.size()>0) {
			if(saleState.get(0).getIs7ToReturn()==0) {
				subMap.put("authority_logo_uid", TopConfig.Instance.bConfig("productcenter.authority_logo_sevenday_no"));
			}
			else {
				subMap.put("authority_logo_uid", TopConfig.Instance.bConfig("productcenter.authority_logo_sevenday"));	
			}
		}
		else {
			subMap.put("authority_logo_uid", TopConfig.Instance.bConfig("productcenter.authority_logo_sevenday_no"));
		}
		
		subMap.put("create_time", DateUtil.getSysDateTimeString());
		subMap.put("uid", UUID.randomUUID().toString().replace("-", ""));
		DbUp.upTable("pc_product_authority_logo").dataInsert(subMap);

		List<String[]> ppList = getParam(detailInfo.optString("param"));
		PcProductproperty pp;
		for(String[] v : ppList) {
			pp = new PcProductproperty();
			pp.setPropertyKey(v[0]);
			pp.setPropertyValue(v[1]);
			pp.setPropertyType("449736200004");
			productinfo.getPcProductpropertyList().add(pp);
		}
	}
	
	// 提取京东商品详情中的商品参数，每个参数以Key,Value的数组返回
	private static List<String[]> getParam(String text) {
		if(StringUtils.isBlank(text)) return new ArrayList<String[]>();
		
		Document jp = Jsoup.parse(text);
		
		List<String[]> list = new ArrayList<String[]>();
		Elements ets = jp.getElementsByTag("table");
		if(ets == null || ets.isEmpty()){
			return list;
		}
		
		Elements trs,tds;
		String key,val;
		for(int i = 0;i<ets.size();i++) {
			trs = ets.get(i).getElementsByTag("tr");
			for(int j = 0; j < trs.size(); j++) {
				tds = trs.get(j).getElementsByTag("td");
				if(tds.size() != 2) {
					continue;
				}
				
				key = StringUtils.trimToEmpty(tds.get(0).text());
				val = StringUtils.trimToEmpty(tds.get(1).text());
				
				if(StringUtils.isBlank(key) || StringUtils.isBlank(val)) {
					continue;
				}
				
				list.add(new String[]{key, val});
			}
		}
		
		return list;
	}

	private static boolean setPcProductproperty(PcProductinfo productinfo, JSONObject subSkuProperties,
			ProductSkuInfo productSkuInfo) {
		// 对sku主图重新进行赋值
		try {
			if (subSkuProperties != null && StringUtils.isNotBlank(subSkuProperties.get("imagePath").toString())) {
				productSkuInfo.setSkuPicUrl(subSkuProperties.get("imagePath").toString());
			}
		} catch (Exception e) {
			LogLog.error("报错subSkuProperties值为:" + subSkuProperties);
		}

		// 把京东商品认为分成两个固定的属性
		String colorValue = StringUtils.isBlank(subSkuProperties.get("color").toString()) ? "共同"
				: subSkuProperties.get("color").toString();
		String styleValue = StringUtils.isBlank(subSkuProperties.get("saleValue").toString()) ? "共同"
				: subSkuProperties.get("saleValue").toString();

		if("共同".equals(colorValue) && "共同".equals(styleValue)) {
			return false;
		}
		
		String colorKey = getPropertyCode(productinfo.getProductCode(), SKU_KEY_COLOR, "颜色", colorValue);
		String styleKey = getPropertyCode(productinfo.getProductCode(), SKU_KEY_STYLE, "款式", styleValue);

		productSkuInfo.setSkuKey("4497462000010001=" + colorKey + "&4497462000020001=" + styleKey);
		productSkuInfo.setSkuValue("颜色=" + colorValue + "&款式=" + styleValue);

		productinfo.getProductSkuInfoList().add(productSkuInfo);

		return true;
	}

	private static String getPropertyCode(String productCode, String keyCode, String keyName, String styleValue) {
		// 校验当前的keycode ，如果是已经存在的
		MDataMap mdProperty = DbUp.upTable("pc_productproperty").oneWhere("property_code", "", "", "product_code",
				productCode, "property_keycode", keyCode, "property_value", styleValue);
		if (mdProperty != null)
			return mdProperty.get("property_code");
		// 取属性的最大值
		MDataMap maxCodeMap = DbUp.upTable("pc_productproperty").oneWhere("max(property_code) code", "", "",
				"product_code", productCode, "property_keycode", keyCode);
		String newCode = null;
		if (maxCodeMap == null || StringUtils.isBlank(maxCodeMap.get("code"))) {
			// 默认追加初始后缀
			newCode = keyCode + "0001";
		} else {
			// 在最大的基础上加1
			newCode = new BigDecimal(maxCodeMap.get("code")).add(BigDecimal.ONE).toString();
		}

		MDataMap insertMap = new MDataMap();
		insertMap.put("product_code", productCode);
		insertMap.put("property_keycode", keyCode);
		insertMap.put("property_code", newCode);
		insertMap.put("property_key", keyName);
		insertMap.put("property_value", styleValue);
		if (SKU_KEY_COLOR.equals(keyCode)) {
			insertMap.put("property_type", ProductService.ColorHead);
		} else if (SKU_KEY_COLOR.equals(keyCode)) {
			insertMap.put("property_type", ProductService.MainHead);
		}
		insertMap.put("type", "2");
		DbUp.upTable("pc_productproperty").dataInsert(insertMap);
		return newCode;
	}

	private static void rsyncJDProducToFlowMain(PcProductinfo productinfo) {

		// 进行审批流程添加
		String PAGEURL = "page_preview_v_pc_productDetailInfo?zw_f_product_code=";
		String pCode = productinfo.getProductCode();
		List<Map<String, Object>> lm = DbUp.upTable("sc_flow_main").dataSqlList(
				"select * from sc_flow_main where outer_code=:outer_code", new MDataMap("outer_code", pCode));
		// 审批流中已经存在，不做入流处理
		if (lm != null && lm.size() > 0)
			return;
		// 1.同步审批流
		MDataMap paramMap = new MDataMap();
		String createTime = DateUtil.getSysDateTimeString();
		paramMap.put("uid", UUID.randomUUID().toString().replace("-", ""));
		paramMap.put("flow_code", WebHelper.upCode("SF"));
		paramMap.put("flow_type", "449717230016");
		// 创建更新默认为京东特定商户
		paramMap.put("creator", Constants.SMALL_SELLER_CODE_JD);
		paramMap.put("updator", Constants.SMALL_SELLER_CODE_JD);
		paramMap.put("create_time", createTime);
		paramMap.put("update_time", createTime);
		paramMap.put("outer_code", pCode);
		paramMap.put("flow_title", pCode);
		paramMap.put("flow_url", PAGEURL + pCode + "_1");
		paramMap.put("flow_remark", "");
		paramMap.put("flow_isend", "0");
		paramMap.put("current_status", "4497172300160003");
		paramMap.put("last_status", "4497172300160002");
		paramMap.put("next_operators", "46770318000100030005");
		paramMap.put("next_operator_status", "4677031800010001:4497172300160008;4677031800010001:4497172300160013");
		paramMap.put("next_operator_id", "");
		DbUp.upTable("sc_flow_main").dataInsert(paramMap);
		// 2.同步商品流
		MDataMap productFlow = new MDataMap();
		JsonHelper<PcProductinfo> pHelper = new JsonHelper<PcProductinfo>();
		productFlow.put("flow_code", WebHelper.upCode(ProductService.ProductFlowHead));
		productFlow.put("product_code", pCode);
		productFlow.put("product_json", pHelper.ObjToString(productinfo));
		productFlow.put("flow_status", SkuCommon.FlowStatusInit);
		productFlow.put("creator", Constants.SMALL_SELLER_CODE_JD);
		productFlow.put("create_time", DateUtil.getSysDateTimeString());
		productFlow.put("updator", Constants.SMALL_SELLER_CODE_JD);
		productFlow.put("update_time", DateUtil.getSysDateTimeString());
		DbUp.upTable("pc_productflow").dataInsert(productFlow);
	}

	//public static void rsynProState(Map<String, String> cateMap, Set<String> skuCodeSet, Set<String> allSkuCodeSet) {
	public static void rsynProState(Map<String, String> paramMap) {	
	   // 获取商品池中存在的sku
		//skuCodeSet.retainAll(allSkuCodeSet);
		List<String> relSkuList = new ArrayList<String>(paramMap.keySet());

		// 获取京东接口中的名义"商品id"集合
		//Set<String> erpPidSet = cateMap.keySet();

		List<String> messageList = new ArrayList<String>();
		// 1.价格批量查询
		Map<String, JSONObject> priceMap = batchQuerySkuPrice(relSkuList);
		// 2.可售状态
		Map<String, String> saleMap = batchQuerySkuIfSale(relSkuList);
		// 3.上下架状态
		Map<String, String> stateMap = batchQuerySkuState(relSkuList);
		//4.同属分类
		Map<String, List<JSONObject>> cateMap = cateSkuInfo(paramMap,paramMap.keySet());
		
		for (String erpPid : cateMap.keySet()) {
			// 4.对比考拉的判断条件：
			// 4.1获取同一"商品下的所有sku对象"
			List<JSONObject> skuArray = cateMap.get(erpPid);
			String proId = erpPid;

			// 4.2.兼容调编情况下惠家有多个商品编号对应一个京东编号的情况，排除已经强制下架的商品
			String sqlWhere = "product_status != '4497153900060004' AND product_code_old = :product_code_old AND small_seller_code = :small_seller_code";
			List<MDataMap> productMapList = DbUp.upTable("pc_productinfo").queryAll("*", "", sqlWhere,
					new MDataMap("product_code_old", proId, "small_seller_code", Constants.SMALL_SELLER_CODE_JD));

			// 5.开始循环判断
			for (MDataMap productInfo : productMapList) {
				String productCode = productInfo.get("product_code");
				String productStatus = productInfo.get("product_status");

				boolean downFlag = false;
				boolean needRefresh = false;
				boolean allSkuSaleN = false;

				// 5.1惠家有SKU编号
				String skuCode;
				String saleYn;
				String picUrl = "";
				for (JSONObject skuJsonObj : skuArray) {
					String jdSkuId = skuJsonObj.get("skuId").toString();
					boolean skuAvailabel = true; // 商品是否还在商品池
					if (!relSkuList.contains(jdSkuId))
						continue;
					if (!priceMap.containsKey(jdSkuId)) {
						// 取不到价格有可能是京东商品池里面已经没有这个商品了
						// 检查sku是否不在商品池了
						if(checkSkuAvailable(jdSkuId)){
							// 在商品池但是未取到价格则忽略更新
							continue;
						} else {
							// 不再商品池的sku需要标识一下，后面设置为不可售
							skuAvailabel = false;
						}
					}
					
					MDataMap skuInfo = null;
					String sSql = "select s.* from pc_skuinfo s,pc_productinfo p where "
							+ " s.product_code = p.product_code "
							+ " and p.product_status != '4497153900060004' "
							+ " and p.small_seller_code = :small_seller_code "
							+ " and s.sell_productcode = :jdSkuId";
					List<Map<String, Object>> skuList = DbUp.upTable("pc_skuinfo").dataSqlList(sSql, new MDataMap("jdSkuId", jdSkuId,"small_seller_code", Constants.SMALL_SELLER_CODE_JD));
					// 一个京东sku如果在惠家有对应多条SKU记录，优先取跟当前更新商品编号一致的数据
					for(Map<String, Object> skuMap : skuList) {
						if(productCode.equals(skuMap.get("product_code"))) {
							skuInfo = new MDataMap(skuMap);
							break;
						}
					}
					
					// 不在商品池的sku直接忽略，不进入pc_skuinfo表
					if(skuInfo == null && !skuAvailabel) {
						continue;
					}
					
					// 如果存在对应的sku存在但未匹配到跟当前商品编号一致的数据，则可能是白名单中sku更改了erpPid。默认更新第一条数据
					if(skuInfo == null && !skuList.isEmpty()) {
						skuInfo = new MDataMap(skuList.get(0));
					}
					
					String jdPrice = "0";
					String price = "0";
					
					if(skuAvailabel) {
						jdPrice = priceMap.get(jdSkuId).get("jdPrice").toString();
						price = priceMap.get(jdSkuId).get("price").toString();
					} else {
						// 取不到价格时，默认是商品原价格
						jdPrice = skuInfo.get("sell_price").toString();
						price = skuInfo.get("cost_price").toString();
					}
					
					jdPrice = getSellPrice(new BigDecimal(price), new BigDecimal(jdPrice)).toString();
				
					// 5.2京东商品可售与否/是否下架
					if ("1".equals(saleMap.get(jdSkuId)) && "1".equals(stateMap.get(jdSkuId).toString()) && skuAvailabel) {
						saleYn = "Y";
					} else {
						saleYn = "N";
					}
					
					// 5.3.商品属性接口调用
					JSONObject skuProperties = getSkuProperties(jdSkuId);
					if (!StringUtils.isBlank(StringUtils.trimToEmpty(skuProperties.get("imagePath").toString()))) {
						picUrl = StringUtils.trimToEmpty(skuProperties.get("imagePath").toString());
					}

					// 5.4不存在则保存
					if (skuInfo == null) {
						
						skuCode = WebHelper.upCode(JD_Sku_Head);
						skuInfo = new MDataMap();
						skuInfo.put("sku_picurl", picUrl);
						skuInfo.put("sku_code", skuCode);
						skuInfo.put("product_code", productCode);
						skuInfo.put("sell_price", jdPrice);
						skuInfo.put("market_price", jdPrice);
						skuInfo.put("cost_price", price);
						skuInfo.put("stock_num", storeNum);
						skuInfo.put("sku_name", skuJsonObj.get("name").toString());
						skuInfo.put("sell_productcode", skuJsonObj.get("skuId").toString());
						skuInfo.put("seller_code", "SI2003");
						skuInfo.put("sale_yn", saleYn);
						skuInfo.put("flag_enable", "1");
						skuInfo.put("sku_key", "color_id=0&style_id=0");
						skuInfo.put("sku_keyvalue", "颜色=共同&款式=共同");

						// 把京东商品认为分成两个固定的属性
						String colorValue = StringUtils.isBlank(skuProperties.get("color").toString()) ? "共同"
								: skuProperties.get("color").toString();
						String styleValue = StringUtils.isBlank(skuProperties.get("saleValue").toString()) ? "共同"
								: skuProperties.get("saleValue").toString();
						
						if("共同".equals(colorValue) && "共同".equals(styleValue)) {
							continue;
						}

						String colorKey = getPropertyCode(productCode, SKU_KEY_COLOR, "颜色", colorValue);
						String styleKey = getPropertyCode(productCode, SKU_KEY_STYLE, "款式", styleValue);

						skuInfo.put("sku_key", "4497462000010001=" + colorKey + "&4497462000020001=" + styleKey);
						skuInfo.put("sku_keyvalue", "颜色=" + colorValue + "&款式=" + styleValue);

						DbUp.upTable("pc_skuinfo").dataInsert(skuInfo);

						// 查询一下刚插入的数据，供后续使用
						skuInfo = DbUp.upTable("pc_skuinfo").one("sku_code", skuCode, "product_code", productCode);
						
						// 更新一下商品的最小和最大价格
						MDataMap rangePriceMap = DbUp.upTable("pc_skuinfo").oneWhere("min(sell_price) minSellPrice, max(sell_price) maxSellPrice", "", "", "product_code", productCode, "sale_yn", "Y", "flag_enable", "1");
						if(!rangePriceMap.isEmpty()) {
							List<String> list = new ArrayList<String>();
							MDataMap m = new MDataMap();
							m.put("product_code", productCode);
							if(rangePriceMap.get("minSellPrice").compareTo(skuInfo.get("sell_price")) > 0) {
								list.add("min_sell_price");
								m.put("min_sell_price", skuInfo.get("sell_price"));
							}
							if(rangePriceMap.get("maxSellPrice").compareTo(skuInfo.get("sell_price")) < 0) {
								list.add("max_sell_price");
								m.put("max_sell_price", skuInfo.get("sell_price"));
							}
							
							// 有变更的时候才更新
							if(!list.isEmpty()) {
								DbUp.upTable("pc_productinfo").dataUpdate(m, StringUtils.join(list,","), "product_code");
							}
						}
					} else {
						skuCode = skuInfo.get("sku_code");
					}

					boolean updateSkuFlag = false;
					MDataMap updateSkuInfo = new MDataMap();
					updateSkuInfo.put("zid", skuInfo.get("zid"));
					updateSkuInfo.put("sku_code", skuInfo.get("sku_code"));
					// 更新一下成本价，并且记录一下商品需要下架
					if (new BigDecimal(price).compareTo(new BigDecimal(skuInfo.get("cost_price"))) != 0) {
						updateSkuInfo.put("cost_price", price);
						updateSkuFlag = true;
						
						// 成本价变高时商品下架
						if(new BigDecimal(price).compareTo(new BigDecimal(skuInfo.get("cost_price"))) > 0) {
							// 如果有正在进行或即将开始的活动则检查一下会不会出现负毛利，如果出现则再下架商品
							BigDecimal minPrice = getSkuEventMinPrice(skuInfo.get("sku_code"));
							BigDecimal sellPrice = new BigDecimal(skuInfo.get("sell_price"));
							
							// 取销售最低价
							minPrice = (minPrice != null && minPrice.compareTo(sellPrice) < 0) ? minPrice : sellPrice;
							
							// 如果新的成本价比最低销售价高可能产生负毛利则商品下架
							if(new BigDecimal(price).compareTo(minPrice) >= 0) {
								downFlag = true;
							}
						}
					}

					// 可售状态，以京东为准
					if (!saleYn.equalsIgnoreCase(skuInfo.get("sale_yn"))) {
						updateSkuFlag = true;
						updateSkuInfo.put("sale_yn", saleYn);
					}

					// 商品图片
					if (StringUtils.isNotBlank(picUrl) && !picUrl.equals(skuInfo.get("sku_picurl"))) {
						updateSkuFlag = true;
						updateSkuInfo.put("sku_picurl", picUrl);
					}

					if (updateSkuFlag) {
						DbUp.upTable("pc_skuinfo").dataUpdate(updateSkuInfo, "", "zid,sku_code");
						needRefresh = true;
					}

					// 刷新库存(为固定假数据,无实际意义)
					MDataMap store = DbUp.upTable("sc_store_skunum").one("store_code", "TDS1", "sku_code", skuCode);
					if (store == null) {
						store = new MDataMap();
						store.put("store_code", "TDS1");
						store.put("sku_code", skuCode);
						store.put("stock_num", storeNum);
						DbUp.upTable("sc_store_skunum").delete("sku_code", skuCode);
						DbUp.upTable("sc_store_skunum").dataInsert(store);
						PlusHelperNotice.onChangeSkuStock(skuCode);
					}
				}

				// 是否商品下面有可售的SKU
				if (DbUp.upTable("pc_skuinfo").count("product_code", productCode, "sale_yn", "Y") == 0) {
					allSkuSaleN = true;
				}

				// 如果商品是在架状态且需要下架则执行下架操作
				if ("4497153900060002".equalsIgnoreCase(productStatus)) {
					if (downFlag||allSkuSaleN) {
						String remark = "";
						String fromStatus = "4497153900060002";
						String toStatus = "4497153900060003";
						String flowType = "449715390006";
						String userCode = "RsyncJDService";

					if (downFlag) {
							remark = "SKU成本价变更商品下架";
						}
						if (allSkuSaleN) {
							if (StringUtils.isBlank(remark)) {
								remark = "无可售SKU/SKU下架";
							} else {
								remark = ",无可售SKU/SKU下架";
							}
						}
						new FlowBussinessService().ChangeFlow(productInfo.get("uid"), flowType, fromStatus, toStatus,
								userCode, remark, new MDataMap());
						messageList.add(String.format("[%s][%s][%s]", productCode,productInfo.get("product_name"),remark));
					}
				}

				if (needRefresh) {
					PlusHelperNotice.onChangeProductInfo(productCode);
				}
			}
		}
		// 发送通知
		if(!messageList.isEmpty()) {
			WxGateSupport support = new WxGateSupport();
			String receives = support.bConfig("groupcenter.jd_notice_receives_product");
			List<String> list = support.queryOpenId(receives);
			String msg = StringUtils.join(messageList,"\r\n");
			for(String v : list) {
				support.sendWarnCountMsg("商品变更通知", "京东商品下架", v, msg);
			}
		}
	}

	// 封装一个获取json字符串中的key值的方法
	public static net.sf.json.JSONObject getJSONStrVal(String jsonString, String key) {
		com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(jsonString);
		Object object = jsonObject.get(key);
		net.sf.json.JSONObject subResult = net.sf.json.JSONObject.fromObject(object.toString());
		return subResult;
	}

	public static String getJsonValue(String jsonString, String key) {
		JSONObject jsonValues2 = JsonUtil.getJsonValues(jsonString);
		String string = jsonValues2.get(key).toString();
		return string;
	}
	
	public static MWebResult changeSkuPrice(String skuId) {
		MWebResult result = new MWebResult();
		result.setResultCode(0);
		List<String> messageList = new ArrayList<String>();
		//查询本地京东选品库
		Map<String, Object> map = DbUp.upTable("pc_jingdong_choosed_products").dataSqlOne("select * from pc_jingdong_choosed_products where jd_sku_id=:jd_sku_id and is_enabled='1'", new MDataMap("jd_sku_id",skuId));
		if(map!=null) {
			String erpPid = map.get("jd_erppid").toString();
			Map<String, Object> paramtMap  = new HashMap<String, Object> ();
			Map<String, Object> resultMap  = new HashMap<String, Object> ();
			paramtMap.put("sku", skuId);	
			String returnStr = RsyncJingdongSupport.callGateway("biz.price.sellPrice.get", paramtMap);
			if(returnStr.contains("biz_price_sellPrice_get_response")) {
				JSONObject priceResult = JsonUtil.getJsonValues(RsyncJDService.getJsonValue(returnStr,"biz_price_sellPrice_get_response"));
				if (successCode.equals(priceResult.get("resultCode"))) {
					JSONArray array = JSON.parseArray((RsyncJDService.getJsonValue(priceResult.toString(), "result")));
					if (array != null && array.size() > 0) {
						for (int i = 0; i < array.size(); i++) {
							resultMap.put(RsyncJDService.getJsonValue(array.get(i).toString(), "skuId"),JsonUtil.getJsonValues(array.get(i).toString()));
						
						}
					}
				}
			}
			else {
				LogLog.error("biz.price.sellPrice.get错误返回值为"+returnStr);
			}
			
			if(resultMap.containsKey(skuId)) {
				Object object = resultMap.get(skuId);
				net.sf.json.JSONObject jsonObj = net.sf.json.JSONObject.fromObject(object.toString());
				String price = jsonObj.getString("price");
				//兼容调编情况下惠家有多个商品编号对应一个京东编号的情况，排除已经强制下架的商品
				String sqlWhere = "product_status != '4497153900060004' AND product_code_old = :product_code_old AND small_seller_code = :small_seller_code";
				List<MDataMap> productMapList = DbUp.upTable("pc_productinfo").queryAll("*", "", sqlWhere,
						new MDataMap("product_code_old", erpPid, "small_seller_code", Constants.SMALL_SELLER_CODE_JD));
				MDataMap skuInfo;
				for (MDataMap productInfo : productMapList) {
					String productCode = productInfo.get("product_code");
					String productStatus = productInfo.get("product_status");
					skuInfo = DbUp.upTable("pc_skuinfo").one("product_code", productCode, "sell_productcode", skuId);
				 if (skuInfo != null) {	
					 	int priceDiff = new BigDecimal(price).compareTo(new BigDecimal(skuInfo.get("cost_price")));
					 	if(priceDiff!=0) {
							if (priceDiff > 0) {
								MDataMap updateSkuInfo = new MDataMap();
								updateSkuInfo.put("cost_price", price);
								updateSkuInfo.put("zid", skuInfo.get("zid"));
								updateSkuInfo.put("sku_code", skuInfo.get("sku_code"));
								DbUp.upTable("pc_skuinfo").dataUpdate(updateSkuInfo, "", "zid,sku_code");
								result.setResultCode(1);
								
								// 如果有正在进行或即将开始的活动则检查一下会不会出现负毛利，如果出现则再下架商品
								BigDecimal minPrice = getSkuEventMinPrice(skuInfo.get("sku_code"));
								BigDecimal sellPrice = new BigDecimal(skuInfo.get("sell_price"));
								// 取销售最低价
								minPrice = (minPrice != null && minPrice.compareTo(sellPrice) < 0) ? minPrice : sellPrice;
								// 如果新的成本价比最低销售价高可能产生负毛利则商品下架
								if(new BigDecimal(price).compareTo(minPrice) >= 0 && "4497153900060002".equalsIgnoreCase(productStatus)) {
									String remark = "";
									String fromStatus = "4497153900060002";
									String toStatus = "4497153900060003";
									String flowType = "449715390006";
									String userCode = "RsyncJDService";
									remark = "SKU成本价变更商品下架";
									new FlowBussinessService().ChangeFlow(productInfo.get("uid"), flowType, fromStatus, toStatus,
											userCode, remark, new MDataMap());
									messageList.add(String.format("[%s][%s][%s]", productCode,productInfo.get("product_name"),remark)); 
								}
								
							}
							else {
								MDataMap updateSkuInfo = new MDataMap();
								updateSkuInfo.put("cost_price", price);
								updateSkuInfo.put("zid", skuInfo.get("zid"));
								updateSkuInfo.put("sku_code", skuInfo.get("sku_code"));
								DbUp.upTable("pc_skuinfo").dataUpdate(updateSkuInfo, "", "zid,sku_code");
								result.setResultCode(1);
							}
							PlusHelperNotice.onChangeProductInfo(productCode);
					 	}else {
					 		//价格未变动
					 		result.setResultCode(1);
					 	}
					}else {
						//选品不存在
						result.setResultCode(1);
					}
				}			
			}
		}
		else {
			//选品不存在
			result.setResultCode(1);
		}
		// 发送通知
		if(!messageList.isEmpty()) {
			WxGateSupport support = new WxGateSupport();
			String receives = support.bConfig("groupcenter.jd_notice_receives_product");
			List<String> list = support.queryOpenId(receives);
			String msg = StringUtils.join(messageList,"\r\n");
			for(String v : list) {
				support.sendWarnCountMsg("商品变更通知", "京东商品下架", v, msg);
			}
		}
		return result;
	}
	
	public static MWebResult sysnJDProductState(String skuId) {
		MWebResult result = new MWebResult();
		result.setResultCode(0);
		List<String> messageList = new ArrayList<String>();
		//查询本地京东选品库
		Map<String, Object> map = DbUp.upTable("pc_jingdong_choosed_products").dataSqlOne("select * from pc_jingdong_choosed_products where jd_sku_id=:jd_sku_id and is_enabled='1'", new MDataMap("jd_sku_id",skuId));
		if(map!=null) {
			String erpPid = map.get("jd_erppid").toString();
			Map<String, Object> paramtMap  = new HashMap<String, Object> ();
			Map<String, Object> resultMap  = new HashMap<String, Object> ();
			paramtMap.put("skuIds", skuId);
			// 查询可售验证接口
			JSONObject jsonValues = JsonUtil.getJsonValues(RsyncJingdongSupport.callGateway("biz.product.sku.check", paramtMap));
			if(jsonValues.toString().contains("biz_product_sku_check_response")) {
				JSONObject stateResult = JsonUtil.getJsonValues(jsonValues.get("biz_product_sku_check_response").toString());
				if (successCode.equals(stateResult.get("resultCode"))) {
					JSONArray array = JSON.parseArray(RsyncJDService.getJsonValue(stateResult.toString(), "result"));
					if (array != null && array.size() > 0) {
						for (int i = 0; i < array.size(); i++) {
						    resultMap.put(RsyncJDService.getJsonValue(array.get(i).toString(), "skuId"),RsyncJDService.getJsonValue(array.get(i).toString(), "saleState"));
						}
					}
				}
				if(resultMap.containsKey(skuId)) {
					String state = resultMap.get(skuId).toString();
					//兼容调编情况下惠家有多个商品编号对应一个京东编号的情况，排除已经强制下架的商品
					String sqlWhere = "product_status != '4497153900060004' AND product_code_old = :product_code_old AND small_seller_code = :small_seller_code";
					List<MDataMap> productMapList = DbUp.upTable("pc_productinfo").queryAll("*", "", sqlWhere,
							new MDataMap("product_code_old", erpPid, "small_seller_code", Constants.SMALL_SELLER_CODE_JD));
					MDataMap skuInfo;
					for (MDataMap productInfo : productMapList) {
						String productCode = productInfo.get("product_code");
						String productStatus = productInfo.get("product_status");
						skuInfo = DbUp.upTable("pc_skuinfo").one("product_code", productCode, "sell_productcode", skuId);
					 if (skuInfo != null) {	
							if ("0".equals(state)) {
								MDataMap updateSkuInfo = new MDataMap();
								updateSkuInfo.put("sale_yn", "N");
								updateSkuInfo.put("zid", skuInfo.get("zid"));
								updateSkuInfo.put("sku_code", skuInfo.get("sku_code"));
								int flag = DbUp.upTable("pc_skuinfo").dataUpdate(updateSkuInfo, "", "zid,sku_code");
								if(flag!=0) {
									if ("4497153900060002".equalsIgnoreCase(productStatus)) {
										String remark = "";
										String fromStatus = "4497153900060002";
										String toStatus = "4497153900060003";
										String flowType = "449715390006";
										String userCode = "RsyncJDService";
										remark = "京东SKU不可售,变更惠家有商品下架";
										new FlowBussinessService().ChangeFlow(productInfo.get("uid"), flowType, fromStatus, toStatus,
												userCode, remark, new MDataMap());
										messageList.add(String.format("[%s][%s][%s]", productCode,productInfo.get("product_name"),remark)); 
									}	
									result.setResultCode(1);
								}
							}
							else {
								result.setResultCode(1);
							}
							                                                                                                                                                                                                                                                                                                                                                                                                                                             
						}
					}
					
				}
			}
			else {
				String sqlWhere = "product_status != '4497153900060004' AND product_code_old = :product_code_old AND small_seller_code = :small_seller_code";
				List<MDataMap> productMapList = DbUp.upTable("pc_productinfo").queryAll("*", "", sqlWhere,
						new MDataMap("product_code_old", erpPid, "small_seller_code", Constants.SMALL_SELLER_CODE_JD));
				MDataMap skuInfo;
				for (MDataMap productInfo : productMapList) {
					String productCode = productInfo.get("product_code");
					String productStatus = productInfo.get("product_status");
					skuInfo = DbUp.upTable("pc_skuinfo").one("product_code", productCode, "sell_productcode", skuId);
				 if (skuInfo != null) {	
							MDataMap updateSkuInfo = new MDataMap();
							updateSkuInfo.put("sale_yn", "N");
							updateSkuInfo.put("zid", skuInfo.get("zid"));
							updateSkuInfo.put("sku_code", skuInfo.get("sku_code"));
							int flag = DbUp.upTable("pc_skuinfo").dataUpdate(updateSkuInfo, "", "zid,sku_code");
							if(flag!=0) {
								if ("4497153900060002".equalsIgnoreCase(productStatus)) {
									String remark = "";
									String fromStatus = "4497153900060002";
									String toStatus = "4497153900060003";
									String flowType = "449715390006";
									String userCode = "RsyncJDService";
									remark = "京东SKU删除,变更惠家有商品下架";
									new FlowBussinessService().ChangeFlow(productInfo.get("uid"), flowType, fromStatus, toStatus,
											userCode, remark, new MDataMap());
									messageList.add(String.format("[%s][%s][%s]", productCode,productInfo.get("product_name"),remark));
								}	
								result.setResultCode(1);
							}
					}
				}
				
				//接口返回中查询不到，表明已经删除
				result.setResultCode(1);
			}
		}
		else {
			//推送的商品在本地选品中不存在
			result.setResultCode(1);
		}
		// 发送通知
		if(!messageList.isEmpty()) {
			WxGateSupport support = new WxGateSupport();
			String receives = support.bConfig("groupcenter.jd_notice_receives_product");
			List<String> list = support.queryOpenId(receives);
			String msg = StringUtils.join(messageList,"\r\n");
			for(String v : list) {
				support.sendWarnCountMsg("商品变更通知", "京东商品下架", v, msg);
			}
		}
		return result;
	}

	
	public static void startSysn(Map<String, String> paramMap) {
		Set<String> skuIds = paramMap.keySet();
		// 1.对比确定商品所有sku在推送范围内
		List<String> relSkuList = new ArrayList<String>(skuIds);
		List<String> subRelSkuList = new ArrayList<String>();
		Map<String, String> subParaMap = new HashMap<String, String>();
		Set<String> subSkuIds = new HashSet<String>();
		int count = 0;
		boolean flag = false;
		for (String sId : relSkuList) {
			count = DbUp.upTable("pc_productinfo").count("product_code_old",paramMap.get(sId),"small_seller_code", Constants.SMALL_SELLER_CODE_JD);
			if(count>0) {
				continue;
			}
			else {
				subRelSkuList.add(sId);
				subParaMap.put(sId, paramMap.get(sId));
				subSkuIds.add(sId);
				flag=true;
			}
		}		
		if(flag) {
			// 2.对图片进行批量查询,提高接口调用性能
			Map<String, JSONArray> picMap = batchQuerySkuPic(subRelSkuList);
			// 3.对价格进行批量查询,提高接口调用性能
			Map<String, JSONObject> priceMap = batchQuerySkuPrice(subRelSkuList);
			// 4.可售状态
			Map<String, String> saleMap = batchQuerySkuIfSale(subRelSkuList);
			// 5.上下架状态
			Map<String, String> stateMap = batchQuerySkuState(subRelSkuList);
			//6.获取基础关联数据
			// 根据白名单表中的erpPid对sku进行分组
			Map<String, List<JSONObject>> cateMap = cateSkuInfo(subParaMap,subSkuIds);
			//List<List<JSONObject>> ll = sortSku(cateMap, subSkuIds);
			for(Entry<String, List<JSONObject>>  entry : cateMap.entrySet()) {
				// 6.封装完整京东商品数据并开启同步
				setIntegralJDProInfo(entry.getKey(),entry.getValue(), picMap, priceMap, saleMap, stateMap);
			}
		}
	}

	private static Map<String, List<JSONObject>> cateSkuInfo(Map<String, String> paramMap,Set<String> skuIds) {
		
		// 临时变量，保存已经拿到的SKU编号，减少重复请求
		Map<String,String> skuFilterMap = new HashMap<String, String>();
		// erppid -> skuList
		Map<String, List<JSONObject>> cateMap = new HashMap<String, List<JSONObject>>();
		for (String skuId : skuIds) {
			if(!skuFilterMap.containsKey(skuId)) {
				List<String> setParam = new ArrayList<>();
				setParam.add(skuId);
				Map<String, Object> subParamMap = new HashMap<String, Object>();
				subParamMap.put("set", setParam);
				String returnRelsult = RsyncJingdongSupport.callGateway("jd.kpl.open.shopinfo.sku",
						subParamMap);
				if(returnRelsult.contains("jd_kpl_open_shopinfo_sku_response")) {
					net.sf.json.JSONObject jObRespon = RsyncJDService.getJSONStrVal(returnRelsult,
							"jd_kpl_open_shopinfo_sku_response");
					if(jObRespon != null) {
						net.sf.json.JSONObject jsonObject = jObRespon.getJSONObject("result");
						net.sf.json.JSONArray resList = jsonObject.optJSONArray(skuId);
						net.sf.json.JSONObject skuObj = null;
						
						// 根据erpPid获取缓存的skulist
						List<JSONObject> skuArray = null;
						
						String erpPid = null;
						// 循环一下同类sku列表
						for(int i = 0,j = resList.size(); i < j; i++) {
							skuObj = resList.getJSONObject(i);
							erpPid = paramMap.get(skuObj.optString("skuId"));
							
							// 如果sku在白名单中，把同一个erpPid的放到相同的list里面
							if(StringUtils.isNotBlank(erpPid)) {
								skuArray = cateMap.get(erpPid);
								if(skuArray == null) {
									skuArray = new ArrayList<JSONObject>();
								}
								
								skuArray.add(new JSONObject(skuObj.toString()));
								cateMap.put(erpPid, skuArray);
							}
							
							skuFilterMap.put(skuObj.optString("skuId"), "");
						}
					}
				}
				skuFilterMap.put(skuId, "");
			}
		}
		
		return cateMap;
	}
	

private static String checkData(String skus) {
		// TODO Auto-generated method stub
	JSONArray resultArray = JSON.parseArray(skus);
	if (resultArray != null && resultArray.size() > 0) {
		for (int i=0;i<resultArray.size();i++) {
			String skuId = JsonUtil.getJsonValue(resultArray.get(i).toString(), "skuId");
			String erpPid = JsonUtil.getJsonValue(resultArray.get(i).toString(), "erpPid");
			int count = DbUp.upTable("pc_jingdong_choosed_products").count("select * from pc_jingdong_choosed_products where jd_sku_id='"+skuId+"' and jd_erppid='"+erpPid+"'");
			if(count==0) {
				resultArray.remove(i);
			}
		  }
		}
		return resultArray.toJSONString();
	}

/******************************************************************/
//同步京东接口全部商品清单到临时表，以备惠家有选品使用
	public static void sysnJDAllProductsInfo() {

		// 1.查询商品池接口
		String proPoolObj = RsyncJingdongSupport.callGateway("biz.product.PageNum.query", null);
		JSONObject resJson = JsonUtil.getJsonValues(proPoolObj);
		JSONObject jsonValues = JsonUtil.getJsonValues(resJson.get("biz_product_PageNum_query_response").toString());
		// 2.商品池接收参数 格式：page_num
		List<String> resultPoolList = new ArrayList<String>();
		if (successCode.equals(jsonValues.get("resultCode"))) {
			JSONArray resultArray = JSON.parseArray(jsonValues.get("result").toString());
			if (resultArray != null && resultArray.size() > 0) {
				for (Object object : resultArray) {
					resultPoolList.add(JsonUtil.getJsonValue(object.toString(), "page_num"));
				}

				// 3.查询池内商品编号
				Set<String> skuCodeSet = new HashSet<String>();
				List<String> skuCodeSet2 = new ArrayList<String>();
				if (resultPoolList.size() > 0) {
					for (String st : resultPoolList) {
						Map<String, Object> subParamMap = new HashMap<String, Object>();
						subParamMap.put("pageNum", st);
						String resuPro = RsyncJingdongSupport.callGateway("biz.product.sku.query", subParamMap);
						net.sf.json.JSONObject subResult = RsyncJDService.getJSONStrVal(resuPro,
								"biz_product_sku_query_response");
						if (successCode.equals(subResult.get("resultCode"))
								&& !StringUtils.isBlank(subResult.get("result").toString())) {
							skuCodeSet.addAll(Arrays.asList(subResult.get("result").toString().split(",")));
							skuCodeSet2.addAll(Arrays.asList(subResult.get("result").toString().split(",")));
						}
					}
				}
				
				//排除已经同步过的商品
				List<Map<String,Object>> dataSqlList = DbUp.upTable("pc_jingdong_allproducts_tem").dataSqlList("select jd_sku_id from pc_jingdong_allproducts_tem where rate='0'", null);
				List<String> list = new ArrayList<String>();
				
				for (Map<String, Object> map : dataSqlList) {
					list.add(map.get("jd_sku_id").toString());
					
				}
                System.out.println("库中京东商品数量"+list.size());
                System.out.println("最新接口同步中商品数量"+skuCodeSet.size());
				skuCodeSet.retainAll(list);
				//skuCodeSet2.removeAll(skuCodeSet);
				
				
				System.out.println("最新商品数量"+skuCodeSet2.size());
				// 4.进行分类统一
				List<String> transferList = new ArrayList<String>(list);
				if (transferList.size() > 0) {
					Map<String, String> cateMap = new HashMap<>();
					List<String> setParam = new ArrayList<>();
					List<String> validateList = new ArrayList<>();
					Set<String> skuIdSet = new HashSet<String>();
					 Map<String,String> scatMap = new HashMap<String,String>();
					int num = 0;
					for (int i = 0; i < transferList.size(); i++) {
						if (skuIdSet.contains(transferList.get(i))) {
							continue;
						}
						setParam.add(transferList.get(i));
						Map<String, Object> subParamMap = new HashMap<String, Object>();
						subParamMap.put("set", setParam);
						String returnRelsult = RsyncJingdongSupport.callGateway("jd.kpl.open.shopinfo.sku",
								subParamMap);
						if (returnRelsult.contains("errorResponse")) {
							continue;
						}
						net.sf.json.JSONObject jObRespon = RsyncJDService.getJSONStrVal(returnRelsult,
								"jd_kpl_open_shopinfo_sku_response");
						try {
							if (jObRespon != null && "0".equals(jObRespon.get("code"))) {
								net.sf.json.JSONObject jsonObject = jObRespon.getJSONObject("result");
								@SuppressWarnings("unchecked")
								Set<String> keySet = jsonObject.keySet();
								if (keySet != null && keySet.size() > 0) {
									for (String ks : keySet) {
										if (validateList.size() == 0 || !validateList.contains(ks)) {
											validateList.add(ks);
											String objectStr = jsonObject.get(ks).toString();
											JSONArray parseArray = JSON.parseArray(objectStr);
											net.sf.json.JSONObject subResult = net.sf.json.JSONObject.fromObject(parseArray.get(0));
											cateMap.put(subResult.getString("erpPid"), jsonObject.getString(ks));
											skuIdSet.addAll(getSameProSkuIds(jsonObject, ks));
											num++;
										}
										if (cateMap.keySet().size() == queryNum || i == (transferList.size() - 1)) {
											try {		
												if (cateMap != null) {
													List<List<JSONObject>> ll = sortSku(cateMap, new HashSet<>(list));
													//skuIdSet.retainAll(skuCodeSet2);
													List<String> relSkuList = new ArrayList<String>(skuIdSet);
													Map<String, JSONObject> priceMap = batchQuerySkuPrice(relSkuList);
												LOOP:for (List<JSONObject> slist : ll) {
														System.out.println("come on .....");
													for (JSONObject subObj : slist) {
													String skuId = subObj.get("skuId").toString();
													String erpPid = subObj.get("erpPid").toString();
													String pname = subObj.get("pname").toString();
													String sKuname = subObj.get("name").toString();
													String price = "0";
													String jdPrice = "0";
													String rate = "0";
													String createTime = FormatHelper.upDateTime();
													Map<String, Object> paramMap = new HashMap<String, Object>();
													paramMap.put("sku", skuId);
													paramMap.put("isShow", "true");
													paramMap.put("queryExts","taxInfo");
													Object catId;
													String catName = "";
													JSONObject detailResult = JsonUtil.getJsonValues(RsyncJingdongSupport.callGateway("biz.product.detail.query", paramMap));
													if(detailResult.toString().contains("biz_product_detail_query_response")) {
														JSONObject responResult = detailResult.getJSONObject("biz_product_detail_query_response");
														catId = responResult.getJSONObject("result").get("category");
														rate = responResult.getJSONObject("result").get("taxInfo").toString();
														String[] split = catId.toString().split(";");
														
										/*				for (String string : split) {
															if(scatMap.keySet().contains((string))) {
																catName =catName+scatMap.get(string).toString()+"==";
															}
															else {
																Map<String, Object> subParamMap2 = new HashMap<String, Object>();
																subParamMap2.put("cid", string);
																try {
																	JSONObject ret = JsonUtil.getJsonValues(RsyncJingdongSupport.callGateway("jd.biz.product.getcategory", subParamMap2));
																	if(ret.toString().contains("jd_biz_product_getcategory_response")) {
																		JSONObject re = ret.getJSONObject("jd_biz_product_getcategory_response");
																		scatMap.put(string, re.getJSONObject("result").get("name").toString());
																		 catName =catName+scatMap.get(string)+"==";
																	}
																}catch (Exception e) {
																	// TODO: handle exception
																	e.printStackTrace();
																	System.out.println(RsyncJingdongSupport.callGateway("jd.biz.product.getcategory", subParamMap2));
																}
															}

														}*/
														
													}
													if (priceMap.keySet().contains(skuId)) {
														price = priceMap.get(skuId).get("price").toString();
														jdPrice = priceMap.get(skuId).get("jdPrice").toString();
														
													}
													
													DbUp.upTable("pc_jingdong_allproducts_tem").dataUpdate(new MDataMap("jd_eripid",erpPid,"rate",rate), "rate", "jd_eripid");
													continue LOOP;
													/*DbUp.upTable("pc_jingdong_allproducts_tem").dataInsert(new MDataMap
															("jd_sku_id",skuId,"catName",catName,"uid",WebHelper.upUuid(),"jd_sku_name",sKuname,"jd_sku_price",price,"jd_sku_jdprice",jdPrice,"jd_eripid",erpPid,"jd_pname",pname,"rate",rate,"create_time",createTime));*/
													}
													}
													
												}

											} finally {
												// 7.清除,重新循环
												cateMap.clear();
												skuIdSet.clear();
											}
										}
									}
								}

							}
						} finally {
							setParam.clear();
						}
					}
					validateList.clear();
				}
				transferList.clear();
			}
		}

	}
	
	
	public static void sysnCatName() {
			
		List<Map<String, Object>> dataSqlList = DbUp.upTable("pc_jingdong_allproducts_tem").dataSqlList("select jd_sku_id from pc_jingdong_allproducts_tem where catName='' ", null);
	    int num =0;
	    Map<String,String> catMap = new HashMap<String,String>();
		for (Map<String, Object> map : dataSqlList) {
		String skuId = map.get("jd_sku_id").toString();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("sku", skuId);
		paramMap.put("isShow", "true");
		Object catId;
		String catName = "";
		JSONObject detailResult = JsonUtil.getJsonValues(RsyncJingdongSupport.callGateway("biz.product.detail.query", paramMap));
		if(detailResult.toString().contains("biz_product_detail_query_response")) {
			JSONObject responResult = detailResult.getJSONObject("biz_product_detail_query_response");
			if(responResult.toString().contains("result")) {
				catId = responResult.getJSONObject("result").get("category");
				String[] split = catId.toString().split(";");
				for (String string : split) {
					if(catMap.keySet().contains((string))) {
						catName =catName+catMap.get(string).toString()+"==";
					}
					else {
						Map<String, Object> subParamMap2 = new HashMap<String, Object>();
						subParamMap2.put("cid", string);
						try {
							JSONObject ret = JsonUtil.getJsonValues(RsyncJingdongSupport.callGateway("jd.biz.product.getcategory", subParamMap2));
							if(ret.toString().contains("jd_biz_product_getcategory_response")) {
								JSONObject re = ret.getJSONObject("jd_biz_product_getcategory_response");
								catMap.put(string, re.getJSONObject("result").get("name").toString());
								 catName =catName+catMap.get(string)+"==";
							}
						}catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
							System.out.println(RsyncJingdongSupport.callGateway("jd.biz.product.getcategory", subParamMap2));
						}
					}

				}
			}
			
		}
		System.out.println((++num)+"come on .....");
		
			DbUp.upTable("pc_jingdong_allproducts_tem").dataUpdate(new MDataMap("jd_sku_id",skuId,"catName",catName.substring(0,catName.length()-2)), "catName", "jd_sku_id");
		
		
	}
	}
	
	/**
	 * 修正毛利低于5%的售价
	 * @param costPrice
	 * @param skuPrice
	 * @return
	 */
	private static BigDecimal getSellPrice(BigDecimal costPrice, BigDecimal skuPrice) {
		// 毛利
		BigDecimal profit = skuPrice.subtract(costPrice);
		// 毛利率
		BigDecimal profitRate = BigDecimal.ZERO;
		
		// 毛利计算公式：（京东售价-京东供货价）/京东售价  
		if(skuPrice.compareTo(BigDecimal.ZERO) > 0) {
			profitRate = profit.divide(skuPrice, 3, BigDecimal.ROUND_HALF_UP);
		}
		
		// 毛利率低于指定阀值则修正销售价
		if(profit.compareTo(BigDecimal.ZERO) <= 0 || profitRate.compareTo(minProfitRate) < 0) {
			skuPrice = costPrice.divide(new BigDecimal(1).subtract(minProfitRate), 0, BigDecimal.ROUND_UP);
		}
		
		return skuPrice;
	}

	public static Set<String> getSameProSkuIds(net.sf.json.JSONObject jsonObject, String ks) {
		// TODO Auto-generated method stub
		Set<String> resultSet = new HashSet<String>();
		String jsonStr = jsonObject.get(ks).toString();
		JSONArray parseArray = JSON.parseArray(jsonStr);
		for (Object object : parseArray) {
			net.sf.json.JSONObject jsonObj = net.sf.json.JSONObject.fromObject(object.toString());
			resultSet.add(jsonObj.get("skuId").toString());
		}
		return resultSet;
	}
	
	/**
	 * 检查sku是否在京东商品池
	 * @param skuId
	 * @return
	 */
	public static boolean checkSkuAvailable(String skuId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("sku", skuId);
		String returnStr = RsyncJingdongSupport.callGateway("biz.price.sellPrice.get", paramMap);
		if(returnStr != null && returnStr.contains(skuId+"不在您的商品池中")) {
			return false;
		}
		return true;
	}

	/**
	 * 查询sku的活动最低价，包含未开始的活动
	 * @param skuCode
	 * @return BigDecimal.ZERO 未查询到活动时返回
	 */
	public static BigDecimal getSkuEventMinPrice(String skuCode) {
		String sSql = "SELECT MIN(p.favorable_price) price FROM systemcenter.sc_event_item_product p,systemcenter.sc_event_info e,productcenter.pc_skuinfo s";
		sSql += " WHERE p.event_code = e.event_code AND e.event_status in ('4497472700020001','4497472700020002','4497472700020004') ";
		sSql += " AND e.end_time > NOW() AND p.product_code = s.product_code AND p.flag_enable = 1 ";
		sSql += " AND s.sku_code = :sku_code ";
		
		Map<String, Object> map = DbUp.upTable("sc_event_item_product").dataSqlOne(sSql, new MDataMap("sku_code", skuCode));
		return map == null || map.get("price") == null ? null : new BigDecimal(map.get("price").toString());
	}

	//同步修改sku商品名称
	public static MWebResult sysnSkuProductName(String skuId) {
		// TODO Auto-generated method stub
		MWebResult result = new MWebResult();
		//以最小sku名称为准,即pc_skuinfo表中的该商品的第一个sku
        List<Map<String, Object>> list = DbUp.upTable("pc_skuinfo").dataSqlList("select a.*,b.product_shortname product_shortname from pc_skuinfo a,pc_productinfo b  where a.product_code=b.product_code  and b.small_seller_code='SF031JDSC' and  b.product_status!='4497153900060004' and  a.product_code in (select product_code from pc_skuinfo where sell_productcode='"+skuId+"')", null);
		if(list!=null&&list.size()>0) {
        	Map<String, Object> map = list.get(0);
        	if(skuId.equals(map.get("sell_productcode"))) {
        		String hjyProducode = map.get("product_code").toString();
        		String hjyProductShortName = map.get("product_shortname").toString();
            	List<String> setParam = new ArrayList<>();
    			setParam.add(skuId);
    			Map<String, Object> subParamMap = new HashMap<String, Object>();
    			subParamMap.put("set", setParam);
    			String returnRelsult = RsyncJingdongSupport.callGateway("jd.kpl.open.shopinfo.sku",
    					subParamMap);
    			if(returnRelsult.contains("jd_kpl_open_shopinfo_sku_response")) {
    				net.sf.json.JSONObject jObRespon = RsyncJDService.getJSONStrVal(returnRelsult,
    						"jd_kpl_open_shopinfo_sku_response");
    				if(jObRespon != null) {
    					net.sf.json.JSONObject jsonObject = jObRespon.getJSONObject("result");
    					net.sf.json.JSONArray resList = jsonObject.optJSONArray(skuId);
    					net.sf.json.JSONObject skuObj = null;
    					for(int i = 0,j = resList.size(); i < j; i++) {
    						skuObj = resList.getJSONObject(i);
    						if(skuId.equals(skuObj.optString("skuId"))) {
    							//经过线上数据对比pname和name的属性值一样
    							String proName = skuObj.optString("name");
    							if(!StringUtils.equals(proName, hjyProductShortName)) {
    								int dataUpdate = DbUp.upTable("pc_productinfo").dataUpdate(new MDataMap("product_code",hjyProducode,"product_name",proName,"product_shortname",proName), "product_name,product_shortname", "product_code");
    								if(dataUpdate>0) {
    									PlusHelperNotice.onChangeProductInfo(hjyProducode);
    									result.setResultCode(1);
    								}else {
    									result.setResultCode(0);
    								}
    							}	
    						}
    					}
    				}
    			}
        	}
        }		
		return result;
	}
	
}
