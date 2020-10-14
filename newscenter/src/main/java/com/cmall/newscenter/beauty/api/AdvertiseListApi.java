package com.cmall.newscenter.beauty.api;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.newscenter.beauty.model.Advertise;
import com.cmall.newscenter.beauty.model.AdvertiseListInput;
import com.cmall.newscenter.beauty.model.AdvertiseListResult;
import com.cmall.productcenter.model.PicAllInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 获取广告信息列表
 * @author houwen
 * date 2014-08-21
 * @version 1.0
 */
public class AdvertiseListApi extends RootApiForManage<AdvertiseListResult, AdvertiseListInput> {

	public AdvertiseListResult Process(AdvertiseListInput inputParam,
			MDataMap mRequestMap) {
		
		AdvertiseListResult result = new AdvertiseListResult();
		
		if(result.upFlagTrue()){
			
			MDataMap mWhereMap = new MDataMap();
			
			/*将栏目ID放入map中*/
			//mWhereMap.put("column_code", inputParam.getColumn_code());
			//mWhereMap.put("status", "449746690001");  // 前台只显示状态为上线的广告
			mWhereMap.put("app_code", getManageCode());
			//MPageData mPageData = new MPageData();
			String sql = "select * from nc_advertise n where n.start_time<now() and n.end_time>now() and app_code='" +getManageCode()+ "' order by sort_num desc ";
			List<Map<String, Object>> list = DbUp.upTable("nc_advertise").dataSqlList(sql,mWhereMap);
			//mPageData = DataPaging.upPageData("nc_advertise", "", "-sort_num", mWhereMap,new PageOption());
			
			ProductService productService = new ProductService();
			
			if(list.size()!=0){
			
			for(int i = 0;i<list.size();i++){
				
				Advertise advertise = new Advertise();
				
			    /*广告ID*/
				advertise.setAd_code(list.get(i).get("ad_code").toString());
				
				/*广告名称*/
				advertise.setAd_name(list.get(i).get("ad_name").toString());
				
				/*排序*/
				advertise.setAd_sort(Integer.valueOf(list.get(i).get("ad_sort").toString()));
				
				/*图片*/
				advertise.setAdImg(list.get(i).get("adImg").toString());
				
				/*链接地址*/
				advertise.setAdImg_url(list.get(i).get("adImg_url").toString());
				
				/*版位名称*/
				advertise.setPlace_code(list.get(i).get("place_code").toString());
				
				/*状态*/
				advertise.setStatus(list.get(i).get("status").toString());
				
				String sku_code[] = list.get(i).get("adImg_url").toString().split("@@");
				
				if("url".equals(sku_code[0])){
					/*分享标题*/
					advertise.setShare_title(list.get(i).get("Share_title").toString());
					
					/*分享内容*/
					advertise.setShare_cotent(list.get(i).get("Share_cotent").toString());
					
					/*分享图片*/
					advertise.setShare_pic(list.get(i).get("Share_pic").toString());
				}
				
				//商品编码     查出对应的sku编码（广告管理传入的是商品编码）
				String skuCode = "";
				MDataMap whereMap =  new MDataMap();
				whereMap.put("product_code",sku_code[1]);
				List<MDataMap> skucodelist =DbUp.upTable("pc_skuinfo").queryAll("sku_code","","", whereMap);
				if(skucodelist!=null && !"".equals(skucodelist) && skucodelist.size()!=0){
					skuCode = skucodelist.get(0).get("sku_code").toString();
				}
				
				advertise.setProductType(String.valueOf(productService.getSkuActivityType(skuCode, getManageCode())));
				
				result.getAdvertise().add(advertise);
			}
			
			//result.setPaged(mPageData.getPageResults());
		}
			
		}
		
		
		return result;
	}

	}
