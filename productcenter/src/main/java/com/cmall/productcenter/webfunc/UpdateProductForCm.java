package com.cmall.productcenter.webfunc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.PcProductdescription;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.PcProductpic;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 *修改商品
 *
 *@author jack
 *@version 1.0 
 * 
 */
public class UpdateProductForCm extends RootFunc {

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
				//System.out.println(pService.updateProduct(pp, error));
				if (StringUtils.isEmpty(error.toString())) {
					mResult.setResultMessage(bInfo(909101005));
				} else {
					mResult.inErrorMessage(909101006, error.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(909101007);
		}
		return mResult;
	}
}
