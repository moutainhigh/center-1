package com.cmall.groupcenter.third.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.third.model.ProductRebateInfo;
import com.cmall.groupcenter.third.model.ProductRebateResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootApiForMember;

public class ApiGetRebateInfo extends RootApiForManage<ProductRebateResult, RootInput>{

	public ProductRebateResult Process(RootInput inputParam, MDataMap mRequestMap) {
		//获取返利比例
			ProductRebateResult rebateResult=new ProductRebateResult();
			
//			MDataMap memberMap=DbUp.upTable("mc_member_info").one("member_code",getOauthInfo().getUserCode(),
//					"manage_code",getManageCode());
//			MDataMap traderMap=DbUp.upTable("gc_trader_info").one("account_code",memberMap.get("account_code"));
//			String traderCode=traderMap.get("trader_code");
			MDataMap openMap=DbUp.upTable("gc_wopen_appmanage").one("app_code",getManageCode());
			List<MDataMap> productRebateMap=DbUp.upTable("gc_sku_rebate_scale").queryByWhere("trader_code",openMap.get("trade_code"),"flag_enable","1");
			
			List<ProductRebateInfo> rebateInfoList=new ArrayList<ProductRebateInfo>();
			//判断是否设置sku返现比例
			if(productRebateMap==null||productRebateMap.size()==0){
				MDataMap rebateMap=DbUp.upTable("gc_trader_rebate").one("trader_code",openMap.get("trade_code"));
				if(rebateMap!=null&&rebateMap.size()>0){
					ProductRebateInfo info=new ProductRebateInfo();
					info.setRebateScale(rebateMap.get("rebate_rate"));
					info.setRebateRange(rebateMap.get("rebate_range"));
					rebateInfoList.add(info);
				}else{
					rebateResult.setResultCode(200);
					rebateResult.setResultMessage("该商户没有开通微公社，设置返现比例");
				}
				
			}else{
				for(MDataMap skuRebate:productRebateMap){
					ProductRebateInfo info=new ProductRebateInfo();
					info.setRebateScale(skuRebate.get("rebate_scale"));
					info.setRebateRange(skuRebate.get("rebate_range"));
					info.setProductCode(skuRebate.get("product_code"));
					info.setSkuCode(skuRebate.get("sku_code"));
					info.setStartTime(skuRebate.get("start_time"));
					info.setEndTime(skuRebate.get("end_time"));
					rebateInfoList.add(info);
				}
				
			}
			rebateResult.setRebateInfoList(rebateInfoList);
			return rebateResult;
	}

}
