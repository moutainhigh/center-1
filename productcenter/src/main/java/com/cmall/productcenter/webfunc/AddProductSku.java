package com.cmall.productcenter.webfunc;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class AddProductSku extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		try {

			if (mResult.upFlagTrue()) {
				ProductService pService = new ProductService();
				MDataMap mSubDataMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
				
				//检查sku规格重复
				int flag = pService.checkRepeatSku(mSubDataMap.get("productCode"), mSubDataMap.get("skuKey"),mSubDataMap.get("skuValue"),null, null,null, null);
				if (1 == flag) {	//sku规格重复
					mResult.inErrorMessage(941901111);
					return mResult;
				}else if (2 == flag) {
					mResult.inErrorMessage(941901117);
					return mResult;
				}
				
				ProductSkuInfo skuinfo = new ProductSkuInfo();
				//商户后台添加sku时需要先默认添加一个skuCode，终审时会置为空重新生成skuCode,执行新增流程（TxProductService.updateProduct()）
				skuinfo.setSkuCode(WebHelper.upCode("WSP"));		//为审批使用
				
				skuinfo.setProductCode(mSubDataMap.get("productCode"));
				skuinfo.setSellPrice(new BigDecimal(mSubDataMap.get("sellPrice")));
				skuinfo.setStockNum(Integer.valueOf(mSubDataMap.get("stockNum")));
				skuinfo.setSkuKey(mSubDataMap.get("skuKey"));
				skuinfo.setSkuValue(mSubDataMap.get("skuValue"));
				skuinfo.setSkuPicUrl(mSubDataMap.get("skuPicUrl"));
				skuinfo.setSellProductcode(mSubDataMap.get("sellProductcode"));
				skuinfo.setSecurityStockNum(Integer.valueOf(mSubDataMap.get("securityStockNum")));
				skuinfo.setSellerCode(mSubDataMap.get("sellerCode"));
				skuinfo.setSkuAdv(mSubDataMap.get("skuAdv"));
//				RootResult root = pService.addSku(skuinfo);
				String[] skuValueAry = skuinfo.getSkuValue().split("&");
				PcProductinfo pro = pService.getProduct(mSubDataMap.get("productCode")+"_1");
				String skuName = pro.getProductName();
				for(int  i=0;i<skuValueAry.length;i++){
					skuName +=" " +skuValueAry[i].split("=")[1];
				}
				skuinfo.setSkuName(skuName);
				//sku图片为空时默认设置为商品主图
				if(skuinfo.getSkuPicUrl() == null || skuinfo.getSkuPicUrl().equals("")){
					skuinfo.setSkuPicUrl(pro.getMainPicUrl());
				}
				pro.getProductSkuInfoList().add(skuinfo);
				
				StringBuffer error = new StringBuffer();
				pService.updateProductForCshop(pro, error);
				if (StringUtils.isEmpty(error.toString())) {
//					mResult.setResultMessage(bInfo(941901058));
					mResult.setResultType("116018010");
					mResult.setResultObject("clolseAndrefresh()"); 
				} else {
					mResult.inErrorMessage(941901059, error.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(941901059);
		}
		return mResult;

	}

}
