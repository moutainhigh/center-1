package com.cmall.productcenter.webfunc;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmodel.PcQualificationInfo;
import com.cmall.productcenter.model.PcSellerQualification;
import com.cmall.productcenter.service.ProductBrandService;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 质检保存草稿箱
 * 
 * @author ligj
 * 
 */
public class FuncAddForQualificationDraft extends RootFunc {
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		String qualification_json = mAddMaps.get("qualification_json");
		PcSellerQualification sellerQualification = new PcSellerQualification();
		JsonHelper<PcSellerQualification> pHelper = new JsonHelper<PcSellerQualification>();
		ProductBrandService productBrandService = new ProductBrandService();
		String smallSellerCode = "";
		if (mResult.upFlagTrue()) {
			MUserInfo mUserInfo = UserFactory.INSTANCE.create();
			if (null != mUserInfo) {
				smallSellerCode = mUserInfo.getManageCode();
			}else{
				mResult.inErrorMessage(969905305);
				return mResult;
			}
		}
		if (mResult.upFlagTrue()) {
			if (StringUtils.isNotBlank(qualification_json)) {
				sellerQualification = pHelper.StringToObj(qualification_json, sellerQualification);
			}
			if (null!=sellerQualification && StringUtils.isNotBlank(smallSellerCode)) {
				String sellerQualificationCode = WebHelper.upCode("SQ");
				for (PcQualificationInfo pcQualificationInfo : sellerQualification.getQualificationList()) {
					pcQualificationInfo.setSellerQualificationCode(sellerQualificationCode);
					pcQualificationInfo.setSmallSellerCode(smallSellerCode);
					
					//判断是否有空的资质名称
					if (StringUtils.isBlank(pcQualificationInfo.getQualificationName())) {
						mResult.inErrorMessage(941901138);
						return mResult;
					}
					//判断是否有重复的资质名称
					int count = 0 ;
					for (PcQualificationInfo pcQualificationInfoCheck : sellerQualification.getQualificationList()) {
						if (pcQualificationInfoCheck.getQualificationName().equals(pcQualificationInfo.getQualificationName())) {
							count ++;
							if (count > 1) {
								mResult.inErrorMessage(941901137,pcQualificationInfoCheck.getQualificationName());
								return mResult;
							}
						}
					}
					
				}
				//取出最早过期的资质
				PcQualificationInfo pcQualification = productBrandService.getMinEndTime(sellerQualification.getQualificationList());
				
				sellerQualification.setSmallSellerCode(smallSellerCode);
				sellerQualification.setSellerQualificationCode(sellerQualificationCode);
				sellerQualification.setBrandCode(mAddMaps.get("brand_code"));
				sellerQualification.setCategoryCode(mAddMaps.get("category_code"));
				sellerQualification.setQualificationName(pcQualification.getQualificationName());
				sellerQualification.setEndTime(pcQualification.getEndTime());
				//插入到草稿箱表中
				productBrandService.insertPualificationDraft(sellerQualification,"4497478200010001","");
			}
		}

		if (mResult.upFlagTrue()) {
			mResult.setResultMessage(bInfo(969909001));
		}
		return mResult;

	}
}
