package com.cmall.productcenter.webfunc;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.service.ProductService;
import com.cmall.systemcenter.common.AppConst;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncModSkuinfoForCd extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		ProductService service = new ProductService();
		MUserInfo sys = UserFactory.INSTANCE.create(); 
		if(sys==null){
			mResult.inErrorMessage(941901061, bInfo(941901064));
		}else {
			
			String barcode = mAddMaps.get("barcode");
			if (StringUtils.isNotBlank(barcode)) {
				if (DbUp.upTable("pc_skuinfo").dataCount("barcode='"+barcode+"' and seller_code = '"+AppConst.MANAGE_CODE_CDOG+"' and uid != '"+mAddMaps.get("uid")+"'",null) > 0) {
					mResult.inErrorMessage(941901061, bInfo(941901116));
					return mResult;
				}
			}
			String productCode = DbUp.upTable("pc_skuinfo").one("uid",mAddMaps.get("uid")).get("product_code");
			PcProductinfo pro = service.getProduct(productCode);
			for (int i = 0; i < pro.getProductSkuInfoList().size(); i++) {
				ProductSkuInfo psku= pro.getProductSkuInfoList().get(i);
				if(mAddMaps.get("sku_code").equals(psku.getSkuCode())){
					pro.getProductSkuInfoList().get(i).setSkuName(mAddMaps.get("sku_name"));
					pro.getProductSkuInfoList().get(i).setSecurityStockNum(Integer.valueOf(mAddMaps.get("security_stock_num")));
					pro.getProductSkuInfoList().get(i).setSkuPicUrl(mAddMaps.get("sku_picurl"));
					pro.getProductSkuInfoList().get(i).setSellProductcode(mAddMaps.get("sell_productcode"));
					pro.getProductSkuInfoList().get(i).setSkuAdv(mAddMaps.get("sku_adv"));
					pro.getProductSkuInfoList().get(i).setBarcode(barcode);
					pro.getProductSkuInfoList().get(i).setSaleYn(mAddMaps.get("sale_yn"));
					break;
				}
			}
			StringBuffer error = new StringBuffer();
			service.updateProduct(pro,error);
			if(StringUtils.isEmpty(error.toString())){
				mResult.setResultMessage(bInfo(941901060));
			}else {
				mResult.inErrorMessage(941901061, error.toString());
			}
		}
		
		return mResult;
	}

}
