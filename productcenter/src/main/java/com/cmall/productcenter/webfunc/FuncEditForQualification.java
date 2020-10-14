package com.cmall.productcenter.webfunc;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmodel.PcQualificationInfo;
import com.cmall.productcenter.model.PcSellerQualification;
import com.cmall.productcenter.service.ProductBrandService;
import com.cmall.systemcenter.model.ScFlowMain;
import com.cmall.systemcenter.service.FlowService;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 提交质检修改审核
 * 
 * @author ligj
 * 
 */
public class FuncEditForQualification extends RootFunc {
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		String qualification_json = mAddMaps.get("qualification_json");
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
				
				//判断流程状态是否已经存在
				//判读当前的审批流程是否存在。
				FlowService fs = new FlowService();
				ScFlowMain sfm = fs.getApprovalFlowByOurterCode(sellerQualification.getSellerQualificationCode());
				if(sfm != null){
					mResult.inErrorMessage(941901141);
					return mResult;
				}
				
			}
			if (null!=sellerQualification && StringUtils.isNotBlank(sellerQualification.getSmallSellerCode())) {
				for (PcQualificationInfo pcQualificationInfo : sellerQualification.getQualificationList()) {
					pcQualificationInfo.setSellerQualificationCode(sellerQualification.getSellerQualificationCode());
					pcQualificationInfo.setSmallSellerCode(sellerQualification.getSmallSellerCode());
					
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
				sellerQualification.setBrandCode(mAddMaps.get("brand_code"));
				sellerQualification.setCategoryCode(mAddMaps.get("category_code"));
				sellerQualification.setQualificationName(pcQualification.getQualificationName());
				sellerQualification.setEndTime(pcQualification.getEndTime());

				int repeatCheck = productBrandService.qualificationRepeatCheck(sellerQualification);
				if (repeatCheck > 0) {
					mResult.inErrorMessage(941901142);
					return mResult;
				}
				
				//插入到草稿箱表中
				productBrandService.insertPualificationDraft(sellerQualification,"4497478200010004","");
				
//				productBrandService.updatePualification(new JsonHelper<PcSellerQualification>().ObjToString(sellerQualification));
				
				//加入审批的流程
				ScFlowMain flow = new ScFlowMain();
				flow.setCreator(userCode);
				flow.setCurrentStatus("4497172300150001");
				flow.setFlowType("449717230015");
				flow.setFlowTitle(sellerQualification.getSmallSellerCode());
				flow.setFlowUrl("");
				flow.setOuterCode(sellerQualification.getSellerQualificationCode());
				String title = bInfo(941901140,sellerQualification.getSmallSellerCode());
				flow.setFlowRemark(title);
				flow.setNext_operator_id("");
				new FlowService().CreateFlow(flow);

			}
		}

		if (mResult.upFlagTrue()) {
			mResult.setResultMessage(bInfo(969909001));
		}
		return mResult;

	}
}
