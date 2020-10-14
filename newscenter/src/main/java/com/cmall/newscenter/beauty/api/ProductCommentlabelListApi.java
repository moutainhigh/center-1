package com.cmall.newscenter.beauty.api;


import java.util.List;

import com.cmall.newscenter.beauty.model.ProductCommentLabel;
import com.cmall.newscenter.beauty.model.ProductCommentLabelListInput;
import com.cmall.newscenter.beauty.model.ProductCommentLabelListResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 获取商品评论标签信息列表（对某一商品评论时所选标签的相关信息）  商品评论印象标签统计接口
 * @author houwen
 * date 2014-08-21
 * @version 1.0
 */
public class ProductCommentlabelListApi extends RootApiForManage<ProductCommentLabelListResult, ProductCommentLabelListInput> {

	public ProductCommentLabelListResult Process(ProductCommentLabelListInput inputParam,
			MDataMap mRequestMap) {
		
		ProductCommentLabelListResult result = new ProductCommentLabelListResult();
		
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
			mWhereMap.put("sku_code", sku_code);
			//mWhereMap.put("app_code", getManageCode());
			
			MPageData mPageData = new MPageData();
			
			/*根据app_code,sku_code查询商品评论列表*/
			mPageData = DataPaging.upPageData("nc_product_comment_label", "", "", mWhereMap, new PageOption());
			
			if(mPageData.getListData().size()!=0){
			
			for(MDataMap mDataMap : mPageData.getListData()){
				
				ProductCommentLabel productCommentLabel = new ProductCommentLabel();
				
	/*		    app编号
				productCommentLabel.setApp_code(mDataMap.get("app_code"));
				
				sku编号
				productCommentLabel.setSku_code(mDataMap.get("sku_code"));
				
				sku名称
				productCommentLabel.setSku_name(mDataMap.get("sku_name"));*/
								
				/*评论印象标签*/
				productCommentLabel.setLabel(mDataMap.get("label"));
				
				//String label[] = mDataMap.get("label").split(",");
				//印象标签数量
				productCommentLabel.setLabel_amount(Integer.parseInt(mDataMap.get("click_count")));
				
				result.getProductComment().add(productCommentLabel);
			}
			
		//	result.setPaged(mPageData.getPageResults());
		}
			
		}
		
		
		return result;
	}

}
