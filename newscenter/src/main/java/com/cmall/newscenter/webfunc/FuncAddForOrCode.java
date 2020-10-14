package com.cmall.newscenter.webfunc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cmall.newscenter.util.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 生成二维码
 * @author dyc
 * */
public class FuncAddForOrCode extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult result = new MWebResult();
		MDataMap params = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		String[] skuCodes = params.get("skuCodes").split(",");
		//根据skuCode查出所有sku信息
		List<MDataMap> list = DbUp.upTable("pc_skuinfo").queryIn("","","",new MDataMap(),-1,-1,"sku_code",params.get("skuCodes"));
		
		//商品信息map
		Map<String, MDataMap> skuInfo = new HashMap<String, MDataMap>();
		StringBuffer deleteConditions = new StringBuffer();
		
		for(MDataMap map : list){
			MDataMap pro = DbUp.upTable("pc_productinfo").one("product_code",map.get("product_code"));
			map.put("product_status", pro.get("product_status"));//商品状态
			skuInfo.put(map.get("sku_code"), map);
			deleteConditions.append("'").append(map.get("sku_code")).append("',");
		}
		//若商品已生成二维码，则覆盖之前的条目,所以先删除表中所有新增且已存在的二维码
		DbUp.upTable("nc_qr_code").dataDelete("product_sku in ("+deleteConditions.toString().substring(0,deleteConditions.length()-1)+")", new MDataMap(), "");
		//查出此APP下二维码的链接地址
		MDataMap addressQuery = new MDataMap();
		addressQuery.put("link_appcode",bConfig("newscenter.app_code"));
		addressQuery.put("project_type","4497465000090001");
		List<MDataMap> address = DbUp.upTable("nc_Link_address").queryAll("", "-link_time", "", addressQuery);
		if(address.size()>0){
			String linkAddress = address.get(0).get("link_address");
			if(result.upFlagTrue()){
				for(String skuCode : skuCodes){
					MDataMap insertMap = new MDataMap();
					String productCode = skuInfo.get(skuCode).get("product_code"); 
					String qrCode = linkAddress+"?goods_num="+productCode+"&sku_num="+skuCode+"&app=liujialing&type=product";
					insertMap.inAllValues("app_code",bConfig("newscenter.app_code"),"product_code",productCode,"product_sku",skuCode,"product_name",skuInfo.get(skuCode).get("sku_name"),"sell_price",skuInfo.get(skuCode).get("sell_price"),"market_price",skuInfo.get(skuCode).get("market_price"),"stock_num",skuInfo.get(skuCode).get("stock_num"),"product_status",skuInfo.get(skuCode).get("product_status"),"qr_code",qrCode,"note","","create_time",DateUtil.getNowTime());
					DbUp.upTable("nc_qr_code").dataInsert(insertMap);
				}
			}
		}else{
			result.inErrorMessage(934205112);
		}		
		return result;
	}

}
