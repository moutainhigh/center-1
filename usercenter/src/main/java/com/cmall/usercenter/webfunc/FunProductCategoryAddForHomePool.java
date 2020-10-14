package com.cmall.usercenter.webfunc;

import org.apache.commons.lang.StringUtils;

import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.usercenter.txservice.TxSellercategoryService;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/***
 * 添加商品与分类的映射关系
 * @author jlin
 *
 */
public class FunProductCategoryAddForHomePool extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();
		/**获取提交新增时页面相关属性的值*/
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		String category_codes[]= new String [0];
		
		if(StringUtils.isNotBlank(mAddMaps.get("category_code"))){
			category_codes =mAddMaps.get("category_code").split(",");
		}
		
		String product_code=mAddMaps.get("product_code");
		
		TxSellercategoryService sellercategoryService = BeansHelper.upBean("com_cmall_usercenter_txservice_TxSellercategoryService");
		sellercategoryService.updateSellercategoryProductRelation(product_code, category_codes,MemberConst.MANAGE_CODE_HPOOL);
		
		return mResult;
	}
}
