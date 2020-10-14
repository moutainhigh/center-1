package com.cmall.newscenter.api;


import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.AdvertisementBanneResult;
import com.cmall.newscenter.model.AdvertisementBanner;
import com.cmall.newscenter.model.AdvertisementBannerInput;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;
/**
 * 广告API
 * @author shiyz
 * date 2014-7-8
 * @version 1.0
 */
public class AdvertisementBannerApi extends
		RootApiForManage<AdvertisementBanneResult, AdvertisementBannerInput> {

	public AdvertisementBanneResult Process(
			AdvertisementBannerInput inputParam, MDataMap mRequestMap) {

		AdvertisementBanneResult result = new AdvertisementBanneResult();
		
		String app_code = bConfig("newscenter.app_code");

		if (result.upFlagTrue()) {

			
			List<MDataMap> mDataMap = new ArrayList<MDataMap>();
			
			int location = inputParam.getLocation();
			/*判断传入的值是否为0--首页*/
			if(location==0){
				
				/*获取当前时间*/
				String currentTime = FormatHelper.upDateTime();
				
                String sWhere = "start_time<='"+currentTime+"' and end_time>='"+currentTime+"' and place_code='AdP140814100001' and app_code=:app_code";		
                
                MDataMap mWhereMap = new MDataMap();
                
				mWhereMap.put("app_code", app_code);
				
				mDataMap = DbUp.upTable("nc_advertise").queryAll("", "-ad_sort", sWhere, mWhereMap);
				
				
				if(mDataMap.size()!=0){
					
				
					for(MDataMap dataMap:mDataMap ){
					
					AdvertisementBanner banners = new AdvertisementBanner();
					
					/*获取创建时间*/
					banners.setCreated_at(dataMap.get("create_time"));
					
					/*获取标题-广告名称*/
					banners.setTitle(dataMap.get("ad_name"));
					
					String type = dataMap.get("adImg_url").substring(0,dataMap.get("adImg_url").indexOf("@"));
					
					String url = dataMap.get("adImg_url").substring(dataMap.get("adImg_url").indexOf("@")).replace("@","");
					
					
					if("code".equals(type)){
						
					    MDataMap skuMap = DbUp.upTable("pc_skuinfo").one("sku_code", url);	
					    
					    MDataMap  productMap = DbUp.upTable("pc_productinfo").one("product_code", url);
					    
					    if(skuMap!=null){
					    	banners.setUrlType(6);
					    }
					    if(productMap!=null){
					    	banners.setUrlType(1);
					    }
						
						
						
					}else if ("informationCode".equals(type)){
						
						MDataMap map =  DbUp.upTable("nc_info").one("info_code",url);
						
                   if(map.get("info_category").equals("44974650000100060001")){
							
						banners.setUrlType(5);
							
						}else {
							
						banners.setUrlType(2);
						
						}
					}else if("columnCode".equals(type)){
						
						banners.setUrlType(3);
						
					}else {
						
						banners.setUrlType(4);
					}
					
					/*获取链接地址*/
					banners.setUrl(url);
					/*获取图片-缩略图*/
					banners.getPhoto().setThumb(dataMap.get("adImg"));
					
					/*获取图片-原图*/
					banners.getPhoto().setLarge(dataMap.get("adImg"));
					
					/*将获取的信息放入List中返回*/
					result.getBanners().add(banners);
					
					
				}
				}
				
				/*判断传入的值是否为1--品牌*/
			}else if(location==1){
				
				/*获取当前时间*/
				String currentTime = FormatHelper.upDateTime();
				
                String sWhere = "start_time<='"+currentTime+"' and end_time>='"+currentTime+"' and place_code='AdP140814100002' and app_code=:app_code";		
                
                MDataMap mWhereMap = new MDataMap();
                
                mWhereMap.put("app_code", app_code);
				mDataMap = DbUp.upTable("nc_advertise").queryAll("", "-ad_sort", sWhere, mWhereMap);
				
				if(mDataMap.size()!=0){
					
				
					for(MDataMap dataMap:mDataMap ){
					
					AdvertisementBanner banners = new AdvertisementBanner();
					
					/*获取创建时间*/
					banners.setCreated_at(dataMap.get("create_time"));
					
					/*获取标题-广告名称*/
					banners.setTitle(dataMap.get("ad_name"));
					
					
                    String type = dataMap.get("adImg_url").substring(0,dataMap.get("adImg_url").indexOf("@"));
					
                    String url = dataMap.get("adImg_url").substring(dataMap.get("adImg_url").indexOf("@")).replace("@","");
                   
						
					if("code".equals(type)){
						
                        MDataMap skuMap = DbUp.upTable("pc_skuinfo").one("sku_code", url);	
					    
					    MDataMap  productMap = DbUp.upTable("pc_productinfo").one("product_code", url);
					    
					    if(skuMap!=null){
					    	banners.setUrlType(6);
					    }
					    if(productMap!=null){
					    	banners.setUrlType(1);
					    }
							
						
					}else if ("informationCode".equals(type)){
						
						 MDataMap map =  DbUp.upTable("nc_info").one("info_code",url);
						
						if(map.get("info_category").equals("44974650000100060001")){
							
						banners.setUrlType(5);
								
							}else {
						
						banners.setUrlType(2);
						
							}
						
					}else if("columnCode".equals(type)){
						
						banners.setUrlType(3);
						
					}else {
						
						banners.setUrlType(4);
					}
					
					/*获取链接地址*/
					banners.setUrl(url);
					/*获取图片-缩略图*/
					banners.getPhoto().setThumb(dataMap.get("adImg"));
					
					/*获取图片-原图*/
					banners.getPhoto().setLarge(dataMap.get("adImg"));
					
					/*将获取的信息放入List中返回*/
					result.getBanners().add(banners);
					
				}
				}
				
				/*判断传入的值是否为2--社区*/
			}else if(location==2){
				
				
                String currentTime = FormatHelper.upDateTime();
				
                String sWhere = "start_time<='"+currentTime+"' and end_time>='"+currentTime+"' and place_code='AdP140814100003' and app_code=:app_code";		
                
                MDataMap mWhereMap = new MDataMap();
                mWhereMap.put("app_code", app_code);
                mDataMap = DbUp.upTable("nc_advertise").queryAll("", "-ad_sort", sWhere, mWhereMap);
				
				if(mDataMap.size()!=0){
					
				
					for(MDataMap dataMap:mDataMap ){
					
					AdvertisementBanner banners = new AdvertisementBanner();
					
					/*获取创建时间*/
					banners.setCreated_at(dataMap.get("create_time"));
					
					/*获取标题-广告名称*/
					banners.setTitle(dataMap.get("ad_name"));
					
                    String type = dataMap.get("adImg_url").substring(0,dataMap.get("adImg_url").indexOf("@"));
					
                    String url = dataMap.get("adImg_url").substring(dataMap.get("adImg_url").indexOf("@")).replace("@","");
                    
						
					if("code".equals(type)){
						
                        MDataMap skuMap = DbUp.upTable("pc_skuinfo").one("sku_code", url);	
					    
					    MDataMap  productMap = DbUp.upTable("pc_productinfo").one("product_code", url);
					    
					    if(skuMap!=null){
					    	banners.setUrlType(6);
					    }
					    if(productMap!=null){
					    	banners.setUrlType(1);
					    }
							
						
					}else if ("informationCode".equals(type)){
						
						MDataMap map =  DbUp.upTable("nc_info").one("info_code",url);
						
                     if(map.get("info_category").equals("44974650000100060001")){
							
						banners.setUrlType(5);
								
							}else {
						
						banners.setUrlType(2);
						
							}
						
					}else if("columnCode".equals(type)){
						
						banners.setUrlType(3);
						
					}else {
						
						banners.setUrlType(4);
					}
					
					/*获取链接地址*/
					banners.setUrl(url);
					/*获取图片-缩略图*/
					banners.getPhoto().setThumb(dataMap.get("adImg"));
					
					/*获取图片-原图*/
					banners.getPhoto().setLarge(dataMap.get("adImg"));
					
					/*将获取的信息放入List中返回*/
					result.getBanners().add(banners);
					
				}
				}
				
			}
		
	}
		return result;
}
}
