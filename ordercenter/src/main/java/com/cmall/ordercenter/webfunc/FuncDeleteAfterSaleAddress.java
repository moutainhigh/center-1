package com.cmall.ordercenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfunc.FuncAdd;
import com.srnpr.zapweb.webmethod.WebMethod;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 商户删除售后地址
 * @author lgx
 *
 */
public class FuncDeleteAfterSaleAddress extends FuncAdd {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		MWebOperate mOperate = WebUp.upOperate(sOperateUid);
		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());
		MDataMap mDelMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		if (mResult.upFlagTrue()) {
			if (mDelMaps.containsKey("uid")) {
				// 售后地址uid
				String afterSaleAddressUid = mDelMaps.get("uid");
				WebMethod webMethod = new WebMethod();
				MUserInfo upUserInfo = webMethod.upUserInfo();
				// 商户id
				String smallSellerCode = upUserInfo.getManageCode();
				// 该售后地址下的商品
				int count = DbUp.upTable("pc_productinfo").dataCount(" small_seller_code='"+smallSellerCode+"'  AND after_sale_address_uid = '"+afterSaleAddressUid+"'", null);
				if(count <= 0) {
					// 该售后地址下没有商品
					DbUp.upTable("oc_address_info").delete("uid",afterSaleAddressUid);
				}else {
					int dataExec = DbUp.upTable("pc_productinfo").dataExec("UPDATE pc_productinfo SET after_sale_address_uid = '' WHERE small_seller_code ='"+smallSellerCode+"' AND after_sale_address_uid = '"+afterSaleAddressUid+"'", null);
					// 删除商品下的售后地址
					if(dataExec > 0) {
						// 删除售后地址信息
						DbUp.upTable("oc_address_info").delete("uid",afterSaleAddressUid);
					}else {
						mResult.setResultCode(-1);
						mResult.setResultMessage("删除售后地址失败");
					}
				}
			}
		}
		return mResult;
	}
}
