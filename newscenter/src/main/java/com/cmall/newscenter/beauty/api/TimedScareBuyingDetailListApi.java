package com.cmall.newscenter.beauty.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cmall.newscenter.beauty.model.TimedScareBuyingDetailListInput;
import com.cmall.newscenter.beauty.model.TimedScareBuyingDetailListResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.cmall.ordercenter.service.FlashsalesSkuInfoService;
import com.cmall.productcenter.model.FlashsalesSkuInfo;
import com.cmall.productcenter.model.PcProductPrice;
import com.cmall.productcenter.model.PicInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForMember;

/**
 * 限时抢购详情列表API
 * @author houwen
 * date: 2014-09-18
 * @version1.0
 */
public class TimedScareBuyingDetailListApi extends RootApiForMember<TimedScareBuyingDetailListResult, TimedScareBuyingDetailListInput> {
	

	public TimedScareBuyingDetailListResult Process(TimedScareBuyingDetailListInput inputParam,
			MDataMap mRequestMap) {
		
		TimedScareBuyingDetailListResult result = new TimedScareBuyingDetailListResult();
		MDataMap mWhereMap = new MDataMap();
		MPageData mPageData = new MPageData();
		int num = 0;
		int stock_num = 0;
		int sales_num = 0;
		int remaind_count = 0;
		// 设置相关信息
		if (result.upFlagTrue()) {
			
			if(!getFlagLogin()){
				result.setFavstatus("0");
			}else{
				
				
				//查出是否收藏过                       
				MDataMap mFavMap = DbUp.upTable("nc_productfav").one("member_code",getOauthInfo().getUserCode(),"app_code",getManageCode(),"product_code",inputParam.getSku_code());
				if(mFavMap!=null){
					
					if(mFavMap.get("flag").equals("1")){
						
						result.setFavstatus("1");
					}else{
						result.setFavstatus("0");
					}
				}else{
					result.setFavstatus("0");
				}
			}
			result.setLinkUrl(bConfig("systemcenter.shareLink")+"/cbeauty/web/product/productRush?sku_code="+inputParam.getSku_code());
			ProductService productService = new ProductService();
			
			List<FlashsalesSkuInfo> skuInfoList = productService.getFlashsalesForSkuCode(inputParam.getSku_code(),getManageCode());
				
			//TimedScareBuying flashSale = new TimedScareBuying();
			if(skuInfoList.size()!=0){
			for(int i=0;i<skuInfoList.size();i++){
			result.setSku_code(skuInfoList.get(i).getSkuCode());
			result.setName(skuInfoList.get(i).getSkuName());
			
			ProductService product = new ProductService();
			
			PcProductPrice productPrice = product.getSkuProductPrice(skuInfoList.get(i).getSkuCode(),getManageCode());
			
			result.setOldPrice(productPrice.getMarketPrice().toString());
			
			//有活动价格显示活动价格  没有活动价格显示销售价
			if(("").equals(productPrice.getVipPrice())||null == productPrice.getVipPrice()){
				
				result.setNewPrice(productPrice.getSellPrice().toString());
			}else{
				
				result.setNewPrice(productPrice.getVipPrice());
			}
			result.setRebate(productPrice.getDiscount());
			
			List<PicInfo>  list  = new ArrayList<PicInfo>();
			
			for (int j = 0; j < skuInfoList.get(i).getProduct().getPcPicList().size(); j++) {
				if("".equals(inputParam.getWidth())||null==inputParam.getWidth()){
					
					PicInfo pic = productService.getPicInfo(null, skuInfoList.get(i).getProduct().getPcPicList().get(j).getPicUrl());
					
					list.add(pic);
					
					result.setPhotos(list);
					
				}else{
					
					PicInfo pic = productService.getPicInfo(Integer.valueOf(inputParam.getWidth()), skuInfoList.get(i).getProduct().getPcPicList().get(j).getPicUrl());
					
                    list.add(pic);
					
					result.setPhotos(list);
					
					
				}
				
			}
			
			//result.setPhotos(skuInfoList.get(i).getProduct().getMainPicUrl());   //主图
			//商品标签
			/*String [] labls = skuInfoList.get(i).getProduct().getLabels().split(",");
			
			if(labls!=null){
				for (int j = 0; j < labls.length; j++) {
					result.getLabels().add(labls[j]);
				}
			}*/
			
			result.setLabels(skuInfoList.get(i).getProduct().getLabels());   //
			result.setProduct_code(skuInfoList.get(i).getProduct().getProductCode());
			//产品详情图片
			String [] ptotos = skuInfoList.get(i).getProduct().getDescription().getDescriptionPic().split("\\|");
			
			if(ptotos!=null){
				for (int j = 0; j < ptotos.length; j++) {
					if("".equals(inputParam.getWidth())||null==inputParam.getWidth()){
						result.getInfophotos().add(productService.getPicInfo(null,ptotos[j]));
						
					}else{
						result.getInfophotos().add(productService.getPicInfo(Integer.valueOf(inputParam.getWidth()),ptotos[j]));
						
					}
					
				}
			}
			//result.setProduct_url(skuInfoList.get(i).getProduct().getDescription().getDescriptionInfo());   //产品详情URl
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			result.setSystemTime(dateFormat.format(new Date())); //当前服务器时间
			result.setEndTime(skuInfoList.get(i).getEndTime());          //结束时间                   
			try {
				Date date = dateFormat.parse(skuInfoList.get(i).getEndTime());
				Long end = (long) date.getTime();
				Long now = (long) new Date().getTime();
				if(end>now){
					Long surTime = (long) ((end - now))/1000;
					result.setSurplusTime(String.valueOf(Integer.valueOf(surTime.toString())));
				}else{
					result.setSurplusTime("0");
				}
				
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
			
		/*	stock_num = skuInfoList.get(i).getStockNum().intValue();
			sales_num = skuInfoList.get(i).getSalesNum().intValue();
		    remaind_count = stock_num-sales_num;*/
			
			//商品剩余件数从别的方法实时取
			FlashsalesSkuInfoService flashsalesSkuInfoService = new FlashsalesSkuInfoService();
			
			int surplusNum = flashsalesSkuInfoService.salesNumSurplus(skuInfoList.get(i).getSkuCode(),skuInfoList.get(i).getActivityCode());
			result.setRemaind_count(String.valueOf(surplusNum));    //商品剩余 件数
			
			//result.setRemaind_count(String.valueOf(skuInfoList.get(i).getSurplusNum()));    //商品剩余 件数
			result.setCount(skuInfoList.get(i).getSalesNum().toString());  //促销库存是商品总件数
			/*将sku,app编号放入map中    查询商品评论数*/
			mWhereMap.put("order_skuid", inputParam.getSku_code());
			mWhereMap.put("manage_code",getManageCode());
			mWhereMap.put("check_flag","4497172100030002");
			
			/*根据app_code,sku_code查询商品评论列表*/
			mPageData = DataPaging.upPageData("nc_order_evaluation", "", "-oder_creattime", mWhereMap, new PageOption());
			if(mPageData!=null){
			num = mPageData.getListData().size();
			}
			result.setComment_count(String.valueOf(num));                              //评论数   
		    }
			}
		}
		return result;
	}
}


