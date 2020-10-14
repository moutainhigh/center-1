package com.cmall.productcenter.webfunc;

import com.cmall.systemcenter.common.DateUtil;
import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 新建子分类添加商品
 * @author lgx
 *
 */
public class NewCategoryAddProduct extends RootFunc {

	@Override
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mWebResult = new MWebResult();
		
		String product_code = mDataMap.get("zw_f_product_code");
		String category_code = mDataMap.get("zw_f_category_code");
		
		// 验证商品是否重复
		MDataMap one = DbUp.upTable("uc_sellercategory_pre_product").one("product_code",product_code,"category_code",category_code);
		if(null != one) {
			mWebResult.setResultCode(-1);
			mWebResult.setResultMessage("该分类下已经添加此商品");
			return mWebResult;
		}
		
		MDataMap insertMap = new MDataMap();
		insertMap.put("category_code", category_code);
		insertMap.put("product_code", product_code);
		insertMap.put("create_time", DateUtil.getSysDateTimeString());
		DbUp.upTable("uc_sellercategory_pre_product").dataInsert(insertMap );
		
		// 刷新solr缓存
		ProductJmsSupport productJmsSupport = new ProductJmsSupport();
		productJmsSupport.updateSolrData(product_code);
		
		return mWebResult;
	}

}
