package com.cmall.groupcenter.jd.job;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.groupcenter.service.RsyncJDService;
import com.cmall.ordercenter.alipay.util.JsonUtil;
import com.srnpr.xmassystem.homehas.RsyncJingdongSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

import net.sf.json.util.JSONUtils;

/**
 * decription：定时同步京东商品信息
 * 
 * @author zhangbo
 *
 */

public class RsyncJDProductsInfo {

	final static String successCode = "0000";// 查询成功返回编码
	final static int skuCodeLen = 8;// sku编码长度
	final static int queryNum = 100;// 每次查询数据最大值

	public RsyncResult doProcess() {

	
		//1.获取京东选品表中的数据
		List<Map<String, Object>> resultList = DbUp.upTable("pc_jingdong_choosed_products").dataSqlList("select * from pc_jingdong_choosed_products where is_enabled=:is_enabled", new MDataMap("is_enabled","1"));
		
		//2.封装选品数据进行同步处理
		if(resultList!=null&&resultList.size()>0) {
			Map<String,String> paramMap = new HashMap<String,String>();
			for (Map<String, Object> map : resultList) {
				if(!paramMap.containsKey(map.get("jd_sku_id").toString())) {
					paramMap.put(map.get("jd_sku_id").toString(), map.get("jd_erppid").toString());
				}
			}
			
			//3.开启同步
			RsyncJDService.startSysn(paramMap);
			
			//4.状态同步
			RsyncJDService.rsynProState(paramMap);
		}	
		
		
		
		
		
		
		
		/**--------------------------------------------
		 * |此方法为获取京东接口全部商品，供惠家有运营进行选品使用                  |
		 * | RsyncJDService.sysnJDAllProductsInfo();  |
		 * |------------------------------------------|
		 */

		return null;
	}

	
	
	private void sysnJdProductsMethod() {
		
		//如果京东来维护合同商品的话，可放开此注释方法	
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
						}
					}
				}
				// 4.进行分类统一
				List<String> transferList = new ArrayList<String>(skuCodeSet);
				if (transferList.size() > 0) {
					Map<String, String> cateMap = new HashMap<>();
					List<String> setParam = new ArrayList<>();
					List<String> validateList = new ArrayList<>();
					Set<String> skuIdSet = new HashSet<String>();
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
											cateMap.put(ks, jsonObject.getString(ks));
											skuIdSet.addAll(this.getSameProSkuIds(jsonObject, ks));
											LogFactory.getLog(getClass()).warn((++num) + ":商品erpPid为:" + ks + "成功");
										}
										if (cateMap.keySet().size() == queryNum || i == (transferList.size() - 1)) {
											try {
												// 5.同步数据
												//RsyncJDService.rsynData(cateMap, skuIdSet, skuCodeSet);

												// 6.同步商品状态
												//RsyncJDService.rsynProState(cateMap, skuIdSet, skuCodeSet);
											} finally {
												// 7.清除,重新循环
												cateMap.clear();
												skuIdSet.clear();
											}
										}
									}
								}

							}
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
							LogFactory.getLog(getClass()).error("请求参数subParamMap：" + subParamMap);
							LogFactory.getLog(getClass()).error("返回结果returnRelsult：" + returnRelsult);
						} finally {
							setParam.clear();
						}
					}
					validateList.clear();
				}
				transferList.clear();
			}
		}
	};
	
	private Set<String> getSameProSkuIds(net.sf.json.JSONObject jsonObject, String ks) {
		// TODO Auto-generated method stub
		Set<String> resultSet = new HashSet<String>();
		String jsonStr = jsonObject.get(ks).toString();
		JSONArray parseArray = JSON.parseArray(jsonStr);
		for (Object object : parseArray) {
			net.sf.json.JSONObject jsonObj = net.sf.json.JSONObject.fromObject(object);
			resultSet.add(jsonObj.get("skuId").toString());
		}
		return resultSet;
	}

}
