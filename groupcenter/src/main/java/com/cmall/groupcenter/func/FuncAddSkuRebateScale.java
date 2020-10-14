package com.cmall.groupcenter.func;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncAddSkuRebateScale extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mWebResult = new MWebResult();
		MDataMap mInputMap = upFieldMap(mDataMap);
		
		String skuCodeString=mInputMap.get("sku_code");
		String rebateScaleString=mInputMap.get("rebate_scale");
		String productCodeString=mInputMap.get("product_code");
		String productNameString=mInputMap.get("product_name");
		String sellPriceString=mInputMap.get("sell_price");
        String sellerCodeString=mInputMap.get("seller_code");
        String startTimeString=mInputMap.get("start_time");
        String endTimeString=mInputMap.get("end_time");
        
        //判断开始时间
  		if(startTimeString==null||StringUtils.isBlank(startTimeString)){
  			startTimeString=FormatHelper.upDateTime();
  		}
  		else{
  			if(DateHelper.parseDate(startTimeString).before(new Date())){
  				mWebResult.inErrorMessage(918519004);
  			}
  		}
  		
  		//判断结束时间
  		if(mWebResult.upFlagTrue()){
  			if(endTimeString==null||StringUtils.isBlank(endTimeString)){
  				endTimeString="2099-01-01 00:00:00";
  			}
  			else{
  				if(DateHelper.parseDate(endTimeString).before(new Date())){
  					mWebResult.inErrorMessage(918519005);
  				}
  			}
  		}
  		
  		//判断开始时间和结束时间
  		if(mWebResult.upFlagTrue()){
  			if(DateHelper.parseDate(startTimeString).after(DateHelper.parseDate(endTimeString))){
  				mWebResult.inErrorMessage(918519006);
  			}
  		}
        
		//判断是否选择商品
		if(mWebResult.upFlagTrue()){
			if(skuCodeString==null||StringUtils.isBlank(skuCodeString)){
				mWebResult.inErrorMessage(918519001);//没有选择商品
			}
		}
		
		String[] rebateScales=rebateScaleString.split(",");
		//判断数在0-100之间
		if(mWebResult.upFlagTrue()){
			for(String rebateScale:rebateScales){
				if(0>Double.valueOf(rebateScale).doubleValue()||100<Double.valueOf(rebateScale).doubleValue()){
					mWebResult.inErrorMessage(918519002);//0-100的数
					break;
				}
			}
			
		}
		
		String[] skuCodes=skuCodeString.split(",");
		String[] sellerCodes=sellerCodeString.split(",");
		//判断是否存在有效的商品比例
		if(mWebResult.upFlagTrue()){
			for(int i=0;i<skuCodes.length;i++){
				if(DbUp.upTable("gc_sku_rebate_scale").
						dataCount(" sku_code=:sku_code and app_code=:app_code and flag_enable=1 and status=1 and end_time>=:startTime ", new MDataMap("sku_code",skuCodes[i],"app_code",sellerCodes[i],"datenow",FormatHelper.upDateTime(),
								"startTime",startTimeString,"endTime",endTimeString))>0){
					mWebResult.inErrorMessage(918519003,skuCodes[0]);//有存在的有效的商品比例
					break;
				}
			}
		}
		
		//判断sku是否重复
		if(mWebResult.upFlagTrue()){
			Map<String,Object> skuMap=new HashMap<String, Object>();
			for(String skuCode:skuCodes){
				if(skuMap.containsKey(skuCode)){
					mWebResult.inErrorMessage(918519008,skuCode);
					break;
				}
				else{
					skuMap.put(skuCode, "");
				}
				
			}
		}
		
		//插入
		if(mWebResult.upFlagTrue()){
			
			String[] productCodes=productCodeString.split(",");
			String[] productNames=productNameString.split(",");
			String[] sellPrices=sellPriceString.split(",");
			
			for(int i=0;i<skuCodes.length;i++){
				MDataMap map=new MDataMap();
				map.inAllValues("product_code",productCodes[i],"sku_code",skuCodes[i],"product_name",productNames[i],
						"sell_price",sellPrices[i],"rebate_scale",rebateScaleString,"start_time",startTimeString,
						"end_time",endTimeString,"operator",UserFactory.INSTANCE.create().getLoginName(),
						"create_time",FormatHelper.upDateTime(),"flag_enable","1","status","1","app_code",sellerCodes[i]);
				DbUp.upTable("gc_sku_rebate_scale").dataInsert(map);
			}
			
		}
		return mWebResult;
	}

}
