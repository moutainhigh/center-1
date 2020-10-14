package com.cmall.newscenter.beauty.api;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.cmall.newscenter.beauty.model.ProductCommentAddInput;
import com.cmall.newscenter.beauty.model.ProductCommentAddResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 添加商品评论信息
 * 
 * @author houwen date 2014-09-11
 * @version 1.0
 */

public class ProductCommentAddApi extends RootApiForToken<ProductCommentAddResult, ProductCommentAddInput> {

	public ProductCommentAddResult Process(ProductCommentAddInput inputParam,
			MDataMap mRequestMap) {

		ProductCommentAddResult result = new ProductCommentAddResult();
		if (result.upFlagTrue()) {

			MDataMap mDataMap = new MDataMap();
			MDataMap mLabelDataMap = new MDataMap();
			MDataMap map = new MDataMap();
			MPageData mLabelListMap = new MPageData();
			// 商品评论信息
			if (getUserCode() != null) {
				
				MDataMap mapList = DbUp.upTable("nc_order_evaluation").one("order_skuid",inputParam.getSku_code(),"order_name", getUserCode(),"manage_code", getManageCode(),"order_code", inputParam.getOrder_code());
				
				if(mapList!=null){
					
					result.setResultCode(934205133);
					result.setResultMessage(bInfo(934205133));
					
				}else {
				
				mDataMap.put("order_name", getUserCode());  
				
				mDataMap.put("manage_code", getManageCode());
				
			
				mDataMap.put("order_assessment", inputParam.getComment_content());
				// 评论时间 为系统时间
				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss"); // 设置日期格式

				mDataMap.put("oder_creattime", df.format(new Date()));

				mDataMap.put("label", inputParam.getLabel());

				/**
				 * 上线下线：449746530001、449746530002  待审核 审核通过 审核拒绝：4497172100030001、4497172100030002、4497172100030003
				 */
				
				mDataMap.put("check_flag", "4497172100030001");  //审核状态
				
				mDataMap.put("flag_show", "449746530001");  //是否上下线（也可叫显示隐藏）
			
				mDataMap.put("oder_photos",inputParam.getPost_img());
				
				mDataMap.put("order_smallphotos",inputParam.getPost_img());
				
				mDataMap.put("order_skuid", inputParam.getSku_code());
				
				mDataMap.put("order_code", inputParam.getOrder_code()); //订单编号
	
				MDataMap skuMap = DbUp.upTable("pc_skuinfo").one("sku_code",inputParam.getSku_code());
				if(skuMap != null){
					mDataMap.put("product_code", skuMap.get("product_code"));
				}
				
				/* 将商品评论信息放入数据库中 */
				DbUp.upTable("nc_order_evaluation").dataInsert(mDataMap);
				
				//将标签信息存入到标签表中
				if(!"".equals(inputParam.getLabel())){
				
				String labels[] = inputParam.getLabel().split(",");
				for(int i = 0;i<labels.length;i++){
					map.put("sku_code",inputParam.getSku_code());
					map.put("label", labels[i]);
					mLabelListMap = DataPaging.upPageData("nc_product_comment_label", "", "", map, new PageOption());
					if(mLabelListMap.getListData().size()!=0){
					//click_count = Integer.parseInt(mLabelListMap.getListData().get(i).get("click_count"))+1;
					//map.put("click_count",String.valueOf(click_count));
					//DbUp.upTable("nc_product_comment_label").dataUpdate(map, "", "sku_code,label");
					
					String sql = "update nc_product_comment_label set click_count = click_count + 1 where sku_code =:sku_code and label in ('" +labels[i]+ "')";
					DbUp.upTable("nc_product_comment_label").dataExec(sql,map);
					
					}else {
						mLabelDataMap.put("sku_code", inputParam.getSku_code());
						mLabelDataMap.put("label", labels[i]);
						//mLabelDataMap.put("click_count", )
						DbUp.upTable("nc_product_comment_label").dataInsert(mLabelDataMap);
					}
				}
				
				}
			}
			}else { 
				
                result.setResultCode(969905917);
				result.setResultMessage(bInfo(969905917));
			}
		
		}

		return result;
	}

}
