package com.cmall.groupcenter.duohuozhu.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.srnpr.xmassystem.duohuozhu.model.RequestModel;
import com.srnpr.xmassystem.duohuozhu.model.ResponseModel;
import com.srnpr.xmassystem.duohuozhu.support.RsyncDuohuozhuSupport;
import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 多货主商品相关服务类
 */
public class ProductForDuohuozhuSupport {
	
	@SuppressWarnings("unchecked")
	public RootResult rsyncNewProduct(String productCode) {
		RootResult result = new RootResult();
		
		MDataMap productMap = DbUp.upTable("pc_productinfo").one("product_code", productCode);
		List<MDataMap> skuList = DbUp.upTable("pc_skuinfo").queryAll("", "", "", new MDataMap("product_code", productCode));
		
		List<LinkedHashMap<String,Object>> objList = new ArrayList<LinkedHashMap<String,Object>>();
		LinkedHashMap<String,Object> obj,proObj;
		String[] skuColorStyle;
		
		List<LinkedHashMap<String,Object>> skuObjList = new ArrayList<LinkedHashMap<String,Object>>();
		for(MDataMap sku : skuList) {
			obj = new LinkedHashMap<String, Object>();
			skuColorStyle = getColorAndStyle(sku.get("sku_code"));
			obj.put("color_id", skuColorStyle[0]);
			obj.put("color_nm", skuColorStyle[2]);
			obj.put("style_id", skuColorStyle[1]);
			obj.put("style_nm", skuColorStyle[3]);
			obj.put("good_prc", sku.get("sell_price"));
			skuObjList.add(obj);
		}
		
		if(skuObjList.isEmpty()) {
			result.setResultCode(0);
			result.setResultMessage("无SKU数据");
			return result;
		}
		
		proObj = new LinkedHashMap<String, Object>();
		proObj.put("cp_good_id", productCode);
		proObj.put("good_nm", productMap.get("product_name"));
		proObj.put("detail", skuObjList);
		objList.add(proObj);
		
		LinkedHashMap<String,Object> bodyMap = new LinkedHashMap<String, Object>();
		bodyMap.put("good", objList);
		
		RequestModel reqModel = new RequestModel();
		reqModel.getHead().setFunction_id("CP000001");
		reqModel.setBody(bodyMap);
		
		ResponseModel respModel = new RsyncDuohuozhuSupport().callGateway(reqModel);
		
		if(respModel == null) {
			result.setResultCode(0);
			result.setResultMessage("接口调用异常");
			return result;
		}
		
		if(!"00".equals(respModel.getHeader().getResp_code())) {
			result.setResultCode(0);
			result.setResultMessage(respModel.getHeader().getResp_msg());
			return result;
		}
		
		List<Map<String, Object>> goodList = (List<Map<String, Object>>)respModel.getBody().get("good");
		if(goodList != null && !goodList.isEmpty()) {
			for(Map<String, Object> map : goodList) {
				if(!"00".equals(map.get("err_code"))) {
					result.setResultCode(0);
					result.setResultMessage((String)map.get("err_msg"));
				}
			}
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public RootResult rsyncProductStock(String productCode) {
		RootResult result = new RootResult();
		List<MDataMap> skuList = DbUp.upTable("pc_skuinfo").queryAll("sku_code", "", "", new MDataMap("product_code", productCode));
		
		List<LinkedHashMap<String,Object>> objList = new ArrayList<LinkedHashMap<String,Object>>();
		LinkedHashMap<String,Object> obj;
		String[] skuColorStyle;
		Map<String,String> skuKeyMap = new HashMap<String, String>();
		for(MDataMap sku : skuList) {
			obj = new LinkedHashMap<String, Object>();
			skuColorStyle = getColorAndStyle(sku.get("sku_code"));
			
			obj.put("cp_good_id", productCode);
			obj.put("color_id", skuColorStyle[0]);
			obj.put("style_id", skuColorStyle[1]);
			obj.put("color_nm", skuColorStyle[2]);
			obj.put("style_nm", skuColorStyle[3]);
			obj.put("site_no", "");
			objList.add(obj);
			
			skuKeyMap.put(productCode+"-"+skuColorStyle[0]+"-"+skuColorStyle[1], sku.get("sku_code"));
			skuKeyMap.put(productCode+"-"+skuColorStyle[2]+"-"+skuColorStyle[3], sku.get("sku_code"));
		}
		
		if(objList.isEmpty()) {
			return result;
		}
		
		LinkedHashMap<String,Object> bodyMap = new LinkedHashMap<String, Object>();
		bodyMap.put("stock", objList);
		
		RequestModel reqModel = new RequestModel();
		reqModel.getHead().setFunction_id("CP000010");
		reqModel.setBody(bodyMap);
		
		ResponseModel respModel = new RsyncDuohuozhuSupport().callGateway(reqModel);
		
		if(respModel == null) {
			result.setResultCode(0);
			result.setResultMessage("接口调用异常");
			return result;
		}
		
		if(!"00".equals(respModel.getHeader().getResp_code())) {
			result.setResultCode(0);
			result.setResultMessage(respModel.getHeader().getResp_msg());
			return result;
		}
		
		List<Map<String, Object>> stockList = (List<Map<String, Object>>)respModel.getBody().get("stock");
		if(stockList == null || stockList.isEmpty()) {
			return result;
		}
		
		// 更新库存
		String skuCode = "";
		MDataMap storeMap;
		String stockNum;
		for(Map<String, Object> map : stockList) {
			if(!"00".equals(map.get("err_code"))) {
				continue;
			}
			
			skuCode = skuKeyMap.get(map.get("cp_good_id")+"-"+map.get("color_nm")+"-"+map.get("style_nm"));
			if(StringUtils.isBlank(skuCode)) {
				skuCode = skuKeyMap.get(map.get("cp_good_id")+"-"+map.get("color_id")+"-"+map.get("style_id"));
			}
			
			stockNum = NumberUtils.toInt(map.get("qty")+"")+"";
			if(StringUtils.isNotBlank(skuCode)) {
				storeMap = DbUp.upTable("sc_store_skunum").oneWhere("zid,stock_num", "", "", "sku_code",skuCode,"store_code","TDS1");
				if(storeMap == null) {
					storeMap = new MDataMap();
					storeMap.put("store_code", "TDS1");
					storeMap.put("sku_code", skuCode);
					storeMap.put("stock_num", stockNum);
					DbUp.upTable("sc_store_skunum").dataInsert(storeMap);
				}else if(!storeMap.get("stock_num").equals(stockNum)){
					storeMap.put("stock_num", stockNum);
					DbUp.upTable("sc_store_skunum").dataUpdate(storeMap, "stock_num", "zid");
					PlusHelperNotice.onChangeSkuStock(skuCode);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 返回SKU的颜色尺码
	 * @param skuCode
	 * @return {colorId,styleId,colrName,styleName}
	 */
	public String[] getColorAndStyle(String skuCode) {
		MDataMap sku = DbUp.upTable("pc_skuinfo").one("sku_code", skuCode);
		// 4497462000010001=44974620000100010002&4497462000020001=44974620000200010001
		String skuKey[] = sku.get("sku_key").split("&");
		// 颜色=海盐蓝&款式=共同
		String skuKeyvalue[] = sku.get("sku_keyvalue").split("&");
		
		return new String[]{
				skuKey[0].split("=")[1],
				skuKey[1].split("=")[1],
				skuKeyvalue[0].split("=")[1],
				skuKeyvalue[1].split("=")[1]
		};
	}
}
