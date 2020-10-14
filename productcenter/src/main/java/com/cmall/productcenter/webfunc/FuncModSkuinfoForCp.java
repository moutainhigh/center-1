package com.cmall.productcenter.webfunc;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncModSkuinfoForCp extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		ProductService service = new ProductService();
		ProductSkuInfo info = new ProductSkuInfo();
		info.setUid(mAddMaps.get("uid"));
		info.setSkuName(mAddMaps.get("sku_name"));
		//市场价--add by ligj
//		info.setMarketPrice(new BigDecimal(Double.valueOf(mAddMaps.get("market_price"))));/////////////////////////////////////////////前端没有传入市场价，所以把这行给注掉，免得 null
		info.setSecurityStockNum(Integer.valueOf(mAddMaps.get("security_stock_num")));
		info.setSkuPicUrl(mAddMaps.get("sku_picurl"));
		info.setSellProductcode(mAddMaps.get("sell_productcode"));
		info.setSkuAdv(mAddMaps.get("sku_adv"));
		MUserInfo sys = UserFactory.INSTANCE.create(); 
		if(sys==null){
			mResult.inErrorMessage(941901061, bInfo(941901064));
		}else {
			RootResult rootResult = service.updateSkuOther(info);
			if(StringUtils.isEmpty(rootResult.getResultMessage())){
				mResult.setResultMessage(bInfo(941901060));
			}else {
				mResult.inErrorMessage(941901061, rootResult.getResultMessage());
			}
		}
		
		return mResult;
	}

}
