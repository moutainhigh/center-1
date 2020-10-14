package com.cmall.newscenter.beauty.api;

import java.util.List;

import com.cmall.newscenter.beauty.model.Commentator;
import com.cmall.newscenter.beauty.model.ProductComment;
import com.cmall.newscenter.beauty.model.ProductCommentListInput;
import com.cmall.newscenter.beauty.model.ProductCommentListResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.util.DataPaging;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 获取商品评论信息列表
 * @author houwen
 * date 2014-08-21
 * @version 1.0
 */
public class ProductCommentListApi extends RootApiForManage<ProductCommentListResult, ProductCommentListInput> {

	public ProductCommentListResult Process(ProductCommentListInput inputParam,
			MDataMap mRequestMap) {
		
		ProductCommentListResult result = new ProductCommentListResult();
		ProductService productService = new ProductService();
		if(result.upFlagTrue()){
			
			String sku_code = inputParam.getSku_code();
			 
			//判断传入的是商品编码还是sku编码        如果传入的是商品编码     查出对应的sku编码（广告管理传入的是商品编码）
			if(sku_code.substring(0, 4).equals("8016") || sku_code.substring(0, 5).equals("i8016")){
				MDataMap whereMap =  new MDataMap();
				whereMap.put("product_code",sku_code);
				List<MDataMap> skucodelist =DbUp.upTable("pc_skuinfo").queryAll("sku_code","","", whereMap);
				if(skucodelist!=null && !"".equals(skucodelist) && skucodelist.size()!=0){
					sku_code = skucodelist.get(0).get("sku_code");
				}
			}
			
			MDataMap mWhereMap = new MDataMap();
			
			/*将sku,app编号放入map中*/
			mWhereMap.put("order_skuid",sku_code);
			mWhereMap.put("manage_code", getManageCode());
			mWhereMap.put("check_flag","4497172100030002");
			mWhereMap.put("flag_show","449746530001");
			
			MDataMap mSkuDataMap = new MDataMap();
			MPageData mPageData = new MPageData();
			
			/*根据app_code,sku_code查询商品评论列表*/
			mPageData = DataPaging.upPageData("nc_order_evaluation", "", "-oder_creattime", mWhereMap,inputParam.getPaging());
			
			if(mPageData.getListData().size()!=0){
			
			for(MDataMap mDataMap : mPageData.getListData()){
				
				ProductComment productComment = new ProductComment();
				
			    /*app编号*/
				productComment.setApp_code(mDataMap.get("manage_code"));
				
				/*评论内容*/
				productComment.setComment_content(mDataMap.get("order_assessment"));
				
				/*评论时间*/
				String commentTime = mDataMap.get("oder_creattime").split(" ")[0];
				
				productComment.setComment_time(commentTime);
				
				/*评论印象标签*/
				productComment.setLabel(mDataMap.get("label"));
				
				//商品图片
				
				productComment.setPicInfos(productService.getPicForProduct(inputParam.getPicWidth(), mDataMap.get("oder_photos")));

				/*评论人信息*/
				Commentator commentator = new Commentator();
				List<MDataMap>  memberList = this.getNickName(mDataMap.get("order_name"));
				commentator.setMember_code(mDataMap.get("order_name"));
				if(memberList.size()!=0){
					
					commentator.setNickname(memberList.get(0).get("nickname"));
					commentator.setSkin_type(memberList.get(0).get("skin_type"));
					commentator.setMember_avatar(memberList.get(0).get("member_avatar"));
					productComment.setCommentator(commentator);
				}
				
				/*sku编号*/
				productComment.setSku_code(mDataMap.get("order_skuid"));
				
				/*sku名称*/
				
				mSkuDataMap = DbUp.upTable("pc_skuinfo").one("sku_code",
						sku_code);
				
				if(mSkuDataMap!=null){
					
					productComment.setSku_name(mSkuDataMap.get("sku_name"));
				
				}

				result.getProductComment().add(productComment);
			}
			
			result.setPaged(mPageData.getPageResults());
		}
			
		}
		
		
		return result;
	}

	/**
	 * 根据用户Id查询用户昵称
	 * @param member_code
	 * @return
	 */
	public List<MDataMap> getNickName(String member_code){
		
		MDataMap mWhereDataMap = new MDataMap();
		mWhereDataMap.put("member_code", member_code);
		
		List<MDataMap> mDataMap = DbUp.upTable("mc_extend_info_star").queryAll("", "", "", mWhereDataMap);
		return mDataMap;
	}
	
	
}
