package com.cmall.newscenter.beauty.api;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.cmall.newscenter.beauty.model.ProductCommentLabel;
import com.cmall.newscenter.beauty.model.ProductCommentLabelInput;
import com.cmall.newscenter.beauty.model.ProductCommentLabelResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 获取商品评论标签信息列表（关联到商品分类上的标签）  印象标签接口
 * @author houwen
 * date 2014-08-29
 * @version 1.0
 * 
 */
public class ProductCommentlabelApi extends RootApiForManage<ProductCommentLabelResult, ProductCommentLabelInput> {

	public ProductCommentLabelResult Process(ProductCommentLabelInput inputParam,
			MDataMap mRequestMap) {
		
		ProductCommentLabelResult result = new ProductCommentLabelResult();
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
			MDataMap skuMap = new MDataMap();
			MDataMap mSkuWhereMap = new MDataMap();
			MPageData mSkuPageData = new MPageData();
			List<ProductCommentLabel> productCommentLabels = new ArrayList<ProductCommentLabel>();
			skuMap.put("sku_code", sku_code);
			//根据sku_code查询产品所属的商品分类 
			MPageData mPageData = new MPageData();
			String sql = "select b.category_code from pc_skuinfo a,pc_productcategory_rel b where a.product_code=b.product_code and a.sku_code=:sku_code";
			Map<String,Object> list = DbUp.upTable("pc_skuinfo").dataSqlOne(sql, skuMap);
			/*根据category_code查询商品标签Code*/
			if(list!=null){
			mWhereMap.put("category_code", list.get("category_code").toString());
			mPageData = DataPaging.upPageData("pc_commentlabel_category", "", "", mWhereMap, new PageOption());
			
			if(mPageData.getListData().size()!=0){
			
			for(MDataMap mDataMap : mPageData.getListData()){
				ProductCommentLabel productCommentLabel = new ProductCommentLabel();	
				/*印象标签*/
				MDataMap mDataMapName  = new MDataMap();
				if(mDataMap.get("comment_label_code")!=null && !mDataMap.get("comment_label_code").equals("")){
				mDataMapName.put("label_code", mDataMap.get("comment_label_code"));
				String sqll = "select label_name from pc_comment_labelmanage where label_code in('"+mDataMap.get("comment_label_code")+"')";
			    Map<String,Object> listName = DbUp.upTable("pc_comment_labelmanage").dataSqlOne(sqll,mDataMapName);   //根据label_code查询标签名称 
				//productCommentsLabel.setLabel(listName.get("label_name").toString());
			    mSkuWhereMap.put("sku_code",sku_code);
			    mSkuWhereMap.put("label",listName.get("label_name").toString());
			    mSkuPageData = DataPaging.upPageData("nc_product_comment_label", "", "", mSkuWhereMap, new PageOption());
			    
			    if(mSkuPageData.getListData().size()!=0){
			    	productCommentLabel.setLabel_amount(Integer.parseInt(mSkuPageData.getListData().get(0).get("click_count")));
			    }else{
			    	productCommentLabel.setLabel_amount(0);

			    }
			    
			    productCommentLabel.setLabel(listName.get("label_name").toString());
				//labelName = labelName + listName.get("label_name")+",";
				//}
			}
			   productCommentLabels.add(productCommentLabel);
		
		     }
	      }
			Collections.sort(productCommentLabels, new Comparator() {
			      public int compare(Object label1, Object label2) {
			    	  ProductCommentLabel one = (ProductCommentLabel)label1;
			    	  ProductCommentLabel two = (ProductCommentLabel)label2;
			    	  int oneSellCount = one.getLabel_amount();
				    	int twoSellCount = two.getLabel_amount();
				    	
				    	if (oneSellCount > twoSellCount) {
							return -1;
						}else if (oneSellCount < twoSellCount) {
							return 1;
						}else {
							return 0;
						}
			      }
			    });
			result.setProductComment(productCommentLabels);
       }
     
	}
		return result;
}
}
