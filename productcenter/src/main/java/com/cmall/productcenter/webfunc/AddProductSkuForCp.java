package com.cmall.productcenter.webfunc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class AddProductSkuForCp extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		try {

			if (mResult.upFlagTrue()) {
				ProductService pService = new ProductService();
				ProductSkuInfo skuinfo = new ProductSkuInfo();
				MDataMap mSubDataMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
				MDataMap qu = new MDataMap();
				qu.put("product_code", mSubDataMap.get("productCode"));
				List<Map<String, Object>> list = DbUp.upTable("pc_productinfo").dataQuery("seller_code", "", "", qu, 0, 0);
				if(!list.isEmpty()&&list.size()>0){
					skuinfo.setSellerCode(list.get(0).get("seller_code").toString());
				}
				skuinfo.setProductCode(mSubDataMap.get("productCode"));
				skuinfo.setSellPrice(new BigDecimal(mSubDataMap.get("sellPrice")));
				skuinfo.setStockNum(Integer.valueOf(mSubDataMap.get("stockNum")));
				skuinfo.setSkuKey(mSubDataMap.get("skuKey"));
				skuinfo.setSkuValue(mSubDataMap.get("skuValue"));
				skuinfo.setSkuPicUrl(mSubDataMap.get("skuPicUrl"));
				skuinfo.setSellProductcode(mSubDataMap.get("sellProductcode"));
				skuinfo.setSecurityStockNum(Integer.valueOf(mSubDataMap.get("securityStockNum")));
				skuinfo.setSkuAdv(mSubDataMap.get("skuAdv"));
				RootResult root = pService.addSku(skuinfo);
				if (StringUtils.isEmpty(root.getResultMessage())) {
//					mResult.setResultMessage(bInfo(941901058));
					mResult.setResultType("116018010");
					mResult.setResultObject("clolseAndrefresh()"); 
				} else {
					mResult.inErrorMessage(941901059, root.getResultMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(941901059);
		}
		return mResult;

	}

}
