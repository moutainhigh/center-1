package com.cmall.groupcenter.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.cmall.groupcenter.func.PublishPost;
import com.cmall.groupcenter.groupapp.model.ShareModel;
import com.cmall.groupcenter.recommend.model.ApiGetRecommendDetailContentResult;
import com.cmall.groupcenter.recommend.model.ApiGetRecommendDetailInput;
import com.cmall.groupcenter.recommend.model.ApiGetRecommendDetailProductResult;
import com.cmall.groupcenter.recommend.model.ApiGetRecommendDetailResult;
import com.cmall.groupcenter.recommend.model.ApiRecommendDetailNcPostContentResult;
import com.cmall.productcenter.model.PicInfo;
import com.srnpr.zapcom.basehelper.RegexHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 微公社帖子相关
 * 
 */
public class NcPostService {
	/**
	 * 获取好物推荐帖子详情
	 * 
	 * @param inputParam
	 * @return
	 * @authore gaozx
	 */
	public ApiGetRecommendDetailResult getRecommendDetailService(
			ApiGetRecommendDetailInput inputParam, String manageCode) {
		ApiGetRecommendDetailResult detailResult = new ApiGetRecommendDetailResult();

		String pid = inputParam.getPid();
		if (pid.endsWith("#")) {
			pid = pid.substring(0, pid.length() - 1);
		}
		// 更新帖子表中的实际购买人数和实际返利金额总和
		PublishPost.updateProBuyerAndReckonCount(null, pid);

		MDataMap postDataMap = DbUp.upTable("nc_post").one("pid", pid,
				"app_code", manageCode);
		if (postDataMap != null) {
			
			detailResult.setPid(postDataMap.get("pid"));
			detailResult.setCoverPicUrl(postDataMap.get("detail_img_url"));
			detailResult.setArticleCoverPicUrl(postDataMap.get("list_img_url"));
			detailResult.setTitle(postDataMap.get("p_title"));
			detailResult.setSeenNum((Integer.parseInt(postDataMap
					.get("actual_browse_num")) + Integer.parseInt(postDataMap
					.get("browse_add_num")))
					+ "");
			detailResult.setCommentsNum(postDataMap.get("comment_num"));
			detailResult.setCollectNum((Integer.parseInt(postDataMap
					.get("actual_collect_num")) + Integer.parseInt(postDataMap
					.get("collect_add_num")))
					+ "");
			detailResult.setSharedNum((Integer.parseInt(postDataMap
					.get("actual_share_num")) + Integer.parseInt(postDataMap
					.get("share_add_num")))
					+ "");
			detailResult.setRebatePersonNum(detailResult.getSharedNum());
			detailResult.setReturnMoney(new BigDecimal(postDataMap
					.get("actual_reckon_money")).add(
					new BigDecimal(postDataMap.get("reckon_add_money")))
					.toString());
			detailResult.setIntro(postDataMap.get("p_intro"));
			
			

			List<MDataMap> contentDataMapList = DbUp.upTable("nc_post_content")
					.queryAll("p_cid, p_content", "zid", "pid='" + pid + "'",
							new MDataMap());
			if (contentDataMapList != null && contentDataMapList.size() > 0) {
				List<ApiGetRecommendDetailContentResult> listContent = new ArrayList<ApiGetRecommendDetailContentResult>();
				for (MDataMap cmap : contentDataMapList) {
					ApiGetRecommendDetailContentResult contentRes = new ApiGetRecommendDetailContentResult();
					contentRes.setP_cid(cmap.get("p_cid"));
					contentRes.setP_title(cmap.get("p_content"));//内容标题
					
					//查询内容详情
					List<MDataMap> detailList = DbUp.upTable("nc_post_content_detail").queryAll("", "sort", "p_cid='"+cmap.get("p_cid")+"'", new MDataMap());
					if(detailList != null && detailList.size()>0){
						List<ApiRecommendDetailNcPostContentResult> textAndImgList = new ArrayList<ApiRecommendDetailNcPostContentResult>();
						for(MDataMap dMap : detailList){
							ApiRecommendDetailNcPostContentResult textAndImg = new ApiRecommendDetailNcPostContentResult();
							textAndImg.setSort(dMap.get("sort"));
							textAndImg.setType(dMap.get("type"));
							textAndImg.setContent(dMap.get("content"));
							textAndImgList.add(textAndImg);
						}
						contentRes.setTextAndImgList(textAndImgList);
					}

					//切割帖子内容
//					List<Map<String, String>> contentList = StringHelper.formatContentByImg(cmap.get("p_content"));
//					contentRes.setTextAndImgList(StringHelper.changeMap2BeanAdapter(contentList));
					
					
					List<MDataMap> productRes = DbUp
							.upTable("nc_post_products")
							.queryAll(
									"product_code, product_name, product_pic, "
											+ "product_price, product_desc, product_source, "
											+ "product_link", "zid",
									"pcid='" + cmap.get("p_cid") + "'",
									new MDataMap());
					if (productRes != null && productRes.size() > 0) {
						List<ApiGetRecommendDetailProductResult> listAdProduct = new ArrayList<ApiGetRecommendDetailProductResult>();
						for (MDataMap pmap : productRes) {
							ApiGetRecommendDetailProductResult products = new ApiGetRecommendDetailProductResult();
							products.setProductCode(pmap.get("product_code"));
							products.setProductName(pmap.get("product_name"));
							products.setProductPic(pmap.get("product_pic"));
							products.setProductPrice(pmap.get("product_price"));
							products.setProductDesc(pmap.get("product_desc"));
							String productSourceCode = pmap.get("product_source");
							String sourceName = null;
							if(!StringUtils.isEmpty(productSourceCode)) {
								MDataMap sourceNameMap = DbUp.upTable("sc_define").one("define_code", productSourceCode);
								if(sourceNameMap != null) {
									sourceName = sourceNameMap.get("define_name");
								}
							}
							sourceName = StringUtils.isEmpty(sourceName) ? productSourceCode : sourceName;
							products.setProductSourceCode(productSourceCode);
							products.setProductSource(sourceName);
							products.setProductLink(pmap.get("product_link"));
							listAdProduct.add(products);
						}
						contentRes.setListAdProduct(listAdProduct);
					}
					listContent.add(contentRes);
				}
				detailResult.setListContent(listContent);
			}
		} else {
			detailResult.setResultCode(918519016);
		}
		return detailResult;
	}

	/**
	 * 判断用户对于某个帖子的收藏状态
	 * 
	 * @param map
	 * @return
	 * @author gaozx
	 */
	public String getFavoriteStateOfUser(MDataMap map) {
		String result = "";
		// MDataMap ncMap =
		// DbUp.upTable("nc_collections").one("post_id",inputParam.getPost_id(),"member_code",getUserCode(),
		// "flag", "4497472000020001", "app_code", super.getManageCode());
		MDataMap ncMap = DbUp.upTable("nc_collections").one("post_id",
				map.get("post_id"), "member_code", map.get("member_code"),
				"flag", "4497472000020001", "app_code", map.get("app_code"));
		if (null != ncMap) {
			result = "4497472000020001";
		} else {
			result = "4497472000020002";
		}
		return result;
	}
	
	/**
	 * 调整好物推荐详情中文章内容中的图文列表的图片大小
	 * @param detailResult
	 * @param detailInput
	 * @return
	 */
	public ApiGetRecommendDetailResult resizeContentImgSize(ApiGetRecommendDetailResult detailResult, ApiGetRecommendDetailInput detailInput) {
		String imageMaxWidthStr = detailInput.getImageMaxWidth();
		if(null != imageMaxWidthStr && RegexHelper.checkRegexField(imageMaxWidthStr, "base=number") 
				&& Integer.parseInt(imageMaxWidthStr) > 0) { //传入宽度大于0
			int imageMaxWidth = Integer.parseInt(imageMaxWidthStr);
			if(detailResult != null && detailResult.getListContent() != null 
					&& detailResult.getListContent().size() > 0) {
				com.cmall.productcenter.service.ProductService productService = new com.cmall.productcenter.service.ProductService();
				//resize之后的图片列表
				List<PicInfo> picInfos = null;
				for(int i=0; i<detailResult.getListContent().size(); i++) {
					ApiGetRecommendDetailContentResult contentResult = detailResult.getListContent().get(i);
					if(contentResult.getTextAndImgList() != null && contentResult.getTextAndImgList().size() > 0) {
						//待修改图片列表
						List<String> imgList = new ArrayList<String>();
						for(ApiRecommendDetailNcPostContentResult detailContentRes : contentResult.getTextAndImgList()) {
							if(null != detailContentRes.getType() && "1".equals(detailContentRes.getType())) {
								imgList.add(detailContentRes.getContent());
							}
						}
						picInfos = productService.getPicInfoOprBigForMulti(imageMaxWidth,imgList);
						
						for(int j=0; j<contentResult.getTextAndImgList().size() && picInfos.size() > 0; j++) {
							ApiRecommendDetailNcPostContentResult detailContentRes = contentResult.getTextAndImgList().get(j);
							if(null != detailContentRes.getType() && "1".equals(detailContentRes.getType())) { //图片
								PicInfo picInfo = picInfos.remove(0);
								detailContentRes.setContent(picInfo.getPicNewUrl());
								detailContentRes.setHeight(picInfo.getHeight() + "");
								detailContentRes.setWidth(picInfo.getWidth() + "");
							}
						}
					}
				}
			}
		}
		return detailResult;
	}
}
