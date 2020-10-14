package com.cmall.productcenter.webfunc;

import java.util.List;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * LD商品图片复制
 * @author lgx
 *
 */
public class FuncCopyLDProductPic extends RootFunc {

	@Override
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		
		if (mResult.upFlagTrue()) {
			String from_product_code = mDataMap.get("zw_f_from_product_code");
			String to_product_code = mDataMap.get("zw_f_to_product_code");
			// 校验被复制的商品
			MDataMap fromProduct = DbUp.upTable("pc_productinfo").one("product_code",from_product_code,"small_seller_code","SI2003");
			if(null != fromProduct) {
				// 商品主图
				String mainpic_url = fromProduct.get("mainpic_url");
				// 商品图
				List<MDataMap> productpicList = DbUp.upTable("pc_productpic").queryAll("pic_url", "", "product_code='" + from_product_code + "'", null);
				// 描述图
				List<MDataMap> productdescriptionList = DbUp.upTable("pc_productdescription").queryAll("description_pic as pic_url", "", "product_code='" + from_product_code + "'", null);
				// 广告图
				List<MDataMap> productadpicList = DbUp.upTable("pc_productadpic").queryAll("pic_url", "", "product_code='" + from_product_code + "'", null);
				// 被复制的商品没有图片可复制时：提示复制失败
				if((null == mainpic_url || "".equals(mainpic_url)) && !checkIsHaveProductPic(productpicList) 
						&& !checkIsHaveProductPic(productdescriptionList) && !checkIsHaveProductPic(productadpicList)) {
					mResult.setResultCode(-1);
					mResult.setResultMessage("复制失败,没有可复制的图片!");
					return mResult;
				}else {
					// 校验被复制的商品
					MDataMap toProduct = DbUp.upTable("pc_productinfo").one("product_code",to_product_code,"small_seller_code","SI2003");
					if(null != toProduct) {
						// 商品主图
						String to_mainpic_url = toProduct.get("mainpic_url");
						// 商品图
						List<MDataMap> to_productpicList = DbUp.upTable("pc_productpic").queryAll("pic_url", "", "product_code='" + to_product_code + "'", null);
						// 描述图
						List<MDataMap> to_productdescriptionList = DbUp.upTable("pc_productdescription").queryAll("description_pic as pic_url", "", "product_code='" + to_product_code + "'", null);
						// 广告图
						List<MDataMap> to_productadpicList = DbUp.upTable("pc_productadpic").queryAll("pic_url", "", "product_code='" + to_product_code + "'", null);
						// 复制到的商品必须没有图片才能复制,有任意图片则提示复制失败,已有商品图片
						if((null == to_mainpic_url || "".equals(to_mainpic_url)) && !checkIsHaveProductPic(to_productpicList) 
								&& !checkIsHaveProductPic(to_productdescriptionList) && !checkIsHaveProductPic(to_productadpicList)) {
							// 复制商品主图
							if(null != mainpic_url && !"".equals(mainpic_url)){
								MDataMap mainpicMapParam = new MDataMap();
								mainpicMapParam.put("product_code", to_product_code);
								mainpicMapParam.put("mainpic_url", mainpic_url);
								String sql = "update pc_productinfo set mainpic_url=:mainpic_url where product_code=:product_code";
								DbUp.upTable("pc_productinfo").dataExec(sql, mainpicMapParam);
							}
							// 复制商品图
							if(checkIsHaveProductPic(productpicList)) {
								DbUp.upTable("pc_productpic").delete("product_code", to_product_code);
								for (MDataMap productpic : productpicList) {
									if(null != productpic.get("pic_url") && !"".equals(productpic.get("pic_url"))) {										
										MDataMap productpicInsertMap = new MDataMap();
										productpicInsertMap.put("product_code", to_product_code);
										productpicInsertMap.put("pic_url", productpic.get("pic_url"));
										DbUp.upTable("pc_productpic").dataInsert(productpicInsertMap);
									}
								}
							}
							// 复制描述图
							if(checkIsHaveProductPic(productdescriptionList)) {
								DbUp.upTable("pc_productdescription").delete("product_code", to_product_code);
								for (MDataMap productdescription : productdescriptionList) {
									if(null != productdescription.get("pic_url") && !"".equals(productdescription.get("pic_url"))) {										
										MDataMap productdescriptionInsertMap = new MDataMap();
										productdescriptionInsertMap.put("product_code", to_product_code);
										productdescriptionInsertMap.put("description_pic", productdescription.get("pic_url"));
										DbUp.upTable("pc_productdescription").dataInsert(productdescriptionInsertMap);
									}
								}
							}
							// 复制广告图
							if(checkIsHaveProductPic(productadpicList)) {
								DbUp.upTable("pc_productadpic").delete("product_code", to_product_code);
								for (MDataMap productadpic : productadpicList) {
									if(null != productadpic.get("pic_url") && !"".equals(productadpic.get("pic_url"))) {										
										MDataMap productadpicInsertMap = new MDataMap();
										productadpicInsertMap.put("product_code", to_product_code);
										productadpicInsertMap.put("pic_url", productadpic.get("pic_url"));
										DbUp.upTable("pc_productadpic").dataInsert(productadpicInsertMap);
									}
								}
							}
							
							mResult.setResultMessage("复制成功!");
						}else {
							mResult.setResultCode(-1);
							mResult.setResultMessage("复制失败,复制到的商品已有商品图片!");
							return mResult;
						}
					}else {
						mResult.setResultCode(-1);
						mResult.setResultMessage("您所要复制到的商品不存在!");
						return mResult;
					}
				}
				
			}else {
				mResult.setResultCode(-1);
				mResult.setResultMessage("您所复制的商品不存在!");
				return mResult;
			}
		}
		return mResult;
	}
	
	/**
	 * 检查是否有图片
	 * @param picList
	 * @return
	 */
	public boolean checkIsHaveProductPic(List<MDataMap> picList) {
		boolean flag = false; //没有图片
		if(null != picList) {
			for (MDataMap mDataMap : picList) {
				String picUrl = mDataMap.get("pic_url");
				if(null != picUrl && !"".equals(picUrl)) {
					// 有图
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

}
