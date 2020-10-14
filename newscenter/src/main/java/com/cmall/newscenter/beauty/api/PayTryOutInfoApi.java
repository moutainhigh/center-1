package com.cmall.newscenter.beauty.api;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cmall.newscenter.beauty.model.PayTryOutInfoInput;
import com.cmall.newscenter.beauty.model.PayTryOutInfoResult;
import com.cmall.newscenter.util.DateUtil;
import com.cmall.productcenter.model.PcFreeTryOutGood;
import com.cmall.productcenter.model.PicInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForMember;

/**
 * 付邮试用详情Api
 * @author houwen
 * date: 2014-09-16
 * @version1.0
 */
public class PayTryOutInfoApi extends RootApiForMember<PayTryOutInfoResult, PayTryOutInfoInput> {
	

	public PayTryOutInfoResult Process(PayTryOutInfoInput inputParam,
			MDataMap mRequestMap) {
		
		PayTryOutInfoResult result = new PayTryOutInfoResult();
		
		// 设置相关信息
		if (result.upFlagTrue()) {
			  ProductService productService = new ProductService();
			  
			  if("SI2007".equals(getManageCode())){
				  result.setLinkUrl(bConfig("systemcenter.shareLink")+"/cbeauty/web/product/payProductDetail?sku_code="+inputParam.getSku_code()+"&end_time="+URLEncoder.encode(inputParam.getEnd_time()));  
			  }else if("SI2013".equals(getManageCode())){
				  result.setLinkUrl(bConfig("systemcenter.cyoungLink")+"/cyoung/web/product/payProductDetail?sku_code="+inputParam.getSku_code()+"&end_time="+URLEncoder.encode(inputParam.getEnd_time()));
			  }
			  
			  List<Map<String, Object>>  resultMap = productService.getFreeTryOutGoodsForSkuCode(inputParam.getSku_code(),"449746930002",getFlagLogin()?getOauthInfo().getUserCode():null,getManageCode(),inputParam.getEnd_time());
			  for(Map<String, Object> map : resultMap){
			  PcFreeTryOutGood pcFreeTryOutGood = (PcFreeTryOutGood) map.get("freeGood");
			  if(pcFreeTryOutGood!=null){
				  
			  result.setProduct_code(pcFreeTryOutGood.getpInfo().getProductCode());
			 // result.setPhoto(pcFreeTryOutGood.getpInfo().getMainPicUrl());
			  
			    //产品定的是取轮播图的第一张,如果轮播图为空，则展示主图
				//for (int i = 0; i < pcFreeTryOutGood.getpInfo().getPcPicList().size(); i++) {
				if(pcFreeTryOutGood.getpInfo().getPcPicList().size()!=0){
				//result.getPhotos().add(free.getpInfo().getPcPicList().get(i).getPicUrl());
				if("".equals(inputParam.getPicWidth())||null==inputParam.getPicWidth()){
					

					PicInfo pic = productService.getPicInfo(null,pcFreeTryOutGood.getpInfo().getPcPicList().get(0).getPicUrl());
					
					result.setPhoto(pic.getPicNewUrl()
							);
					
				}else{

					PicInfo pic = productService.getPicInfo(Integer.valueOf(inputParam.getPicWidth()), pcFreeTryOutGood.getpInfo().getPcPicList().get(0).getPicUrl());
					
					result.setPhoto(pic.getPicNewUrl()
							);
					
				}
				
				}else {
					  result.setPhoto(pcFreeTryOutGood.getpInfo().getMainPicUrl());
				}
			  
			  
			  result.setName(pcFreeTryOutGood.getSkuName());
			  result.setSku_code(pcFreeTryOutGood.getSkuCode());
			  result.setOld_price(pcFreeTryOutGood.getpInfo().getMarketPrice().toString());  //市场价=原价
			  if(pcFreeTryOutGood.getTryoutPrice().equals("") || pcFreeTryOutGood.getTryoutPrice()==null){
				  result.setTryout_price("0");
			  }else {
				  result.setTryout_price(pcFreeTryOutGood.getTryoutPrice());
		      }
			  result.setCount(String.valueOf(pcFreeTryOutGood.getInitInventory()));
			  result.setSurplus_count(String.valueOf(pcFreeTryOutGood.getTryoutInventory())); //商品剩余件数
			  int surplus_count = pcFreeTryOutGood.getTryoutInventory();
			  result.setPostage(pcFreeTryOutGood.getPostage().toString());  
			  result.setActivityCode(pcFreeTryOutGood.getActivityCode());//活动编号
			  result.setDescribe(pcFreeTryOutGood.getNotice());  //使用须知   
			  //SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
			 // result.setSystemtime(sf.format(new Date()));   //系统时间
			  String  dString  = FormatHelper.upDateTime();
			  result.setTime(pcFreeTryOutGood.getEndTime());     //结束时间
			  String  endTime = pcFreeTryOutGood.getEndTime();
			  if(!getFlagLogin()){  // 如果未登录，返回未申请
				  if(endTime.compareTo(dString)<0 || surplus_count <= 0){
					  result.setSurplus_count("0");
					  result.setStatus("449746890004"); //已结束
				  }else {
					  result.setStatus("449746890001"); //未申请
				}
				  
			  }else {
			  if(pcFreeTryOutGood.getTryoutStatus().equals("0")){  //如果还未生成订单
				  if(endTime.compareTo(dString)<0 || surplus_count<=0){
					  result.setSurplus_count("0");
					  result.setStatus("449746890004");//已结束
				  }else {
					  result.setStatus("449746890001");//未申请
				}
			  //result.setStatus(pcFreeTryOutGood.getTryoutStatus()); //申请状态 
			  }else if(pcFreeTryOutGood.getTryoutStatus().equals("4497153900010003") || pcFreeTryOutGood.getTryoutStatus().equals("4497153900010004") || pcFreeTryOutGood.getTryoutStatus().equals("4497153900010005")){
				  result.setStatus("449746890005");  //已发货
			  }else if(pcFreeTryOutGood.getTryoutStatus().equals("4497153900010006")){
				  
				  result.setStatus("449746890001");  //未申请
			   }else {
				   result.setStatus("449746890006");  //已试用
		     	}
			 
			  }
			  }
			  }
			 }
			
		return result;
	}
}

