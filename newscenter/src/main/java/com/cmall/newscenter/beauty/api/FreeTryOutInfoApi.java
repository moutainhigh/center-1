package com.cmall.newscenter.beauty.api;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cmall.newscenter.beauty.model.FreeTryOutInfoInput;
import com.cmall.newscenter.beauty.model.FreeTryOutInfoResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.cmall.newscenter.util.DateUtil;
import com.cmall.productcenter.model.PcFreeTryOutGood;
import com.cmall.productcenter.model.PicInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForMember;

/**
 * 免费试用详情Api
 * @author houwen
 * date: 2014-09-18
 * @version1.0
 */
public class FreeTryOutInfoApi extends RootApiForMember<FreeTryOutInfoResult, FreeTryOutInfoInput> {
	

	public FreeTryOutInfoResult Process(FreeTryOutInfoInput inputParam,
			MDataMap mRequestMap) {
		
		FreeTryOutInfoResult result = new FreeTryOutInfoResult();
		int count = 0;
		// 设置相关信息
		if (result.upFlagTrue()) {
			
			  ProductService productService = new ProductService();
			  			  
			  MDataMap mDataMap = new MDataMap();
			  MPageData mPageData = new MPageData();
			 
			  if("SI2007".equals(getManageCode())){
				  result.setLinkUrl(bConfig("systemcenter.shareLink")+"/cbeauty/web/product/tryProductDetail?sku_code="+inputParam.getSku_code()+"&end_time="+URLEncoder.encode(inputParam.getEnd_time()));  
			  }else if("SI2013".equals(getManageCode())){
				  result.setLinkUrl(bConfig("systemcenter.cyoungLink")+"/cyoung/web/product/tryProductDetail?sku_code="+inputParam.getSku_code()+"&end_time="+URLEncoder.encode(inputParam.getEnd_time()));
			  }
			  	
			  List<Map<String, Object>>  resultMap = productService.getFreeTryOutGoodsForSkuCode(inputParam.getSku_code(),"449746930003",getFlagLogin()?getOauthInfo().getUserCode():null,getManageCode(),inputParam.getEnd_time());
			  
			  
			  if(resultMap.size()!=0){
				  for(Map<String, Object> map : resultMap){
					  PcFreeTryOutGood pcFreeTryOutGood = (PcFreeTryOutGood) map.get("freeGood");
					  if(pcFreeTryOutGood!=null){
							  
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
						  result.setSystemtime(DateUtil.getSysDateTimeString());   //系统时间
						  String dString = DateUtil.getSysDateTimeString();
						  result.setTime(pcFreeTryOutGood.getEndTime());     //结束时间
						  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						  try {
								Date date = dateFormat.parse(pcFreeTryOutGood.getEndTime());
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
						 // float endTime = Float.parseFloat(pcFreeTryOutGood.getEndTime().replace(":", "").replace("-", "").replace(" ", ""));
						  String endTime = pcFreeTryOutGood.getEndTime();
						  //非审核试用时申请人数
						   // count = pcFreeTryOutGood.getInitInventory()-pcFreeTryOutGood.getTryoutInventory();
						  
						  //从免费试用申请表里申请
				    		MDataMap mWhereMapApply = new MDataMap();
							MPageData mPageDataApply = new MPageData();
							int size = 0;
							mWhereMapApply.put("sku_code", pcFreeTryOutGood.getSkuCode());
							mWhereMapApply.put("end_time", pcFreeTryOutGood.getEndTime());
							mPageDataApply = DataPaging.upPageData("nc_freetryout_apply", "", "", mWhereMapApply,new PageOption());
							if(mPageDataApply.getListData().size()!=0){
								size = mPageDataApply.getListData().size();
							}
						  result.setTryout_count(String.valueOf(size));  //试用商品申请 人数
						  result.setSurplus_count(String.valueOf(pcFreeTryOutGood.getTryoutInventory()));//商品剩余件数
						  result.setActivityCode(pcFreeTryOutGood.getActivityCode()); //活动编号
						  if(!getFlagLogin()){  // 如果未登录，
							  if(dString.compareTo(endTime)>0){
								  result.setStatus("449746890004"); //已结束
							  }else {
								  result.setStatus("449746890001"); //未申请
							}
							  
						  }else {
							  if(pcFreeTryOutGood.getTryoutStatus().equals("0")){  //如果还未生成订单，则去查询审核表
								  mDataMap.put("member_code", getOauthInfo().getUserCode());
								  mDataMap.put("sku_code",inputParam.getSku_code());
								  mDataMap.put("end_time", inputParam.getEnd_time());
								  mPageData = DataPaging.upPageData("nc_freetryout_apply", "", "", mDataMap, new PageOption());
								  if(mPageData.getListData().size()!=0){
									 // int applyNum =  DbUp.upTable("nc_freetryout_apply").count("sku_code",inputParam.getSku_code());
									  //审核试用的申请人数
									//  result.setTryout_count(String.valueOf(applyNum));
							
									  result.setStatus(mPageData.getListData().get(0).get("status"));
										 
								 }else {
									 if(dString.compareTo(endTime)>0){
										 result.setStatus("449746890004"); //已结束
									 }else {
										 result.setStatus("449746890001");//未申请
									}
									 
								}
								  
							}else if(pcFreeTryOutGood.getTryoutStatus().equals("4497153900010003") || pcFreeTryOutGood.getTryoutStatus().equals("4497153900010004")){
								  result.setStatus("449746890005");  //已发货
							  }else {
								  result.setStatus("449746890003");  //申请通过
							  }
						  }
						 
					  result.setDescribe(pcFreeTryOutGood.getNotice());  //使用须知
				 }
			 }
		}
	}
		
		return result;
	}
}

