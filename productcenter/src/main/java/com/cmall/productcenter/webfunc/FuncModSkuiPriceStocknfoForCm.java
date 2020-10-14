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

public class FuncModSkuiPriceStocknfoForCm extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		ProductService service = new ProductService();
		ProductSkuInfo info = new ProductSkuInfo();
		info.setUid(mAddMaps.get("uid"));
		info.setSellPrice(new BigDecimal(mAddMaps.get("sell_price")));
		if(mAddMaps.get("stock_num")==null||"".equals(mAddMaps.get("stock_num"))||"0".equals(mAddMaps.get("stock_num"))){
			info.setStockNum(0);
		}else if("0".equals(mAddMaps.get("option"))){
			info.setStockNum(Integer.valueOf(mAddMaps.get("stock_num")));
		}else if("1".equals(mAddMaps.get("option"))){
			info.setStockNum((0-Integer.valueOf(mAddMaps.get("stock_num"))));
		}
		MUserInfo sys = UserFactory.INSTANCE.create();
		if(sys==null){
			mResult.inErrorMessage(941901061, bInfo(941901064));
		}else {
			RootResult rootResult = service.changeProductSkuStock(info,"");
			if(StringUtils.isEmpty(rootResult.getResultMessage())){
				mResult.setResultMessage(bInfo(941901060));
			}else {
				mResult.inErrorMessage(941901061, rootResult.getResultMessage());
			}
		}
		return mResult;
	}

}
