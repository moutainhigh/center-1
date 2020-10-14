package com.cmall.productcenter.webfunc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class AddProductSkuBatch extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		try {

			if (mResult.upFlagTrue()) {

				ProductService pService = new ProductService();

				MDataMap mSubDataMap = mDataMap
						.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

				PcProductinfo pcProductinfo = new PcProductinfo();
				pcProductinfo = new JsonHelper<PcProductinfo>().StringToObj(
						mSubDataMap.get("json"), pcProductinfo);
				
				StringBuffer error = new StringBuffer();
				List<String> skuKey = new ArrayList<String>();
				List<String> skuKeyValue = new ArrayList<String>();
				if (pcProductinfo.getProductSkuInfoList() != null && pcProductinfo.getProductSkuInfoList().size()>0) {
					for (int i = 0; i < pcProductinfo.getProductSkuInfoList().size(); i++) {
						ProductSkuInfo sku = pcProductinfo.getProductSkuInfoList().get(i);
						skuKey.add(sku.getSkuKey());
						skuKeyValue.add(sku.getSkuKeyvalue());
					}
				}else{
					//没有sku时提示：请维护上sku信息后重试！
					mResult.inErrorMessage(941901131);
					return mResult;
				}
				//检查sku规格是否存在重复
				if (1 == new ProductService().checkRepeatSku(skuKey, skuKeyValue)) {
					mResult.inErrorMessage(941901124);
					return mResult;
				}
				
				PcProductinfo pro = pService.getProduct(mSubDataMap.get("product_code")+"_1");
				if (null == pro || StringUtils.isBlank(pro.getProductCode())) {
					pro = pService.getProduct(mSubDataMap.get("product_code"));
				}
				//防止添加进去已经存在的sku的信息，在此过滤一遍
				List<ProductSkuInfo> addSku = new ArrayList<ProductSkuInfo>();
				for (ProductSkuInfo skuNew : pcProductinfo.getProductSkuInfoList()) {
					boolean flagRepeat = false;
					for (ProductSkuInfo skuOld : pro.getProductSkuInfoList()) {
						if (skuNew.getSkuKey().equals(skuOld.getSkuKey())) {
							flagRepeat = true;
							break;
						}
					}
					if (!flagRepeat) {
						addSku.add(skuNew);
					}
				}
				if (addSku.size()>0) {
					pro.getProductSkuInfoList().addAll(addSku);
					pService.addSkuBatch(pro, error);
				}else{
					mResult.inErrorMessage(941901059,"未知原因。请刷新后重试或联系技术人员！");
					return mResult;
				}
				
				if (StringUtils.isEmpty(error.toString())) {
					mResult.setResultMessage(bInfo(941901058));

				} else {
					mResult.inErrorMessage(941901059, error.toString());
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(941901059,"未知原因。请联系技术人员！");
		}

		return mResult;

	}

}
