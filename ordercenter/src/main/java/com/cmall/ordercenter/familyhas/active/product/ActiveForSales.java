package com.cmall.ordercenter.familyhas.active.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.cmall.ordercenter.familyhas.active.ActiveReq;
import com.cmall.ordercenter.familyhas.active.ActiveReturn;
import com.cmall.ordercenter.familyhas.active.BaseActive;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.systemcenter.util.StringUtility;
import com.srnpr.xmassystem.helper.PlusHelperEvent;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuInfo;
import com.srnpr.xmassystem.support.PlusSupportProduct;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 *促销活动处理，优先级低于闪购
 * 
 */
public class ActiveForSales extends BaseActive{

	@Override
	protected Map<String, ActiveReturn> activeExc(List<ActiveReq> activeRequests, RootResultWeb activeResult) {
		
		Map<String, ActiveReturn> returnMap=new HashMap<String, ActiveReturn>(activeRequests.size());
		
		List<String> sku_codes=new ArrayList<String>(activeRequests.size());
		for (ActiveReq activeReq : activeRequests) {
			sku_codes.add(activeReq.getSku_code());
		}
			
		Map<String, ProductSkuInfo> skuMap = getSkuInfo(sku_codes);
		//返回价格信息
		for (ActiveReq activeReq : activeRequests) {
			ActiveReturn activeReturn= new ActiveReturn();
			String skuCode=activeReq.getSku_code();
			Integer isPurchase = activeReq.getIsPurchase();
//			if(PlusHelperEvent.checkEventItem(activeReq.getSku_code())){
				PlusModelSkuInfo info = new PlusSupportProduct().upSkuInfoBySkuCode(activeReq.getSku_code(),activeReq.getBuyer_code(),"",isPurchase);
				skuCode=info.getSkuCode();
				activeReturn.setEventType(info.getEventType());
				activeReturn.setActivity_price(info.getSellPrice());
				activeReturn.setProduct_code(info.getProductCode());
				activeReturn.setSku_code(info.getSkuCode());
				if(info.getBuyStatus()==1&&StringUtility.isNotNull(info.getEventCode())){
					activeReturn.setActivity_type("AT140820108888");
					activeReturn.setActivity_code(info.getEventCode()+"&"+info.getItemCode());
					activeReturn.setUse_activity(true);
					if(activeReq.getSku_num()>info.getMaxBuy()&&info.getMaxBuy()>0){
						activeResult.setResultCode(916401132);
						activeResult.setResultMessage(bInfo(916401132, "“" + DbUp.upTable("pc_skuinfo").one("sku_code",info.getSkuCode()).get("sku_name")
								+ "”",info.getMaxBuy()));
					}else if (activeReq.getSku_num()>info.getMaxBuy()&&info.getMaxBuy()==0) {
						activeResult.setResultCode(916401131);
						activeResult.setResultMessage(bInfo(916401131, "“" + DbUp.upTable("pc_skuinfo").one("sku_code",info.getSkuCode()).get("sku_name")
								+ "”"));
					}
				}else if(StringUtility.isNotNull(info.getEventCode())) {
					activeReturn.setActivity_code(info.getEventCode()+"&"+info.getItemCode());
					activeReturn.setUse_activity(true);
					if(activeReq.getSku_num()>info.getMaxBuy()&&info.getMaxBuy()>0){
						activeResult.setResultCode(916401132);
						activeResult.setResultMessage(bInfo(916401132, "“" + DbUp.upTable("pc_skuinfo").one("sku_code",info.getSkuCode()).get("sku_name")
								+ "”",info.getMaxBuy()));
					}else if (activeReq.getSku_num()>info.getMaxBuy()&&info.getMaxBuy()==0) {
						activeResult.setResultCode(916401131);
						activeResult.setResultMessage(bInfo(916401131, "“" + DbUp.upTable("pc_skuinfo").one("sku_code",info.getSkuCode()).get("sku_name")
								+ "”"));
					}
				}else {
					activeReturn.setUse_activity(false);
//					activeResult.setResultCode(916401131);
//					activeResult.setResultMessage(bInfo(916401131, DbUp.upTable("pc_skuinfo").one("sku_code",info.getSkuCode()).get("sku_name")));
				}
//			}else {
//				activeReturn.setActivity_price(skuMap.get(activeReq.getSku_code()).getSellPrice());
//				activeReturn.setUse_activity(false);
//			}
			returnMap.put(skuCode+"_"+activeReq.getBuyer_code(), activeReturn);
			// 兼容秒杀商品时的skuCode是IC编号
			returnMap.put(activeReq.getSku_code()+"_"+activeReq.getBuyer_code(), activeReturn);
		}
		return returnMap;
	}
}