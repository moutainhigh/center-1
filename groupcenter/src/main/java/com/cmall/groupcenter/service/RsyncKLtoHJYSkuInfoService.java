package com.cmall.groupcenter.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmall.groupcenter.homehas.RsyncKaoLaSupport;
import com.cmall.productcenter.service.ProductService;
import com.cmall.systemcenter.service.FlowBussinessService;
import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topdo.TopConfig;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

public class RsyncKLtoHJYSkuInfoService extends BaseClass {
	
	final static String klProDown = "0";//考拉商品下架不可卖
	final static String klProUp = "1";//考拉商品上架可卖
	final static String rsyncProType = "3";//一般贸易商品类型标识
	final static String KL_Sku_Head = "8019";
	public final static String SKU_KEY_COLOR = "4497462000010001"; // 颜色属性
	public final static String SKU_KEY_STYLE = "4497462000020001"; // 款式属性

	public static void rsyncKLtoHJYSkuInfo(String goodsId, String skuId) {
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		
		// 优先根据商品更新数据，避免因为单个sku上下架状态更新的顺序问题造成的商品下架
		if(StringUtils.isNotBlank(goodsId)){
			treeMap.put("goodsIds", "[\""+goodsId+"\"]");
			String proInfoStr = RsyncKaoLaSupport.doPostRequest("querySkuIdsByGoodsIds", "channelId", treeMap);
			JSONObject obj = JSON.parseObject(proInfoStr);
			
			if(obj != null && obj.getIntValue("recCode") == 200){
				JSONArray goodsInfoList = obj.getJSONArray("goodsInfo");
				if(!goodsInfoList.isEmpty()){
					JSONObject goods = goodsInfoList.getJSONObject(0);
					JSONArray ja = goods.getJSONArray("skuIds");
					if(ja.size() > 0){
						// 多个skuid根据逗号分割
						skuId = StringUtils.join(ja.toArray(new String[0]),",");
					}
				}
			}
			
		}
		
		List<JSONObject> skuItemList = new ArrayList<JSONObject>();
			
		if(StringUtils.isNotBlank(skuId)){
			// 根据skuid编号更新数据
			treeMap = new TreeMap<String, String>();
			treeMap.put("skuIds", "[\""+StringUtils.join(skuId.split(","),"\",\"")+"\"]");
			treeMap.put("queryType", "0");
			String proInfoStr = RsyncKaoLaSupport.doPostRequest("queryGoodsInfoByIds", "channelId", treeMap);
			JSONArray klItemList = JSONArray.parseArray(proInfoStr);
			
			if(klItemList != null){
				JSONObject resultItem;
				JSONObject goodsInfo;
				for(int m = 0, n = klItemList.size(); m < n; m++){
					resultItem = klItemList.getJSONObject(m);
					
					if(resultItem.getIntValue("recCode") == 200){
						goodsInfo = resultItem.getJSONObject("goodsInfo");
						//拉取过来考拉已经上架且类型是一般贸易的商品进行同步，对下架商品进行惠家有产品查询，有则进行商品状态修改
						if(!rsyncProType.equalsIgnoreCase(goodsInfo.getString("importType"))){
							continue;
						}
						
						skuItemList.add(goodsInfo);
					}
				}
			}

		}
		

		if (!skuItemList.isEmpty()) {
			rsynSkuInfo(skuItemList);
		}
	}
	
	public static void rsynSkuInfo(List<JSONObject> itemList) {
		// 考拉商品编号
		String klGoodsId = itemList.get(0).getString("goodsId");
		
		// 兼容调编情况下惠家有多个商品编号对应一个考拉编号的情况，排除已经强制下架的商品
		String sqlWhere = "product_status != '4497153900060004' AND product_code_old = :product_code_old AND small_seller_code = :small_seller_code";
		List<MDataMap> productMapList = DbUp.upTable("pc_productinfo").queryAll("*", "", sqlWhere, new MDataMap("product_code_old", klGoodsId,"small_seller_code", TopConfig.Instance.bConfig("familyhas.seller_code_KL")));
		
		for(MDataMap productInfo : productMapList) {
			// 惠家有商品编号
			String productCode = productInfo.get("product_code");
			// 当前惠家有商品状态
			String productStatus = productInfo.get("product_status");
			
			boolean downFlag = false;    // 是否需要下架商品
			boolean needRefresh = false; // 是否需要刷新商品缓存
			boolean allSkuSaleN = false;   // 是否所有的sku都不可售
			MDataMap skuInfo;
			String skuCode; //惠家有SKU编号
			String saleYn;
			String picUrl;
			for(JSONObject item : itemList){
				skuInfo = DbUp.upTable("pc_skuinfo").one("product_code", productCode,"sell_productcode",item.getString("skuId"));
				saleYn = klProUp.equals(item.getString("onlineStatus")) ? "Y" : "N";
				
				// 优先取skuImageUrl图片
				picUrl = StringUtils.trimToEmpty(item.getString("skuImageUrl"));
				if(StringUtils.isBlank(picUrl)){
					picUrl = StringUtils.trimToEmpty(item.getString("imageUrl"));
				}
				
				// 先初始为空字符串后面判断如果未匹配到内容再设置默认值
				String colorName = "";
				String styleName = "";
				JSONArray skuProperty = item.getJSONArray("skuProperty");
				if(skuProperty != null && skuProperty.size() > 0){
					JSONObject obj;
					for(int i = 0,j = skuProperty.size(); i < j; i++){
						obj = skuProperty.getJSONObject(i);
						
						if("颜色".equals(obj.getString("propertyName"))){
							colorName = obj.getString("propertyValue").replaceAll("=", "").replaceAll("&", "");
						} else {
							// 除颜色外其他都归类于“尺码”属性
							if(!styleName.isEmpty()) {
								styleName += "/";
							}
							styleName += obj.getString("propertyValue").replaceAll("=","").replaceAll("&", "").trim();
						}
					}
				}
				// 默认值
				colorName = StringUtils.isBlank(colorName) ? "共同" : colorName;
				styleName = StringUtils.isBlank(styleName) ? "共同" : styleName;
				String colorKey = getPropertyCode(productCode, SKU_KEY_COLOR, "颜色", colorName);
				String styleKey = getPropertyCode(productCode, SKU_KEY_STYLE, "款式", styleName);
				
				// 不存在则保存
				if(skuInfo == null){
					skuCode = WebHelper.upCode(KL_Sku_Head);
					skuInfo = new MDataMap();
					skuInfo.put("sku_code",skuCode);
					skuInfo.put("product_code",productCode);
					skuInfo.put("sell_price",item.getString("suggestPrice"));
					skuInfo.put("market_price", item.getString("marketPrice"));
					skuInfo.put("cost_price", item.getString("price"));
					skuInfo.put("stock_num", item.getString("store"));
					skuInfo.put("sku_picurl", picUrl);
					skuInfo.put("sku_name", item.getString("title"));
					skuInfo.put("sell_productcode",item.getString("skuId"));
					skuInfo.put("seller_code", "SI2003");
					skuInfo.put("sale_yn",saleYn);
					skuInfo.put("flag_enable", "1");
					skuInfo.put("sku_key", "color_id=0&style_id=0");
					skuInfo.put("sku_keyvalue", "颜色=共同&款式=共同");
					
					skuInfo.put("sku_key", "4497462000010001="+colorKey+"&4497462000020001="+styleKey);
					skuInfo.put("sku_keyvalue", "颜色="+colorName+"&款式="+styleName);
					
					DbUp.upTable("pc_skuinfo").dataInsert(skuInfo);
					
					// 查询一下刚插入的数据，供后续使用
					skuInfo = DbUp.upTable("pc_skuinfo").one("sku_code", skuCode, "product_code", productCode);
				}else{
					skuCode = skuInfo.get("sku_code");
				}
				
				boolean updateSkuFlag = false;
				
				MDataMap updateSkuInfo = new MDataMap();
				updateSkuInfo.put("zid", skuInfo.get("zid"));
				updateSkuInfo.put("sku_code", skuInfo.get("sku_code"));
				
				// 销售价调整，以网易考拉为准
				//if(item.getBigDecimal("suggestPrice").compareTo(new BigDecimal(skuInfo.get("sell_price"))) != 0){
				//	updateSkuInfo.put("sell_price",item.getString("suggestPrice"));
				//	updateSkuFlag = true;
				//}
				
				// 市场价调整，以网易考拉为准
				//if(item.getBigDecimal("marketPrice").compareTo(new BigDecimal(skuInfo.get("market_price"))) != 0){
				//	updateSkuInfo.put("market_price",item.getString("marketPrice"));
				//	updateSkuFlag = true;
				//}
				
				// SKU图片调整，以网易考拉为准
				//if(!item.getString("imageUrl").equalsIgnoreCase(skuInfo.get("sku_picurl"))){
				//	updateSkuInfo.put("sku_picurl",item.getString("imageUrl"));
				//	updateSkuFlag = true;
				//}
				
				// 名称调整，以网易考拉为准
				//if(!item.getString("title").equalsIgnoreCase(skuInfo.get("sku_name"))){
				//	updateSkuInfo.put("sku_name",item.getString("title"));
				//	updateSkuFlag = true;
				//}
				
				// 更新一下成本价，并且记录一下商品需要下架
				if(item.getBigDecimal("price").compareTo(new BigDecimal(skuInfo.get("cost_price"))) != 0){
					updateSkuInfo.put("cost_price", item.getString("price"));
					updateSkuFlag = true;
					downFlag = true;
				}
				
				// 可售状态，以网易考拉为准
				if(!saleYn.equalsIgnoreCase(skuInfo.get("sale_yn"))){
					updateSkuFlag = true;
					updateSkuInfo.put("sale_yn", saleYn);
				}
				
				// 商品图片
				if(!picUrl.equalsIgnoreCase(skuInfo.get("sku_picurl"))){
					updateSkuFlag = true;
					updateSkuInfo.put("sku_picurl", picUrl);
				}
				
				//sku_key,sku_keyvalue变化
				if(!("4497462000010001="+colorKey+"&4497462000020001="+styleKey).equalsIgnoreCase(skuInfo.get("sku_key")))
				{
					skuInfo.put("sku_key", "4497462000010001="+colorKey+"&4497462000020001="+styleKey);
					skuInfo.put("sku_keyvalue", "颜色="+colorName+"&款式="+styleName);
					updateSkuFlag = true;
				}
				
				if(updateSkuFlag){
					DbUp.upTable("pc_skuinfo").dataUpdate(updateSkuInfo, "", "zid,sku_code");
					needRefresh = true;
				}

				// 刷新库存
				MDataMap store = DbUp.upTable("sc_store_skunum").one("store_code","TDS1","sku_code",skuCode);
				if(store == null){
					store = new MDataMap();
					store.put("store_code","TDS1" );
					store.put("sku_code",skuCode);
					store.put("stock_num", item.getString("store"));
					DbUp.upTable("sc_store_skunum").delete("sku_code",skuCode);
					DbUp.upTable("sc_store_skunum").dataInsert(store);
					PlusHelperNotice.onChangeSkuStock(skuCode);
				}else if(!store.get("stock_num").equals(item.getString("store"))){
					store.put("stock_num", item.getString("store"));
					DbUp.upTable("sc_store_skunum").dataUpdate(store, "stock_num", "zid,sku_code");
					PlusHelperNotice.onChangeSkuStock(skuCode);
				}
			}
			
			// 是否商品下面有可售的SKU
			if(DbUp.upTable("pc_skuinfo").count("product_code", productCode,"sale_yn", "Y") == 0){
				allSkuSaleN = true;
			}
			
			// 如果商品是在架状态且需要下架则执行下架操作
			if("4497153900060002".equalsIgnoreCase(productStatus)){
				if(downFlag || allSkuSaleN){
					String remark = "";
					String fromStatus= "4497153900060002";
					String toStatus = "4497153900060003";
					String flowType = "449715390006";
					String userCode = "RsyncKLtoHJYSkuInfoService";
					
					if(downFlag){
						remark="SKU成本价变更商品下架";
					}
					if(allSkuSaleN){
						if(StringUtils.isBlank(remark)){
							remark="无可售SKU";
						}else{
							remark=",无可售SKU";
						}
					}
					new FlowBussinessService().ChangeFlow(productInfo.get("uid"), flowType, fromStatus, toStatus, userCode, remark, new MDataMap());
				}
			}
			
			if(needRefresh){
				PlusHelperNotice.onChangeProductInfo(productCode);
			}
			
		}
	}
	
	public static String getPropertyCode(String productCode,String keyCode, String keyName, String name){
		// 校验当前的keycode ，如果是已经存在的
		MDataMap mdProperty = DbUp.upTable("pc_productproperty").oneWhere("property_code", "", "", "product_code", productCode,"property_keycode", keyCode, "property_value", name);
		if(mdProperty != null) return mdProperty.get("property_code");
		
		//ProductService.ColorHead
		
		// 取属性的最大值
		MDataMap maxCodeMap = DbUp.upTable("pc_productproperty").oneWhere("max(property_code) code", "", "", "product_code", productCode,"property_keycode", keyCode);
		String newCode = null;
		if(maxCodeMap == null || StringUtils.isBlank(maxCodeMap.get("code"))){
			// 默认追加初始后缀
			newCode = keyCode + "0001";
		}else{
			// 在最大的基础上加1
			newCode = new BigDecimal(maxCodeMap.get("code")).add(BigDecimal.ONE).toString();
		}
		
		MDataMap insertMap = new MDataMap();
		insertMap.put("product_code", productCode);
		insertMap.put("property_keycode", keyCode);
		insertMap.put("property_code", newCode);
		insertMap.put("property_key", keyName);
		insertMap.put("property_value", name);
		if(SKU_KEY_COLOR.equals(keyCode)){
			insertMap.put("property_type", ProductService.ColorHead);
		}else if(SKU_KEY_COLOR.equals(keyCode)){
			insertMap.put("property_type", ProductService.MainHead);
		}
		insertMap.put("type", "2");
		
		DbUp.upTable("pc_productproperty").dataInsert(insertMap);
		
		return newCode;
	}

}
