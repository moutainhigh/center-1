package com.cmall.productcenter.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.srnpr.xmassystem.load.LoadGiftSkuInfo;
import com.srnpr.xmassystem.load.LoadProductInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelGiftSkuinfo;
import com.srnpr.xmassystem.modelproduct.PlusModelGitfSkuInfoList;
import com.srnpr.xmassystem.modelproduct.PlusModelMediMclassGift;
import com.srnpr.xmassystem.modelproduct.PlusModelProductInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelProductQuery;
import com.srnpr.xmassystem.modelproduct.PlusModelPropertyInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuQuery;
import com.srnpr.xmassystem.util.DateUtil;

public class ProductGiftsSearch {

	/**
	 * 仅供搜索使用       别的地方调用赠品请用这个方法  getProductGifts
	 */
	public Map<String, String> getProductGiftsSearch(String productCodes,String channelId){
		if (StringUtils.isBlank(channelId)) {
			channelId = "449747430001";
		}
		
		//key为productCode,value为赠品
		Map<String,String> resultMap = new HashMap<String, String>();
		if (StringUtils.isEmpty(productCodes)) {
			return resultMap;
		}
		//内联赠品
		Map<String,String> innerGiftMap = new HashMap<String, String>();
		//外联赠品
		Map<String,String> outerGiftMap = new HashMap<String, String>();
		
		String[] productCodeArr = productCodes.split(",");
		
		String gift = "内联赠品";
		
		
		LoadProductInfo loadProductInfo = new LoadProductInfo();
		LoadGiftSkuInfo loadGiftSkuInfo = new LoadGiftSkuInfo();
		for (String productCode : productCodeArr) {
			//获取内联赠品
			PlusModelProductInfo plusModelProductinfo =loadProductInfo.upInfoByCode(new PlusModelProductQuery(productCode));
			if (null != plusModelProductinfo) {
				for (PlusModelPropertyInfo properties : plusModelProductinfo.getPropertyInfoList()) {
					if (gift.equals(properties.getPropertykey())) {
						String startDate = properties.getStartDate();  
						String endDate = properties.getEndDate(); 
						if(StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)){
							SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String currDate=sdf.format(new Date());
							if(startDate.compareTo(currDate) < 0 && currDate.compareTo(endDate) < 0){
								innerGiftMap.put(productCode , properties.getPropertyValue());
							}else{
								innerGiftMap.put(productCode , "");
							}
						}else{  		 // 如果是历史数据，没有被编辑过的商品则开始时间和结束时间都是空，则不做处理 - Yangcl
							innerGiftMap.put(productCode,properties.getPropertyValue());  
						}
					}    
				}
			}
			
			//获取缓存中的外联赠品
			 PlusModelSkuQuery query = new PlusModelSkuQuery();
			 query.setCode(productCode);
			 PlusModelGitfSkuInfoList gitList = loadGiftSkuInfo.upInfoByCode(query);
			 
			 if (null != gitList && null != gitList.getGiftSkuinfos()) {
				 List<PlusModelGiftSkuinfo> giftSkuinfos = gitList.getGiftSkuinfos();
				
				 for (PlusModelGiftSkuinfo plusModelGiftSkuinfo : giftSkuinfos) {
					 String sysTime = DateUtil.getSysDateTimeString();
					 //判断在有效期内
					 if (plusModelGiftSkuinfo.getFr_date().compareTo(sysTime) <= 0
							 && plusModelGiftSkuinfo.getEnd_date().compareTo(sysTime) >= 0) {
						
						 List<PlusModelMediMclassGift> medi_mclssList = plusModelGiftSkuinfo.getMedi_mclss_nm();
						 
						 boolean flag = false;
						 // 2:网站；34:APP通路；39:扫码购；42:微信商城
						 for (PlusModelMediMclassGift plusModelMediMclassGift : medi_mclssList) {
							 //APP通路
							 if ("449747430001".equals(channelId) && "34".equals(plusModelMediMclassGift.getMEDI_MCLSS_ID())) {
								 flag = true;
									break;
							 }
							//网站渠道
							 if (("449747430002".equals(channelId) || "449747430004".equals(channelId)) 
									 && "2".equals(plusModelMediMclassGift.getMEDI_MCLSS_ID())) {
								 flag = true;
									break;
							}
							 //微信商城
							 if ("449747430003".equals(channelId) && "42".equals(plusModelMediMclassGift.getMEDI_MCLSS_ID())) {
								 flag = true;
									break;
								}
							
							
						}
						 if (flag) {
								String giftName = plusModelGiftSkuinfo.getGood_nm();
								if (StringUtils.isNotBlank(outerGiftMap.get(productCode))) {
									giftName = outerGiftMap.get(productCode) + giftName;
								}
								outerGiftMap.put(productCode,giftName+";");
						}
					}
				}
			}
		}
		for (String productCode : productCodeArr) {
			String innerGiftName = innerGiftMap.get(productCode);
			String outerGiftName = outerGiftMap.get(productCode);
			String giftName = "";
			if (StringUtils.isNotEmpty(innerGiftName)) {
				giftName += innerGiftName+";";
				
			}
			if (StringUtils.isNotEmpty(outerGiftName)) {
				giftName += outerGiftName;
			}
			if(StringUtils.isNotBlank(giftName)){
				resultMap.put(productCode, giftName);
			}
		}
		return resultMap;
	}
}
