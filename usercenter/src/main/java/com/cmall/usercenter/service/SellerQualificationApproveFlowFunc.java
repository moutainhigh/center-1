package com.cmall.usercenter.service;

import com.cmall.productcenter.service.ProductBrandService;
import com.cmall.systemcenter.systemface.IFlowFunc;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * com.cmall.usercenter.service.SellerQualificationApproveFlowFunc
 * @author LHY
 * 商户品牌审批流程回调函数,在erp中配置
 */
public class SellerQualificationApproveFlowFunc extends BaseClass implements IFlowFunc {

	public RootResult BeforeFlowChange(String flowCode, String outCode, String fromStatus, String toStatus, MDataMap mSubMap) {
		return null;
	}

	public RootResult afterFlowChange(String flowCode, String outCode, String fromStatus, String toStatus, MDataMap mSubMap) {
		RootResult rootResult = new RootResult();
		fromStatus = String.valueOf(fromStatus);
		toStatus = String.valueOf(toStatus);
		String sellerQualificationCode = String.valueOf(outCode);
		
		ProductBrandService service = new ProductBrandService();
		//新增待审核:4497478200010003,修改待审核,4497478200010004
		MDataMap map = DbUp.upTable("pc_seller_qualification_draftbox").oneWhere("", "", "seller_qualification_code='"+sellerQualificationCode+"' and (flow_status='4497478200010003' or flow_status='4497478200010004')");
		String flowStatus = map.get("flow_status");
		if("4497172300150003".equals(toStatus)) {//审批通过
			if("4497478200010003".equals(flowStatus)) {//新增处理
				service.addApproveSellerQualification(sellerQualificationCode, map.get("qualification_json"));
			} else if("4497478200010004".equals(flowStatus)) {//修改处理
				service.editApproveSellerQualification(sellerQualificationCode, map.get("qualification_json"));
			}
		} else if("4497172300150002".equals(toStatus)) {//审批驳回
			service.updateQualificationDraftFlowStatus(sellerQualificationCode, flowStatus, "4497478200010002", mSubMap.get("remark"),mSubMap.get("upload_show"));
		}
		return rootResult;
	}
}
