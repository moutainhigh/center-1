package com.cmall.ordercenter.familyhas.active.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cmall.ordercenter.familyhas.active.ActiveReq;
import com.cmall.ordercenter.familyhas.active.ActiveReturn;
import com.cmall.ordercenter.familyhas.active.BaseActive;
import com.cmall.systemcenter.util.StringUtility;
import com.srnpr.xmassystem.helper.PlusHelperEvent;
import com.srnpr.xmassystem.load.LoadProductInfo;
import com.srnpr.xmassystem.modelevent.PlusModelProObject;
import com.srnpr.xmassystem.modelevent.PlusModelSaleProObject;
import com.srnpr.xmassystem.modelproduct.PlusModelProductInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelProductQuery;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuInfo;
import com.srnpr.xmassystem.service.PlusServiceSale;
import com.srnpr.xmassystem.support.PlusSupportProduct;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 *促销活动处理(非IC开头的sku编号)，优先级 高于内购 低于闪购
 * 
 */
public class ActiveForSalesNotIc extends BaseActive{

	@Override
	protected Map<String, ActiveReturn> activeExc(List<ActiveReq> activeRequests, RootResultWeb activeResult) {
		
		Map<String, ActiveReturn> returnMap=new HashMap<String, ActiveReturn>(activeRequests.size());
		
		
		Map<String,BigDecimal> skuCodeMap = new HashMap<String, BigDecimal>();
		for (ActiveReq activeReq : activeRequests) {
			if(!PlusHelperEvent.checkEventItem(activeReq.getSku_code())){
				skuCodeMap.put(activeReq.getSku_code(), BigDecimal.ZERO);
			}
		}
		if (skuCodeMap.isEmpty()) {
			return returnMap;
		}
		String sSql = "select sku_code,sell_price from pc_skuinfo where sku_code in ('"+StringUtils.join(skuCodeMap.keySet(),"','")+"') ";
		List<Map<String,Object>> skuInfoMapList = DbUp.upTable("pc_skuinfo").dataSqlList(sSql, null);
		for (Map<String,Object> map : skuInfoMapList) {
			skuCodeMap.put(map.get("sku_code").toString(), new BigDecimal(map.get("sell_price").toString())) ;
		}
		//返回价格信息
		PlusSupportProduct supportProduct = new PlusSupportProduct();
		LoadProductInfo loadProductInfo =  new LoadProductInfo();
		PlusServiceSale plusServiceSale = new PlusServiceSale();
		List<PlusModelProObject> saleObject = new ArrayList<PlusModelProObject>();
		String sellerCode = "";
		for (ActiveReq activeReq : activeRequests) {
			ActiveReturn activeReturn= new ActiveReturn();
			String skuCode=activeReq.getSku_code();
			PlusModelSkuInfo info = supportProduct.upSkuInfoBySkuCode(activeReq.getSku_code(),activeReq.getBuyer_code());
			skuCode=info.getSkuCode();
			if(StringUtility.isNotNull(info.getEventCode())){
				activeReturn.setActivity_code(info.getEventCode()+"&"+info.getItemCode());
				activeReturn.setActivity_price(info.getSellPrice());
				activeReturn.setActivity_type("AT140820108888");
				activeReturn.setProduct_code(info.getProductCode());
				activeReturn.setSku_code(info.getSkuCode());
				activeReturn.setUse_activity(true);
				if(activeReq.getSku_num()>info.getMaxBuy()&&info.getMaxBuy()>=0){
					activeResult.setResultCode(916401132);
					activeResult.setResultMessage(bInfo(916401132, "“" + DbUp.upTable("pc_skuinfo").one("sku_code",info.getSkuCode()).get("sku_name")
							+ "”",info.getMaxBuy()));
				}else if(info.getBuyStatus()!=1){
					activeResult.setResultCode(916421261);
					activeResult.setResultMessage(bInfo(916421261, "“" + DbUp.upTable("pc_skuinfo").one("sku_code",info.getSkuCode()).get("sku_name")
							+ "”"));
				}
			}else {
				activeReturn.setActivity_price(skuCodeMap.get(activeReq.getSku_code()));
				activeReturn.setUse_activity(false);
				
				PlusModelProductInfo plusModelProductinfo = loadProductInfo.upInfoByCode(new PlusModelProductQuery(activeReq.getProduct_code()));
				sellerCode = plusModelProductinfo.getSellerCode();
				PlusModelProObject ppo = new PlusModelProObject();
				ppo.setSkuCode(activeReq.getSku_code());
				ppo.setSkuNum(activeReq.getSku_num());
				ppo.setBrandCode(plusModelProductinfo.getBrandCode());
				ppo.setCategoryCodes(plusModelProductinfo.getCategorys());
				ppo.setOrig_sku_price(info.getSkuPrice());
				ppo.setSku_price(info.getSkuPrice());
				ppo.setProductCode(activeReq.getProduct_code());
				ppo.setChoose_flag("1");
				saleObject.add(ppo);
				
			}
			
			returnMap.put(skuCode+"_"+activeReq.getBuyer_code(), activeReturn);
		}
		
		
		if (StringUtils.isNotBlank(sellerCode)) {
			//满减活动判断
			PlusModelSaleProObject pmo = new PlusModelSaleProObject();
			pmo.setSaleObject(saleObject);
			pmo.setMemberCode(activeRequests.get(0).getBuyer_code());
			pmo = plusServiceSale.getEventSale(pmo, sellerCode, "449747430001");
			for (PlusModelProObject plusModelProObject : saleObject) {
				ActiveReturn activeReturn= new ActiveReturn();
				//参加满减时
				if(plusModelProObject.isEventTrue()&&StringUtils.isNotBlank(plusModelProObject.getEventCode())){
					activeReturn.setActivity_code(plusModelProObject.getEventCode()+"&"+plusModelProObject.getItem_code());
					activeReturn.setActivity_price(plusModelProObject.getSku_price());
					activeReturn.setActivity_type(plusModelProObject.getEventType());
					activeReturn.setEventType(plusModelProObject.getEventType());
					activeReturn.setProduct_code(plusModelProObject.getProductCode());
					activeReturn.setSku_code(plusModelProObject.getSkuCode());
					activeReturn.setUse_activity(true);
					returnMap.put(plusModelProObject.getSkuCode()+"_"+activeRequests.get(0).getBuyer_code(), activeReturn);
				}
			}
		}
		return returnMap;
	}
}
