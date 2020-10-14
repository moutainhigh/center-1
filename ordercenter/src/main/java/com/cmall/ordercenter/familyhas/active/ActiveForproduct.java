package com.cmall.ordercenter.familyhas.active;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.ordercenter.familyhas.active.product.ActiveForFlash;
import com.cmall.ordercenter.familyhas.active.product.ActiveForSales;
import com.cmall.ordercenter.familyhas.active.product.ActiveForSalesNotIc;
import com.cmall.ordercenter.familyhas.active.product.ActiveForSource;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 商品活动
 * @author jlin
 *
 */
public class ActiveForproduct extends BaseClass {

	/**
	 * 商品类活动管道
	 * @param activeRequests
	 * @param activeResult
	 * @return 
	 */
	public Map<String, ActiveReturn> activeGallery(List<ActiveReq> activeRequests,RootResultWeb activeResult) {
	
		if(activeResult==null){
			activeResult=new RootResultWeb();
		}
		
		//先判断IC的商品
		ActiveForSales activeForSales = new ActiveForSales();
		Map<String, ActiveReturn> activeMap=activeForSales.upActive(activeRequests, activeResult);
		
//		if(!activeResult.upFlagTrue()){
//			return null;
//		}
		
		for(ActiveReq rq : activeRequests){
			// 非扫码购促销活动
			if(rq.getSku_code().startsWith("IC") && !rq.getSku_code().contains("IC_SMG")){
				ActiveReturn ar = activeMap.get(rq.getSku_code()+"_"+rq.getBuyer_code());
				if(ar != null){
					rq.setSku_code(ar.getSku_code()); // 促销编号不能正常参与活动时，转换商品的IC编号为正常SKU编号
				}
			}
		}
		
		//最新规则，IC活动 ，闪购活动，非IC促销活动，内购
		
		ActiveForFlash activeForFlash = new ActiveForFlash();
		Map<String, ActiveReturn> activeForFlashMap= null;
		
		ActiveForSource activeForSource = new ActiveForSource();
		Map<String, ActiveReturn> activeForSourceMap= null;
		
		ActiveForSalesNotIc activeForSalesNotIc = new ActiveForSalesNotIc();
		Map<String, ActiveReturn> activeForSalesNotIcMap= null;
		
		for (Map.Entry<String, ActiveReturn> active : activeMap.entrySet()) {
			
			String key=active.getKey();
			ActiveReturn activeReturn = active.getValue();
			
			if(!activeReturn.isUse_activity()){//没有使用IC，开始判断闪购
				
				//使用闪购活动
				if(activeForFlashMap==null){
					activeForFlashMap = activeForFlash.upActive(activeRequests, activeResult);
					if(!activeResult.upFlagTrue()){
						break;
					}
				}
				
				ActiveReturn returnForFlash=activeForFlashMap.get(key);
				
				if(returnForFlash!=null&&returnForFlash.isUse_activity()){
					activeMap.put(key, returnForFlash);
				}else{
					//使用非IC活动
					
					if(activeForSalesNotIcMap==null){
						activeForSalesNotIcMap = activeForSalesNotIc.upActive(activeRequests, activeResult);
						if(!activeResult.upFlagTrue()){
							break;
						}
					}
					
					ActiveReturn returnForNoIc=activeForSalesNotIcMap.get(key);
					
					if(returnForNoIc!=null&&returnForNoIc.isUse_activity()){
						activeMap.put(key, returnForNoIc);
					}else{
						//使用内购活动
						
						if(activeForSourceMap==null){
							activeForSourceMap = activeForSource.upActive(activeRequests, activeResult);
							if(!activeResult.upFlagTrue()){
								break;
							}
						}
						
						ActiveReturn returnForSource=activeForSourceMap.get(key);
						if(returnForSource!=null){
							activeMap.put(key, returnForSource);
						}
					}
				}
			}
		}
		
//		if(!activeResult.upFlagTrue()){
//			return null;
//		}
		
		return activeMap;
	}
	
	/**
	 * 商品类活动管道
	 * @param activeRequest
	 * @param activeResult
	 * @return
	 */
	public ActiveReturn activeGallery(ActiveReq activeRequest,ActiveResult activeResult) {
		List<ActiveReq> activeRequests = new ArrayList<ActiveReq>();
		activeRequests.add(activeRequest);
		Map<String, ActiveReturn> map=activeGallery(activeRequests, activeResult);
		
		ActiveReturn activeReturn = null;
		
		if(map!=null){
			for (Map.Entry<String, ActiveReturn> active : map.entrySet()) {
				activeReturn=active.getValue();
				break;
			}
		}
		return activeReturn;
	}
	
	
	public  Map<String, BigDecimal> upPrice(List<ActiveReq> activeRequests){
		ActiveForSource activeForSource = new ActiveForSource();
		return activeForSource.upSkuPrice(activeRequests);
	}
	
	public  BigDecimal upPrice(ActiveReq activeRequest){
		ActiveForSource activeForSource = new ActiveForSource();
		List<ActiveReq> activeRequests = new ArrayList<ActiveReq>();
		activeRequests.add(activeRequest);
		return activeForSource.upSkuPrice(activeRequests).get(activeRequest.getSku_code());
	}
	
	public static void main(String[] args) {
		
		ActiveReq activeReq = new ActiveReq();
		activeReq.setSku_code("8019409933");
		//System.out.println(new ActiveForproduct().activeGallery(activeReq, null));
		
	}		
			
}
