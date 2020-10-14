package com.cmall.productcenter.webfunc;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.service.ProductDraftBoxService;
import com.cmall.systemcenter.common.AppConst;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 *添加商品信息到草稿箱
 *
 *@author ligj
 *@version 1.0 
 * 
 */
public class AddDraftboxProduct extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();
		MDataMap sellerInfoExt = DbUp.upTable("uc_seller_info_extend").one("small_seller_code", UserFactory.INSTANCE.create().getManageCode());
		try {
			if (mResult.upFlagTrue()) {
				ProductDraftBoxService pdService = new ProductDraftBoxService();
				MDataMap mSubDataMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
				PcProductinfo pp = new PcProductinfo();
				String productJson = mSubDataMap.get("json");
				pp = new JsonHelper<PcProductinfo>().StringToObj(
						productJson, pp);
				pp.setLabels(mDataMap.get("zw_f_labels"));
				
				// 商品保障必须勾选
				if(StringUtils.isBlank(mSubDataMap.get("authority_logo"))){
					mResult.inErrorMessage(941901145);
					return mResult; 
				}
				
				// 支持/不支持7日无理由退货不能同时勾选
				if(StringUtils.isNotBlank(mSubDataMap.get("authority_logo"))
						&& mSubDataMap.get("authority_logo").contains(bConfig("productcenter.authority_logo_sevenday"))
						&& mSubDataMap.get("authority_logo").contains(bConfig("productcenter.authority_logo_sevenday_no"))){
					String msg1 = DbUp.upTable("pc_authority_logo").one("uid",bConfig("productcenter.authority_logo_sevenday")).get("logo_content");
					String msg2 = DbUp.upTable("pc_authority_logo").one("uid",bConfig("productcenter.authority_logo_sevenday_no")).get("logo_content");
					mResult.inErrorMessage(941901146,msg1,msg2);
					return mResult; 
				}
				
				// 配送仓库类别默认以当前商户的为准
				pp.getPcProductinfoExt().setDeliveryStoreType(sellerInfoExt.get("delivery_store_type"));
				
				StringBuffer error = new StringBuffer();
				MUserInfo userInfo = null;
				if (UserFactory.INSTANCE != null) {
					try {
						userInfo = UserFactory.INSTANCE.create();
					} catch (Exception e) {
						userInfo = new MUserInfo();
					}
				}
				pp.setSellerCode(AppConst.MANAGE_CODE_HOMEHAS);
				pp.setSmallSellerCode(userInfo.getManageCode());
				pdService.addProductToDraftBox(pp,error);
				if (StringUtils.isEmpty(error.toString())) {
					mResult.setResultMessage(bInfo(909701002));
				} else {
					mResult.inErrorMessage(909701003, error.toString());
				}
				
				if(StringUtils.isNotBlank(pp.getProductCode())){
					// 商品保障
					DbUp.upTable("pc_product_authority_logo").delete("product_code",pp.getProductCode());
					if(StringUtils.isNotBlank(mSubDataMap.get("authority_logo"))){
						String[] vs = mSubDataMap.get("authority_logo").split(",");
						for(String v : vs){
							if(StringUtils.isBlank(v)) continue;
							MDataMap data = new MDataMap("product_code",pp.getProductCode(),"authority_logo_uid",v,"create_time",FormatHelper.upDateTime());
							DbUp.upTable("pc_product_authority_logo").dataInsert(data);
						}
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
