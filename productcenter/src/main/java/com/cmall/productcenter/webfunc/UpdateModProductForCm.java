package com.cmall.productcenter.webfunc;

import org.apache.commons.lang.StringUtils;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.usermodel.MUserInfo;
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
public class UpdateModProductForCm extends RootFunc {

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
				String sc = pp.getSellerCode();//商品所属店铺编号
				MUserInfo uc = UserFactory.INSTANCE.create();//当前用户所属店铺编号
				if(uc==null){
					mResult.inErrorMessage(941901065, bInfo(941901064));
				}else if(sc!=null&&!"".equals(sc)){
					pService.updateProduct(pp, error);
					if (StringUtils.isEmpty(error.toString())) {
						mResult.setResultMessage(bInfo(909101005));
					} else {
						mResult.inErrorMessage(909101006, error.toString());
					}
				}else{
					mResult.inErrorMessage(909101007);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(909101007);
		}
		return mResult;
	}
}
