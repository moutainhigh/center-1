package com.cmall.ordercenter.familyhas.active.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.familyhas.active.ActiveModel;
import com.cmall.ordercenter.familyhas.active.ActiveReq;
import com.cmall.ordercenter.familyhas.active.ActiveReturn;
import com.cmall.ordercenter.familyhas.active.BaseActive;
import com.cmall.ordercenter.service.FlashsalesSkuInfoService;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 *闪购处理 
 * 
 */
public class ActiveForFlash extends BaseActive{

	@Override
	protected Map<String, ActiveReturn> activeExc(List<ActiveReq> activeRequests, RootResultWeb activeResult) {
		
		Map<String, ActiveReturn> returnMap=new HashMap<String, ActiveReturn>(activeRequests.size());
		
		List<String> sku_codes=new ArrayList<String>(activeRequests.size());
		for (ActiveReq activeReq : activeRequests) {
			sku_codes.add(activeReq.getSku_code().replace("IC_SMG_", ""));
		}
		
		ActiveModel activeModel = getFlashActive();//获取正在进行的闪购活动编号
//		String activity_code = getFlashActive();//获取正在进行的闪购活动编号
		//当前没有闪购活动的情况
		if(activeModel==null){
			
			Map<String, ProductSkuInfo> skuMap = getSkuInfo(sku_codes);
			
			//返回价格信息
			for (ActiveReq activeReq : activeRequests) {
				ActiveReturn activeReturn= new ActiveReturn();
				activeReturn.setActivity_price(skuMap.get(activeReq.getSku_code().replace("IC_SMG_", "")).getSellPrice());
				activeReturn.setUse_activity(false);
				returnMap.put(getKey(activeReq), activeReturn);
			}
			
			return returnMap;
		}
		
		//查看参与闪购活动的商品
		List<MDataMap> flash_sku_list=DbUp.upTable("oc_flashsales_skuInfo").queryAll("", "", "activity_code=:activity_code and status=:status and sku_code in ("+joinStrSql(sku_codes)+")", new MDataMap("activity_code",activeModel.getActivity_code(),"status","449746810001"));
		Map<String, MDataMap> flash_sku_map=new HashMap<String, MDataMap>(flash_sku_list.size());
		for (MDataMap mDataMap : flash_sku_list) {
			flash_sku_map.put(mDataMap.get("sku_code"), mDataMap);
		}
		
		FlashsalesSkuInfoService flashsalesSkuInfoService = new FlashsalesSkuInfoService();
		Map<String, ProductSkuInfo> skuMap = null;
		
		for (ActiveReq activeReq : activeRequests) {
			
			String buyer_code = activeReq.getBuyer_code();
			String sku_code = activeReq.getSku_code();
			int sku_num = activeReq.getSku_num();
			
			ActiveReturn activeReturn= new ActiveReturn();
			
			
			
			if(skuMap==null){
				skuMap = getSkuInfo(sku_codes);
			}
			
			
			//如果参与闪购
			if(flashsalesSkuInfoService.isFlashActiveNow(sku_code, activeModel.getActivity_code(), buyer_code, sku_num)){
				
				activeReturn.setEnd_time(activeModel.getEnd_time());
				activeReturn.setStart_time(activeModel.getStart_time());
				activeReturn.setActivity_code(activeModel.getActivity_code());
				activeReturn.setActivity_type("449715400004");
				activeReturn.setSku_code(sku_code);
				activeReturn.setProduct_code(skuMap.get(sku_code).getProductCode());
				activeReturn.setActivity_price(new BigDecimal(flash_sku_map.get(sku_code).get("vip_price")));
				
			}else{
				
				activeReturn.setActivity_price(skuMap.get(activeReq.getSku_code()).getSellPrice());
				activeReturn.setUse_activity(false);
			}
			
			returnMap.put(getKey(activeReq), activeReturn);
		}
		
		return returnMap;
	}

	/**
	 * 获取正在进行的闪购活动
	 * @return
	 */
	private ActiveModel getFlashActive () {
		String now=DateUtil.getSysDateTimeString();
		MDataMap acticeMap=DbUp.upTable("oc_activity_flashsales").oneWhere("activity_code,end_time,start_time", "", "start_time<=:now and end_time>:now and status=:status and app_code=:app_code","now",now,"status","449746740002","app_code",MemberConst.MANAGE_CODE_HOMEHAS);
		if(acticeMap==null||acticeMap.size()<1){
			return null;
		}
		
		ActiveModel activeModel = new ActiveModel();
		activeModel.setActivity_code(acticeMap.get("activity_code"));
		activeModel.setEnd_time(acticeMap.get("end_time"));
		activeModel.setStart_time(acticeMap.get("start_time"));
		return activeModel;
	}

	
	
}
