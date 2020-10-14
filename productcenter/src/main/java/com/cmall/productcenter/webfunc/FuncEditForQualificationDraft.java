package com.cmall.productcenter.webfunc;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmodel.PcQualificationInfo;
import com.cmall.productcenter.model.PcSellerQualification;
import com.cmall.productcenter.service.ProductBrandService;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 修改品牌资质草稿箱
 * 
 * @author ligj
 * 
 */
public class FuncEditForQualificationDraft extends RootFunc {
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();
		MDataMap mEditMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		String qualification_json = mEditMaps.get("qualification_json");
		PcSellerQualification sellerQualification = new PcSellerQualification();
		JsonHelper<PcSellerQualification> pHelper = new JsonHelper<PcSellerQualification>();
		ProductBrandService productBrandService = new ProductBrandService();
//		String smallSellerCode = "";
		String userCode = "";
		if (mResult.upFlagTrue()) {
			MUserInfo mUserInfo = UserFactory.INSTANCE.create();
			if (null != mUserInfo) {
//				smallSellerCode = mUserInfo.getManageCode();
				userCode = mUserInfo.getUserCode();
			}else{
				mResult.inErrorMessage(969905305);
				return mResult;
			}
		}
		if (mResult.upFlagTrue()) {
			if (StringUtils.isNotBlank(qualification_json)) {
				sellerQualification = pHelper.StringToObj(qualification_json, sellerQualification);
			}
			if (null!=sellerQualification && StringUtils.isNotBlank(sellerQualification.getSmallSellerCode())) {
				for (PcQualificationInfo pcQualificationInfo : sellerQualification.getQualificationList()) {
					pcQualificationInfo.setSellerQualificationCode(sellerQualification.getSellerQualificationCode());
					pcQualificationInfo.setSmallSellerCode(sellerQualification.getSmallSellerCode());
				}
				//取出最早过期的资质
				PcQualificationInfo pcQualification = productBrandService.getMinEndTime(sellerQualification.getQualificationList());
				
				sellerQualification.setBrandCode(mEditMaps.get("brand_code"));
				sellerQualification.setCategoryCode(mEditMaps.get("category_code"));
				sellerQualification.setQualificationName(pcQualification.getQualificationName());
				sellerQualification.setEndTime(pcQualification.getEndTime());
				//更新草稿箱表
				productBrandService.updatePualificationDraft(sellerQualification,mEditMaps.get("uid"));

			}
		}

		if (mResult.upFlagTrue()) {
			mResult.setResultMessage(bInfo(969909001));
		}
		return mResult;

	}
}
