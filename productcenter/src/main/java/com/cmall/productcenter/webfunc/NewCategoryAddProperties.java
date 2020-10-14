package com.cmall.productcenter.webfunc;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cmall.systemcenter.common.DateUtil;
import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 新建子分类添加属性值
 * @author lgx
 *
 */
public class NewCategoryAddProperties extends RootFunc {

	@Override
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mWebResult = new MWebResult();
		
		String properties_value_code = mDataMap.get("zw_f_properties_value_code");
		String category_code = mDataMap.get("zw_f_category_code");

		// 验证属性值是否重复
		MDataMap one = DbUp.upTable("uc_sellercategory_pre_properties_value").one("properties_value_code",properties_value_code,"category_code",category_code);
		if(null != one) {
			mWebResult.setResultCode(-1);
			mWebResult.setResultMessage("该分类下已经添加此属性值");
			return mWebResult;
		}
		
		MDataMap properties_value = DbUp.upTable("uc_properties_value").one("properties_value_code",properties_value_code);
		if(null == properties_value) {
			mWebResult.setResultCode(-1);
			mWebResult.setResultMessage("属性值有误");
			return mWebResult;
		}
		String properties_code = properties_value.get("properties_code");
		
		MDataMap insertMap = new MDataMap();
		insertMap.put("category_code", category_code);
		insertMap.put("properties_code", properties_code);
		insertMap.put("properties_value_code", properties_value_code);
		insertMap.put("create_time", DateUtil.getSysDateTimeString());
		DbUp.upTable("uc_sellercategory_pre_properties_value").dataInsert(insertMap );
		
		ProductJmsSupport productJmsSupport = new ProductJmsSupport();
		List<Map<String, Object>> prodList = DbUp.upTable("uc_properties_product_relation").dataSqlList("SELECT * FROM uc_properties_product_relation WHERE properties_value_code = '"+properties_value_code+"'", new MDataMap());
		if(prodList != null && prodList.size() > 0) {
			for (Map<String, Object> map : prodList) {
				String prodCode = (String) map.get("product_code");
				if(StringUtils.isNotEmpty(prodCode)) {					
					// 刷新solr缓存
					productJmsSupport.updateSolrData(prodCode);
				}
			}
		}
		
		return mWebResult;
	}

}
