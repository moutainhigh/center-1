package com.cmall.productcenter.webfunc;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncModSkuinfo extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		ProductService service = new ProductService();
//		ProductSkuInfo info = new ProductSkuInfo();
//		info.setUid(mAddMaps.get("uid"));
//		info.setSkuName(mAddMaps.get("sku_name"));
//		info.setSecurityStockNum(Integer.valueOf(mAddMaps.get("security_stock_num")));
//		info.setSkuPicUrl(mAddMaps.get("sku_picurl"));
//		info.setSellProductcode(mAddMaps.get("sell_productcode"));
//		info.setSkuAdv(mAddMaps.get("sku_adv"));
//		String sc = mAddMaps.get("seller_code");
//		String sellerCode = UserFactory.INSTANCE.create().getManageCode(); 
//		if(sellerCode!=null&&!"".equals(sellerCode)&&sc!=null&&!"".equals(sc)&&!sellerCode.equals(sc)){
//			mResult.inErrorMessage(941901061, bInfo(941901064));
//		}else {
//			RootResult rootResult = service.updateSkuOther(info);
//			if(StringUtils.isEmpty(rootResult.getResultMessage())){
//				mResult.setResultMessage(bInfo(941901060));
//			}else {
//				mResult.inErrorMessage(941901061, rootResult.getResultMessage());
//			}
//		}
		PcProductinfo pro = service.getProduct(mAddMaps.get("product_code")+"_1");
		for (int i = 0; i < pro.getProductSkuInfoList().size(); i++) {
			ProductSkuInfo psku= pro.getProductSkuInfoList().get(i);
			if(mAddMaps.get("sku_code").equals(psku.getSkuCode())){
				pro.getProductSkuInfoList().get(i).setSkuName(mAddMaps.get("sku_name"));
				pro.getProductSkuInfoList().get(i).setSellPrice(new BigDecimal(mAddMaps.get("sell_price")));
				pro.getProductSkuInfoList().get(i).setSecurityStockNum(Integer.valueOf(mAddMaps.get("security_stock_num")));
				pro.getProductSkuInfoList().get(i).setSkuPicUrl(mAddMaps.get("sku_picurl"));
				pro.getProductSkuInfoList().get(i).setSellProductcode(mAddMaps.get("sell_productcode"));
				pro.getProductSkuInfoList().get(i).setSkuAdv(mAddMaps.get("sku_adv"));
				break;
			}
		}
		StringBuffer error = new StringBuffer();
		service.updateProductForCshop(pro, error);
		if (StringUtils.isEmpty(error.toString())) {
			mResult.setResultMessage(bInfo(941901060));
		} else {
			mResult.inErrorMessage(941901061, error.toString());
		}
		return mResult;
	}

}
