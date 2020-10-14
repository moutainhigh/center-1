package com.cmall.ordercenter.familyhas.active;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cmall.productcenter.model.ProductSkuInfo;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 活动基类
 * @author jlin
 *
 */
public abstract class BaseActive extends BaseClass{
	
	/**
	 * 
	 * @param activeRequests
	 * @param activeResult
	 * @return key为 sku_code_buyer_code
	 */
	protected abstract Map<String, ActiveReturn> activeExc(List<ActiveReq> activeRequests,RootResultWeb activeResult);

	/**
	 * 执行活动
	 * @param activeRequests
	 * @param activeResult 错误原因
	 * @return key为 sku_code_buyer_code
	 */
	public Map<String, ActiveReturn> upActive(List<ActiveReq> activeRequests,RootResultWeb activeResult){
		
		return activeExc(activeRequests,activeResult);
		
	} 
	
	/**
	 * 获取商品的普通价格
	 * @param activeRequests
	 * @return
	 */
	public Map<String, BigDecimal> upSkuPrice(List<ActiveReq> activeRequests){
		
		Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
		
		List<String> sku_codes=new ArrayList<String>(activeRequests.size());
		for (ActiveReq activeReq : activeRequests) {
			sku_codes.add(activeReq.getSku_code());
		}
		
		Map<String, ProductSkuInfo> priceMap=getSkuInfo(sku_codes);
		for (ActiveReq activeReq : activeRequests) {
			
//			ActiveReturn activeReturn = new ActiveReturn();
//			activeReturn.setActivity_price(new BigDecimal(priceMap.get(activeReq.getSku_code()).getSellerCode()));
//			activeReturn.setSku_code(activeReq.getSku_code());
//			activeReturn.setUse_activity(false);
			map.put(activeReq.getSku_code(), new BigDecimal(priceMap.get(activeReq.getSku_code()).getSellerCode()));
		}
		
		return map;
	}
	
	/**
	 * 批量获取sku信息
	 * @param sku_codes
	 * @return
	 */
	protected Map<String, ProductSkuInfo> getSkuInfo(List<String> sku_codes) {
		
		Map<String, ProductSkuInfo> map = new HashMap<String, ProductSkuInfo>();
		
		
		/*
		String sql="SELECT " +
				"s.zid,s.uid,s.sku_code_old,s.sku_code,s.product_code,s.sell_price,p.market_price,s.stock_num,s.sku_key,s.sku_keyvalue,s.sku_picurl,s.sku_name,s.sku_adv,s.sell_productcode,s.seller_code,s.security_stock_num,s.product_code_old,s.qrcode_link,s.sell_count,s.sale_yn " +
				" from pc_skuinfo s LEFT JOIN (select product_code,market_price from pc_productinfo) p on s.product_code=p.product_code where sku_code in ("+joinStrSql(sku_codes)+")";
		*/
		
		
		String sql="SELECT " +
				"s.zid,s.uid,s.sku_code_old,s.sku_code,s.product_code,s.sell_price,s.stock_num,s.sku_key,s.sku_keyvalue,s.sku_picurl,s.sku_name,s.sku_adv,s.sell_productcode,s.seller_code,s.security_stock_num,s.product_code_old,s.qrcode_link,s.sell_count,s.sale_yn " +
				",(select  market_price from pc_productinfo where pc_productinfo.product_code=s.product_code) as market_price "+
				" from pc_skuinfo s where sku_code in ("+joinStrSql(sku_codes)+")";
		
		List<Map<String, Object>> list=DbUp.upTable("pc_skuinfo").dataSqlList(sql, null);
		if(list!=null&&list.size()>0){
			
			for (Map<String, Object> map2 : list) {
				
				SerializeSupport<ProductSkuInfo> ss = new SerializeSupport<ProductSkuInfo>();
				ProductSkuInfo pcSkuInfo=new ProductSkuInfo();
				ss.serialize(new MDataMap(map2), pcSkuInfo);
				map.put(pcSkuInfo.getSkuCode(), pcSkuInfo);
			}
		}
		
		return map;
	}
	
	/**
	 * 拼接sql条件
	 * @param sku_codes
	 * @return
	 */
	protected String joinStrSql(List<String> sku_codes){
		StringBuffer sql_where=new StringBuffer();
		for (String str : sku_codes) {
			sql_where.append(",'").append(str).append("'");
		}
		return sql_where.substring(1);
	}
	
	/**
	 * 获取key
	 * @param sku_code
	 * @param buyer_code
	 * @return
	 */
	public String getKey(ActiveReq activeReq){
		return activeReq.getSku_code()+"_"+activeReq.getBuyer_code();
	}
	
}
