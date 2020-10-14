package com.cmall.newscenter.beauty.api;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.cmall.newscenter.beauty.model.PostDetailListInput;
import com.cmall.newscenter.beauty.model.PostDetailListResult;
import com.cmall.newscenter.beauty.model.PostFollowList;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.cmall.productcenter.model.PcProductPrice;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.PicInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForMember;

/**
 * 获取姐妹圈发布帖子详情信息列表
 * @author houwen
 * date 2014-08-26
 * @version 1.0
 */
public class PostDetailListApi extends RootApiForMember<PostDetailListResult, PostDetailListInput> {

	public PostDetailListResult Process(PostDetailListInput inputParam,
			MDataMap mRequestMap) {
		
		PostDetailListResult result = new PostDetailListResult();
		
		if(result.upFlagTrue()){
			
			MDataMap mWhereMap = new MDataMap();
			
			MDataMap mWhereMapUser = new MDataMap();
			
			MDataMap mWhereMapFollow = new MDataMap();
			
			MDataMap mWherePraiseMap = new MDataMap();
			 
			MDataMap mWhereCollectMap = new MDataMap();
			
			MDataMap mWhereMapReplay = new MDataMap();
			
			MDataMap mpostDataMap = new MDataMap();
			
			/*将帖子ID编号放入map中*/
			if(inputParam.getPost_code().equals("") || inputParam.getPost_code()==null){
				result.setResultCode(934205117);
				result.setResultMessage("该帖已被删除");
			}else {
			
			mWhereMap.put("post_code", inputParam.getPost_code());
			mWhereMap.put("post_type", "449746780001"); //类型为主帖
			mWhereMap.put("status", "449746730001");//状态为上线的帖子，其实有帖子Id传过来，就肯定是上线的 查询全部帖子，下线的话，提示此帖子已被删除
			mWhereMap.put("app_code", getManageCode());//app
			mWhereMap.put("is_delete", "0");
			
			MPageData mPageDataUser = new MPageData();
			
			MPageData mPageDataFollow = new MPageData();
			
			MPageData mPageDataReplay = new MPageData();
		
			String publisher_nickname = null ;
			
			if("SI2007".equals(getManageCode())){
				result.setLinkUrl(bConfig("systemcenter.shareLink")+"/cbeauty/web/product/sisterInvitation?post_code="+inputParam.getPost_code());
			}else if("SI2013".equals(getManageCode())) {
				result.setLinkUrl(bConfig("systemcenter.cyoungLink")+"/cyoung/web/product/sisterInvitation?post_code="+inputParam.getPost_code());
			}
			

			/*根据帖子ID查询帖子列表*/
			MDataMap mDataMap = DbUp.upTable("nc_posts").one("post_code", inputParam.getPost_code(),"post_type", "449746780001","status", "449746730001","app_code", getManageCode(),"is_delete", "0");
					
			
			ProductService productPric = new ProductService();
			
			if(null!=mDataMap){
					
				//主帖发布人信息列表
	
				if(mDataMap.get("publisher_code")!=null && !mDataMap.get("publisher_code").equals("")){
				mWhereMapUser.put("member_code", mDataMap.get("publisher_code"));
				/*根据发布人ID查询发布人信息列表*/
				mPageDataUser = DataPaging.upPageData("mc_extend_info_star", "", "", mWhereMapUser,new PageOption());

				for(MDataMap mDataMapUser : mPageDataUser.getListData()){
					result.getPostPublisherLists().setMember_code(mDataMap.get("publisher_code"));
					if(mDataMapUser.get("nickname").equals("") || mDataMapUser.get("nickname")==null){
						String mobilePhone = mDataMapUser.get("mobile_phone");
						mobilePhone = mobilePhone.substring(0,mobilePhone.length()-(mobilePhone.substring(3)).length())+"*****"+mobilePhone.substring(8);
						result.getPostPublisherLists().setNickname(mobilePhone);
					}else {
						result.getPostPublisherLists().setNickname(mDataMapUser.get("nickname"));
					}
					result.getPostPublisherLists().setMember_avatar(mDataMapUser.get("member_avatar"));
					result.getPostPublisherLists().setSkin_type(mDataMapUser.get("skin_type"));
					publisher_nickname = mDataMapUser.get("nickname");
				
				}}
				
				//主帖其它相关信息
				result.setPost_code(mDataMap.get("post_code"));
				result.setPost_title(mDataMap.get("post_title"));
				result.setPost_content(mDataMap.get("post_content"));
				result.setIsoffical(mDataMap.get("isofficial"));
				//如果妆品不为空，则将图片返回到妆品，否则返回到主帖
				if(null != mDataMap.get("cosmetic_code") && !mDataMap.get("cosmetic_code").equals("")){
					
					result.getCosmetictinfo().setPhoto(productPric.getPicForMany(Integer.valueOf(inputParam.getPicWidth()), mDataMap.get("post_img")));
				}else {
					result.setPicInfos(productPric.getPicForMany(Integer.valueOf(inputParam.getPicWidth()), mDataMap.get("post_img")));
					PicInfo pic = productPric.getPicInfoForMany(Integer.valueOf(inputParam.getPicWidth()), mDataMap.get("post_img"));
					result.setPost_img(pic.getPicNewUrl());
				}
				
				result.setShare_url(mDataMap.get("share_url"));
				//化妆包信息
				result.getCosmetictinfo().setCosmetic_code(mDataMap.get("cosmetic_code"));
				result.getCosmetictinfo().setCosmetic_name(mDataMap.get("cosmetic_name"));
				result.getCosmetictinfo().setCosmetic_price(mDataMap.get("cosmetic_price"));
				result.getCosmetictinfo().setCount(mDataMap.get("count"));
				result.getCosmetictinfo().setDisabled_time(mDataMap.get("disabled_time"));
				result.getCosmetictinfo().setUnit(mDataMap.get("unit"));
				
				//商品信息
				ProductService productService = new ProductService();
				Map<String,Object> resultMap = productService.getSkuView(getManageCode(),mDataMap.get("product_code"));
				PcProductinfo productinfo = (PcProductinfo)resultMap.get("productInfo");
			    int type = productService.getSkuActivityType(mDataMap.get("product_code"),getManageCode());
			    //查出来是试用  返回普通
			    if(type==2){
			    	type = 0;
			    }
				if(productinfo!=null){
					
					//商品标签
					String [] labls = productinfo.getLabels().split(",");
					
					if(labls!=null){
						for (int j = 0; j < labls.length; j++) {
							result.getProductinfo().getLabels().add(labls[j]);
						}
					}
				result.getProductinfo().setPhotos(productinfo.getProductSkuInfoList().get(0).getSkuPicUrl());
				result.getProductinfo().setName(productinfo.getProductSkuInfoList().get(0).getSkuName());
				result.getProductinfo().setId(productinfo.getProductSkuInfoList().get(0).getSkuCode());
				result.getProductinfo().setStock_num(resultMap.get("skuSellNum").toString());
				result.getProductinfo().setProductType(String.valueOf(type));
				
                ProductService product = new ProductService();
				PcProductPrice productPrice = product.getSkuProductPrice(productinfo.getProductSkuInfoList().get(0).getSkuCode(),getManageCode());
				
				result.getProductinfo().setMarket_price(productPrice.getMarketPrice().toString());
				//有活动价格显示活动价格  没有活动价格显示销售价 
				if(("").equals(productPrice.getVipPrice())||null == productPrice.getVipPrice()){
					
					result.getProductinfo().setSell_price(productPrice.getSellPrice().toString());
				}else{
					
					result.getProductinfo().setSell_price(productPrice.getVipPrice());
				}
				}
				
				PostListApi pApi = new PostListApi();
				SimpleDateFormat sf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
				String time = pApi.transform(mDataMap.get("publish_time"), sf.format(new Date()));
				result.setPublish_time(time);
				result.setPost_praise(mDataMap.get("post_praise"));
				result.setPost_browse(mDataMap.get("post_browse"));
				
				//每次调用帖子详情 浏览数加1
				int browse = Integer.valueOf(mDataMap.get("post_browse"))+1;
				mpostDataMap.put("post_browse", String.valueOf(browse));
				mpostDataMap.put("post_code", inputParam.getPost_code());
				DbUp.upTable("nc_posts").dataUpdate(mpostDataMap, "", "post_code");
				
				 //自己是否对这帖子进行过点赞操作
				if(getFlagLogin()){
				 mWherePraiseMap.put("operater_code",getOauthInfo().getUserCode());
					
				 mWherePraiseMap.put("operate_type","4497464900030006");
					
				 mWherePraiseMap.put("info_code",inputParam.getPost_code());
					
				 mWherePraiseMap.put("app_code",getManageCode());
				 	
				 MPageData moperatePageData = DataPaging.upPageData("nc_post_operate", "", "", mWherePraiseMap,new PageOption());
				 
				 if(moperatePageData.getListData().size()!=0){
					 if(moperatePageData.getListData().get(0).get("flag").equals("1")){
						 result.setIspraise("449746870001");
					 }else{
						 result.setIspraise("449746870002");
					 }
				 }else {
					 result.setIspraise("449746870002");
				}
				}else{
				result.setIspraise("449746870002");
				}
				//自己是否对这帖子进行过收藏操作
				 if(getFlagLogin()){
					 mWhereCollectMap.put("operater_code",getOauthInfo().getUserCode());
						
					 mWhereCollectMap.put("operate_type","4497464900030005");
						
					 mWhereCollectMap.put("info_code",inputParam.getPost_code());
						
					 mWhereCollectMap.put("app_code",getManageCode());
					 	
					 MPageData mCollectPageData = DataPaging.upPageData("nc_post_operate", "", "", mWhereCollectMap,new PageOption());
					 if(mCollectPageData.getListData().size()!=0){
						 if(mCollectPageData.getListData().get(0).get("flag").equals("1")){
							 result.setIscollect("449746860001");
						 }else {
							 result.setIscollect("449746860002");
						}
					 }else {
						 result.setIscollect("449746860002");
					}
				 }else{
				 result.setIscollect("449746860002");
				 }
				
				//追帖信息列表
				mWhereMapFollow.put("post_parent_code", inputParam.getPost_code());
				mWhereMapFollow.put("post_type", "449746780002");
				mWhereMapFollow.put("is_delete", "0");
				mWhereMapFollow.put("status", "449746730001"); //上线的帖子
				mWhereMapFollow.put("app_code", getManageCode());
				mPageDataFollow = DataPaging.upPageData("nc_posts", "", "", mWhereMapFollow, new PageOption());
				int follow_praise = 0 ;
				if(mPageDataFollow!=null){
					
					for(MDataMap mDataMapFollow : mPageDataFollow.getListData()){
						PostFollowList postFollowList = new PostFollowList();
						postFollowList.setPost_code(mDataMapFollow.get("post_code"));
						postFollowList.setPost_parent_code(mDataMapFollow.get("post_parent_code"));
						postFollowList.setPost_content(mDataMapFollow.get("post_content"));
												
						//如果妆品不为空，则将图片返回到妆品，否则返回到主帖
						if(null != mDataMapFollow.get("cosmetic_code") && !mDataMapFollow.get("cosmetic_code").equals("")){
							
							postFollowList.getCosmetictinfo().setPhoto(productPric.getPicForMany(Integer.valueOf(inputParam.getPicWidth()), mDataMapFollow.get("post_img")));
						}else {
							postFollowList.setPicInfos(productPric.getPicForMany(Integer.valueOf(inputParam.getPicWidth()), mDataMapFollow.get("post_img")));
							PicInfo postPicInfo = productPric.getPicInfoForMany(Integer.valueOf(inputParam.getPicWidth()), mDataMapFollow.get("post_img"));
							postFollowList.setPost_img(postPicInfo.getPicNewUrl());
						}
						
						
						//化妆包信息
						postFollowList.getCosmetictinfo().setCosmetic_code(mDataMapFollow.get("cosmetic_code"));
						postFollowList.getCosmetictinfo().setCosmetic_name(mDataMapFollow.get("cosmetic_name"));
						postFollowList.getCosmetictinfo().setCosmetic_price(mDataMapFollow.get("cosmetic_price"));
						postFollowList.getCosmetictinfo().setCount(mDataMapFollow.get("count"));
						postFollowList.getCosmetictinfo().setDisabled_time(mDataMapFollow.get("disabled_time"));
						postFollowList.getCosmetictinfo().setUnit(mDataMapFollow.get("unit"));
						
						//商品信息
						Map<String,Object> resultMap1 = productService.getSkuView(getManageCode(),mDataMapFollow.get("product_code"));
						PcProductinfo productinfo1 = (PcProductinfo)resultMap1.get("productInfo");
					    int types = productService.getSkuActivityType(mDataMapFollow.get("product_code"),getManageCode());
					    //查出来是试用  返回普通
					    if(types==2){
					    	types = 0;
					    }
						if(productinfo1!=null){
							
							//商品标签
							String [] labls = productinfo1.getLabels().split(",");
							
							if(labls!=null){
								for (int j = 0; j < labls.length; j++) {
									postFollowList.getProductinfo().getLabels().add(labls[j]);
								}
							}
						postFollowList.getProductinfo().setPhotos(productinfo1.getProductSkuInfoList().get(0).getSkuPicUrl());
						postFollowList.getProductinfo().setName(productinfo1.getProductSkuInfoList().get(0).getSkuName());
						postFollowList.getProductinfo().setId(productinfo1.getProductSkuInfoList().get(0).getSkuCode());
						postFollowList.getProductinfo().setStock_num(resultMap1.get("skuSellNum").toString());
						postFollowList.getProductinfo().setProductType(String.valueOf(types));
                        ProductService product = new ProductService();
						
						PcProductPrice productPrice = product.getSkuProductPrice(productinfo1.getProductSkuInfoList().get(0).getSkuCode(),getManageCode());
						
						postFollowList.getProductinfo().setMarket_price(productPrice.getMarketPrice().toString());
						//有活动价格显示活动价格  没有活动价格显示销售价 
						if(("").equals(productPrice.getVipPrice())||null == productPrice.getVipPrice()){
							
							postFollowList.getProductinfo().setSell_price(productPrice.getSellPrice().toString());
						}else{
							
							postFollowList.getProductinfo().setSell_price(productPrice.getVipPrice());
						}
						}
						
						String timeFollow = pApi.transform(mDataMapFollow.get("publish_time"), sf.format(new Date()));
						postFollowList.setPublish_time(timeFollow);
					    follow_praise += Integer.valueOf(mDataMapFollow.get("post_praise"));
						postFollowList.setPost_praise(mDataMapFollow.get("post_praise"));
						 //自己是否对这帖子进行过点赞操作
						if(getFlagLogin()){
						 mWherePraiseMap.put("operater_code",getOauthInfo().getUserCode());
							
						 mWherePraiseMap.put("operate_type","4497464900030006");
							
						 mWherePraiseMap.put("info_code",mDataMapFollow.get("post_code"));
							
						 mWherePraiseMap.put("app_code",getManageCode());
						 	
						 MPageData moperatePageData = DataPaging.upPageData("nc_post_operate", "", "", mWherePraiseMap,new PageOption());
						 
						 if(moperatePageData.getListData().size()!=0){
							 if(moperatePageData.getListData().get(0).get("flag").equals("1")){
								 postFollowList.setIspraise("449746870001");
							 }else {
								 postFollowList.setIspraise("449746870002");
							}
						 }else{
							 postFollowList.setIspraise("449746870002"); 
						 }
						}else{
						postFollowList.setIspraise("449746870002");
						}
						result.getPostFollowLists().add(postFollowList);
					}
					
				}
				//评论主帖信息列表  
				mWhereMapReplay.put("post_code", inputParam.getPost_code());
				mWhereMapReplay.put("status", "449746800001");  //审核通过的评论
				mWhereMapReplay.put("app_code", getManageCode()); //app
				mPageDataReplay = DataPaging.upPageData("nc_posts_comment", "", "-publish_time", mWhereMapReplay,new PageOption());
			
				result.setReply_acount(mPageDataReplay.getListData().size());
				int praise = Integer.valueOf(mDataMap.get("post_praise"));
				int post_praise_count = praise+follow_praise;
				result.setPost_praise_count(String.valueOf(post_praise_count));  //总点赞数
			
			}else {
				
				result.setResultCode(934205117);
				result.setResultMessage("该帖已被删除");
			}
			
		}
		}
		return result;
	}

}
