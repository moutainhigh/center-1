package com.cmall.groupcenter.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmall.groupcenter.util.HttpUtil;
import com.srnpr.xmassystem.support.PlusSupportLD;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class LdAfterSaleInfo extends BaseClass {

	public Map<String,Object> getAsaleOrderInfo(String asleCode){
		PlusSupportLD ld = new PlusSupportLD();
		String isSyncLd = ld.upSyncLdOrder();
		if("N".equals(isSyncLd)){//添加开关
			return null;
		}
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("saleCodeLd",asleCode);
		String url = bConfig("groupcenter.rsync_homehas_url")+"getAfterServiceList";
		String result = HttpUtil.post(url, JSONObject.toJSONString(params), "UTF-8");
		if(StringUtils.isEmpty(result)){
			return null;
		}
		try{
			JSONObject jo = JSONObject.parseObject(result);
			Map<String,Object> map = new HashMap<String,Object>();
			String codeStr = jo.getString("code");
			if(StringUtils.isEmpty(codeStr)){
				return null;
			}
			if(jo.getInteger("code")!= 0){
				return null;
			}
			String jsonArrayStr = jo.getString("result");
			JSONArray ja = JSONArray.parseArray(jsonArrayStr);
			Iterator it = ja.iterator();
			while(it.hasNext()){
				JSONObject oo = (JSONObject) it.next();
				map.put("order_code",oo.getInteger("ORD_ID"));//LD 订单编号
				map.put("product_code", oo.getInteger("GOOD_ID"));
				map.put("order_seq", oo.getInteger("ORD_SEQ"));
				map.put("after_sale_code_ld", oo.getString("AFTER_SALE_CODE_LD"));
				map.put("return_code", oo.getString("RTN_ID"));//退货号
				map.put("chg_ord", oo.getString("NEW_ORD_ID"));//换货新单号
				String as_type = StringUtils.isEmpty(oo.getString("AFTER_SALE_TYPE")) ? "T" : oo.getString("AFTER_SALE_TYPE");//售后工单类型 T：退货 H:换货
				if("T".equals(as_type)) {
					map.put("after_sale_type", "1");
					map.put("reason", "".equals(oo.getString("RTN_REASON"))?"LD退货":oo.getString("RTN_REASON"));
				} else {
					map.put("after_sale_type", "2");
					map.put("reason", "".equals(oo.getString("RTN_REASON"))?"LD换货":oo.getString("RTN_REASON"));
				}								
				map.put("good_cnt", 1);
				//获取颜色款式，对应到惠家有的SKU_CODE
				Map<String,Object> colorParams = new HashMap<String,Object>();
				colorParams.put("ordId", oo.getInteger("ORD_ID"));
				colorParams.put("ordSeq", oo.getInteger("ORD_SEQ"));
				String urlColor = bConfig("groupcenter.rsync_homehas_url")+ "getOrderDetailById";
				String orderStr = HttpUtil.post(urlColor, JSONObject.toJSONString(colorParams), "UTF-8");
				if(StringUtils.isEmpty(orderStr)){
					return null;
				}
				JSONObject colorJo = JSONObject.parseObject(orderStr);
				Integer code = colorJo.getInteger("code");
				if(code != 0) {
					return null;
				}
				String orderListStr = jo.getString("result");
				if(StringUtils.isEmpty(orderListStr)){
					return null;
				}
				JSONArray jaColor = JSONArray.parseArray(orderListStr);
				Iterator itColor = jaColor.iterator();
				while(itColor.hasNext()){
					JSONObject ooo = (JSONObject)itColor.next();
					Integer styleId = ooo.getInteger("STYLE_ID")!=null?ooo.getInteger("STYLE_ID"):0;
					Integer colorId = ooo.getInteger("COLOR_ID")!=null?ooo.getInteger("COLOR_ID"):0;
					String sku_key = "color_id="+colorId+"&style_id="+styleId;
					MDataMap skuInfo = DbUp.upTable("pc_skuinfo").one("product_code",oo.getString("GOOD_ID"),"sku_key",sku_key);
					String sku_code = "";
					if(skuInfo != null && !skuInfo.isEmpty()) {
						sku_code = skuInfo.get("sku_code");
						map.put("sku_code", sku_code);
					}else {
						return null;
					}
				}
				return map;//只去第一条，理论上只有一条
			}
		}catch(Exception e){
			e.getStackTrace();
		}
		return null;
	}
	
	
}
