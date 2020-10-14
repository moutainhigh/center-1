package com.cmall.productcenter.webfunc;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncModSkuiPriceStocknfo extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		ProductService service = new ProductService();
		ProductSkuInfo info = new ProductSkuInfo();
		info.setUid(mAddMaps.get("uid"));
		if(mAddMaps.get("stock_num")==null||"".equals(mAddMaps.get("stock_num"))||"0".equals(mAddMaps.get("stock_num"))){
			info.setStockNum(0);
		}else if("0".equals(mAddMaps.get("option"))){	//增加库存
			info.setStockNum(0-Integer.valueOf(mAddMaps.get("stock_num")));
		}else if("1".equals(mAddMaps.get("option"))){	//减少库存
			info.setStockNum((Integer.valueOf(mAddMaps.get("stock_num"))));
		}
		String sc = mAddMaps.get("seller_code");
		MUserInfo userInfo = UserFactory.INSTANCE.create();
//		if(null == userInfo || StringUtils.isEmpty(userInfo.getManageCode()) || StringUtils.isEmpty(sc) || !userInfo.getManageCode().startsWith("SF03")){
		/**
		 * 修改商户编码判断条件 2016-12-02 zhy
		 */
		String seller_type = WebHelper.getSellerType(userInfo.getManageCode());
		if(null == userInfo || StringUtils.isEmpty(userInfo.getManageCode()) || StringUtils.isEmpty(sc) || StringUtils.isBlank(seller_type)){
			mResult.inErrorMessage(941901061, bInfo(941901064));
		}else {
			RootResult rootResult = service.changeProductSkuStockForCshop(info,"");
			if(StringUtils.isEmpty(rootResult.getResultMessage())){
				mResult.setResultMessage(bInfo(941901060));
			}else {
				mResult.inErrorMessage(941901061, rootResult.getResultMessage());
			}
		}
		return mResult;
	}

}
