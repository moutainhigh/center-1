package com.cmall.productcenter.webfunc;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 *修改商品
 *
 *@author jack
 *@version 1.0 
 * 
 */
public class UpdateModProduct extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();
		try {
			if (mResult.upFlagTrue()) {
				ProductService pService = new ProductService();
				MDataMap mSubDataMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
				PcProductinfo pp = new PcProductinfo();
				pp = new JsonHelper<PcProductinfo>().StringToObj(
						mSubDataMap.get("json"), pp);
				StringBuffer error = new StringBuffer();
				String sc = pp.getSellerCode();
				String msc = pp.getSmallSellerCode();
				String sellerCode = UserFactory.INSTANCE.create().getManageCode(); 
				if(sellerCode!=null&&!"".equals(sellerCode)&&sc!=null&&!"".equals(sc)&&msc!=null&&!"".equals(msc)&&!sellerCode.equals(msc)){
					mResult.inErrorMessage(941901065, bInfo(941901064));
				}else {
					PcProductinfo pro = pService.getProduct(pp.getProductCode());
					pp.getProductSkuInfoList().clear();
					pp.setProductSkuInfoList(pro.getProductSkuInfoList());
					pService.updateProductForCshop(pp, error);
					if (StringUtils.isEmpty(error.toString())) {
						mResult.setResultMessage(bInfo(909701005));
					} else {
						mResult.inErrorMessage(909701006, error.toString());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(909701007);
		}
		return mResult;
	}
}
