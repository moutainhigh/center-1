package com.cmall.newscenter.beauty.api;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.cmall.newscenter.beauty.model.PostCommentList;
import com.cmall.newscenter.beauty.model.PostCommentListInput;
import com.cmall.newscenter.beauty.model.PostCommentListResult;
import com.cmall.newscenter.beauty.model.PostPublisherList;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.cmall.productcenter.model.PcProductPrice;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForMember;

/**
 * 获取姐妹圈帖子评论信息列表
 * @author houwen
 * date 2014-08-26
 * @version 1.0
 */
public class PostCommentListApi extends RootApiForMember<PostCommentListResult, PostCommentListInput> {

	public PostCommentListResult Process(PostCommentListInput inputParam,
			MDataMap mRequestMap) {
		
		PostCommentListResult result = new PostCommentListResult();
		
		if(result.upFlagTrue()){
			
			MDataMap mWhereMapReplay = new MDataMap();
			MPageData mPageDataReplay = new MPageData();
			MDataMap mWherePraiseMap = new MDataMap();
				if(!inputParam.getPost_code().equals("") && inputParam.getPost_code()!=null){
				//评论主帖信息列表 
				mWhereMapReplay.put("post_code", inputParam.getPost_code());
				mWhereMapReplay.put("app_code", getManageCode());
				//mWhereMapReplay.put("status", "449746800001");
				mPageDataReplay = DataPaging.upPageData("nc_posts_comment", "", "-zid", mWhereMapReplay, inputParam.getPaging());
				if(mPageDataReplay!=null){
					
					for(int i = 0 ;i< mPageDataReplay.getListData().size();i++){
						PostCommentList postCommentList = new PostCommentList();
						
						MDataMap mDataMapReplay =  mPageDataReplay.getListData().get(i);
						/*floor = Integer.parseInt(mDataMapReplay.get("zid"));
						floorRelay = (floor+1)/2;*/
						//评论人的相关信息
						MDataMap mDataMap2 = new MDataMap();
						MPageData mPageData2 = new MPageData();
						PostPublisherList postPublisherList2 = new PostPublisherList();
						mDataMap2.put("member_code",mDataMapReplay.get("publisher_code"));
						mPageData2 = DataPaging.upPageData("mc_extend_info_star", "", "", mDataMap2, new PageOption());
						postPublisherList2.setMember_code(mDataMapReplay.get("publisher_code"));
						if(mPageData2.getListData().size()!=0){
						postPublisherList2.setNickname(mPageData2.getListData().get(0).get("nickname"));
						postPublisherList2.setMember_avatar(mPageData2.getListData().get(0).get("member_avatar"));
						postPublisherList2.setSkin_type(mPageData2.getListData().get(0).get("skin_type"));
						}
						postCommentList.setPostPublisherList(postPublisherList2); //对主帖进行评论，评论人信息
						//被评论人的相关信息
						MDataMap mDataMap3 = new MDataMap();
						MPageData mPageData3 = new MPageData();
						PostPublisherList postPublisherList3 = new PostPublisherList();
						mDataMap3.put("member_code",mDataMapReplay.get("published_code"));
						mPageData3 = DataPaging.upPageData("mc_extend_info_star", "", "", mDataMap3, new PageOption());
						postPublisherList3.setMember_code(mDataMapReplay.get("published_code"));
						if(mPageData3.getListData().size()!=0){
						postPublisherList3.setNickname(mPageData3.getListData().get(0).get("nickname"));
						postPublisherList3.setMember_avatar(mPageData3.getListData().get(0).get("member_avatar"));
						postPublisherList3.setSkin_type(mPageData3.getListData().get(0).get("skin_type"));
						}
						postCommentList.setPublishedList(postPublisherList3); //对主帖进行评论，评论人信息						
						 //自己是否对这帖子进行过点赞操作
						
						if(getFlagLogin()){
						 mWherePraiseMap.put("operater_code",getOauthInfo().getUserCode());
							
						 mWherePraiseMap.put("operate_type","4497464900030006");
							
						 mWherePraiseMap.put("info_code",mDataMapReplay.get("comment_code"));
							
						 mWherePraiseMap.put("app_code",getManageCode());
						 	
						 MPageData moperatePageData = DataPaging.upPageData("nc_post_operate", "", "", mWherePraiseMap,new PageOption());
						   
						 if(moperatePageData.getListData().size()!=0){
							 if(moperatePageData.getListData().get(0).get("flag").equals("1")){
								 postCommentList.setIspraise("449746870001");
							 }else{
								 postCommentList.setIspraise("449746870002");
							 }
						 }else {
					 		 postCommentList.setIspraise("449746870002");
						}
						}else {
							postCommentList.setIspraise("449746870002");
						}
						
						postCommentList.setComment_floor(Integer.valueOf(mDataMapReplay.get("floor")));
						postCommentList.setComment_code(mDataMapReplay.get("comment_code"));
						//如果审核不通过，内容返回 此评论包含敏感词汇，不予显示！
						if(mDataMapReplay.get("status").equals("449746800002")){
							postCommentList.setPost_content("此评论包含敏感词汇，不予显示！");
						}else {
							postCommentList.setPost_content(mDataMapReplay.get("comment_content"));
						}
						PostListApi pApi = new PostListApi();
						SimpleDateFormat sf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
						String time = pApi.transform(mDataMapReplay.get("publish_time"), sf.format(new Date()));
						
						postCommentList.setPublish_time(time);
						if(mDataMapReplay.get("status").equals("449746800002")){
							postCommentList.setPost_praise("-100");
						}else {
							postCommentList.setPost_praise(mDataMapReplay.get("post_praise"));
						}
						
						postCommentList.setType(mDataMapReplay.get("type"));
						
						ProductService productService = new ProductService();
						postCommentList.setPicInfos(productService.getPicForMany(inputParam.getPicWidth(), mDataMapReplay.get("comment_img")));
						
						//商品信息
						Map<String,Object> resultMap = productService.getSkuView(getManageCode(),mDataMapReplay.get("product_code"));
						PcProductinfo productinfo = (PcProductinfo)resultMap.get("productInfo");
					    int type = productService.getSkuActivityType(mDataMapReplay.get("product_code"),getManageCode());
					    //查出来是试用  返回普通
					    if(type==2){
					    	type = 0;
					    }
						if(productinfo!=null){
							
							//商品标签
							String [] labls = productinfo.getLabels().split(",");
							
							if(labls!=null){
								for (int j = 0; j < labls.length; j++) {
									postCommentList.getProductinfo().getLabels().add(labls[j]);
								}
							}
						postCommentList.getProductinfo().setPhotos(productinfo.getProductSkuInfoList().get(0).getSkuPicUrl());
						postCommentList.getProductinfo().setName(productinfo.getProductSkuInfoList().get(0).getSkuName());
						postCommentList.getProductinfo().setId(productinfo.getProductSkuInfoList().get(0).getSkuCode());
						postCommentList.getProductinfo().setStock_num(resultMap.get("skuSellNum").toString());
						postCommentList.getProductinfo().setProductType(String.valueOf(type));
						
		                ProductService product = new ProductService();
						PcProductPrice productPrice = product.getSkuProductPrice(productinfo.getProductSkuInfoList().get(0).getSkuCode(),getManageCode());
						
						postCommentList.getProductinfo().setMarket_price(productPrice.getMarketPrice().toString());
						//有活动价格显示活动价格  没有活动价格显示销售价 
						if(("").equals(productPrice.getVipPrice())||null == productPrice.getVipPrice()){
							
							postCommentList.getProductinfo().setSell_price(productPrice.getSellPrice().toString());
						}else{
							
							postCommentList.getProductinfo().setSell_price(productPrice.getVipPrice());
						}
						
						}
						
						result.getPostsCommentLists().add(postCommentList);
					}
					result.setPaged(mPageDataReplay.getPageResults());
			}
		}
	}
		return result;
 }
}

