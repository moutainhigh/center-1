package com.cmall.usercenter.webfunc;

import org.apache.commons.lang.StringUtils;

import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.cmall.usercenter.txservice.TxSellercategoryService;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.xmassystem.load.LoadProductInfo;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/***
 * 添加商品与分类的映射关系
 * @author jlin
 *
 */
public class FunProductCategoryAdd extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		String app_code = UserFactory.INSTANCE.create().getManageCode();
		
		MWebResult mResult = new MWebResult();
		/**获取提交新增时页面相关属性的值*/
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		String sellerCode = mAddMaps.get("seller_code");
		if(StringUtils.isNotEmpty(sellerCode)){
			app_code = sellerCode;
		}
		String category_codes[]= new String [0];
		
		if(StringUtils.isNotBlank(mAddMaps.get("category_code"))){
			category_codes =mAddMaps.get("category_code").split(",");
		}
		//判断提交时候是否有非四级分类
		for(String code:category_codes) {
			if(code!=null&&code.length()!=20) {
				mResult.setResultCode(0);
				mResult.setResultMessage("请将商品所属分类设置为第四层级");
				return mResult;
			}
		}
		
		String product_code=mAddMaps.get("product_code");
		//判断商品是否在流程审批中，如果是，不允许修改
		MDataMap one = DbUp.upTable("sc_flow_main").one("outer_code",product_code,"flow_isend","0");
		if(one!=null) {
			mResult.setResultCode(0);
			mResult.setResultMessage("当前商品在审核过程中,不能在此处修改商品分类");
			return mResult;
		}
		
		
		TxSellercategoryService sellercategoryService = BeansHelper.upBean("com_cmall_usercenter_txservice_TxSellercategoryService");
		sellercategoryService.updateSellercategoryProductRelation(product_code, category_codes,app_code);
		new LoadProductInfo().deleteInfoByCode(product_code);
		
		PlusHelperNotice.onChangeProductInfo(product_code);
		//触发消息队列
		new ProductJmsSupport().onChangeForProductChangeAll(product_code);
		//更新分类商品数量表
		XmasKv.upFactory(EKvSchema.IsUpdateCategoryProductCount).set("isUpdateCateProd","update");
		return mResult;
	}
}
